package triangulation;

import io.swagger.client.model.Pair;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

public class Triangulation {


    public void trianguleBataw(List<Pair> pairs) {
        Map<String, Set<Trade>> trades  = listAllTrades(pairs);
        System.out.println(trades);
        return;
    }

    private Map<String,Set<Trade>> listAllTrades(List<Pair> pairs) {
        Map<String,Set<Trade>> trades = new HashMap<>();
        for(Pair pair:pairs){
            String pairSymbol = pair.getSymbol();
            if (pairSymbol.equals("123456")) {
                continue;
            }
            String masterCrypto = findMasterCrypto(pairSymbol);
            String crypto = pairSymbol.replaceAll(masterCrypto, "");
            //il faut créer un trade pour la crypto et un pour la master avec le prix inversé
            Trade cryptoTrade = new Trade(crypto, masterCrypto, pair.getPrice());
            Trade masterTrade = new Trade(masterCrypto, crypto, BigDecimal.ONE.divide(pair.getPrice(), 6, RoundingMode.HALF_EVEN));

            addNewTrade(trades, crypto, cryptoTrade);
            addNewTrade(trades, masterCrypto, masterTrade);
        }
        return trades;
    }

    private void addNewTrade(Map<String, Set<Trade>> trades, String crypto, Trade trade) {
        if(trades.get(crypto)==null){
            HashSet<Trade> tradeSet = new HashSet<>();
            tradeSet.add(trade);
            trades.put(crypto, tradeSet);
        }else{
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
