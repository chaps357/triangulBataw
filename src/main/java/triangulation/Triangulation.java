package triangulation;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Sets;
import io.swagger.client.model.Pair;

import javax.annotation.Nullable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

public class Triangulation {


    public void trianguleBataw(List<Pair> pairs) {
        Map<String, Set<Trade>> trades = listAllTrades(pairs);
        Set<Map.Entry<String, Set<Trade>>> entries = trades.entrySet();
        List<LinkedList<Trade>> totalPaths = new ArrayList<>();
        for (Map.Entry<String, Set<Trade>> entry : entries) {
            String originCypto = entry.getKey();
            List<LinkedList<Trade>> paths = findPaths(originCypto, 1, trades);
            totalPaths.addAll(paths);
        }
        return;
    }

    private List<LinkedList<Trade>> findPaths(String originCypto, int requestedLevel, Map<String, Set<Trade>> trades) {
        List<LinkedList<Trade>> paths = new ArrayList<>();
        int level = 0;
        Set<Trade> cryptoTrades = trades.get(originCypto);
        for (Trade trade : cryptoTrades) {
            LinkedList<Trade> newPath = new LinkedList<>();
            newPath.add(trade);
            paths = findLoops(newPath, level, requestedLevel, trades, originCypto);
        }
        return paths;
    }

    private List<LinkedList<Trade>> findLoops(LinkedList<Trade> path, int level, int requestedLevel, Map<String,
            Set<Trade>> trades, final String originCypto) {
        List<LinkedList<Trade>> paths = new ArrayList<>();

        String lastCrypto = path.getLast().getTarget();
        Set<Trade> nextPossibleTrades = trades.get(lastCrypto);
        if (level >= requestedLevel) {
            //on essaye de revenir sur la crypto d'origine
            Set<Trade> filter = Sets.filter(nextPossibleTrades, new Predicate<Trade>() {
                @Override
                public boolean apply(@Nullable Trade trade) {
                    return trade.getTarget().equals(originCypto);
                }
            });
            if (filter.size() > 1) {
                throw new RuntimeException("IMPOSSIBLE! Many trades leading to same target!");
            }
            if (filter.size() == 1) {
                Trade finalTrade = filter.iterator().next();
                path.add(finalTrade);
                paths.add(path);
                return paths;
            }
        }
        level++;
        for (Trade newTrade : nextPossibleTrades) {
            Collection<Trade> filter = Collections2.filter(path, new Predicate<Trade>() {
                @Override
                public boolean apply(@Nullable Trade trade) {
                    return trade.getTarget().equals(newTrade.getTarget());
                }
            });
            if(!filter.isEmpty()){
                continue;
            }else {
                LinkedList<Trade> newPath = new LinkedList<>();
                newPath.addAll(path);
                newPath.add(newTrade);
                paths.addAll(findLoops(newPath, level, requestedLevel, trades, originCypto));
            }
        }
        return paths;
    }

    private Map<String, Set<Trade>> listAllTrades(List<Pair> pairs) {
        Map<String, Set<Trade>> trades = new HashMap<>();
        for (Pair pair : pairs) {
            String pairSymbol = pair.getSymbol();
            if (pairSymbol.equals("123456")) {
                continue;
            }
            String masterCrypto = findMasterCrypto(pairSymbol);
            String crypto = pairSymbol.replaceAll(masterCrypto, "");
            //il faut créer un trade pour la crypto et un pour la master avec le prix inversé
            Trade cryptoTrade = new Trade(crypto, masterCrypto, pair.getPrice());
            Trade masterTrade = new Trade(masterCrypto, crypto, BigDecimal.ONE.divide(pair.getPrice(), 6,
                    RoundingMode.HALF_EVEN));

            addNewTrade(trades, crypto, cryptoTrade);
            addNewTrade(trades, masterCrypto, masterTrade);
        }
        return trades;
    }

    private void addNewTrade(Map<String, Set<Trade>> trades, String crypto, Trade trade) {
        if (trades.get(crypto) == null) {
            HashSet<Trade> tradeSet = new HashSet<>();
            tradeSet.add(trade);
            trades.put(crypto, tradeSet);
        } else {
            trades.get(crypto).add(trade);
        }
    }

    private String findMasterCrypto(String pairSymbole) {
        if (pairSymbole.matches(".*USDT")) {
            return "USDT";
        } else if (pairSymbole.matches(".*BTC")) {
            return "BTC";
        } else if (pairSymbole.matches(".*ETH")) {
            return "ETH";
        } else if (pairSymbole.matches(".*BNB")) {
            return "BNB";
        }
        throw new RuntimeException("WTF is the master crypto? : " + pairSymbole);
    }
}
