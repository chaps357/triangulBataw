package triangulation;

import com.binance.api.client.BinanceApiClientFactory;
import com.binance.api.client.BinanceApiRestClient;
import com.binance.api.client.domain.market.OrderBook;
import com.binance.api.client.domain.market.OrderBookEntry;
import com.binance.api.client.exception.BinanceApiException;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Sets;
import com.binance.api.price.model.Pair;

import javax.annotation.Nullable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.SocketTimeoutException;
import java.text.SimpleDateFormat;
import java.util.*;

public class Triangulation {

    private static final int NUMBER_OF_TRADES = 3;
    private static final Integer DISPLAYED = null;
    private static final BigDecimal FEES = BigDecimal.valueOf(0.05);
    private static final String INITIAL_CRYPTO = "BTC";
    private static final Double MINIMUM_PERCENT = 0d;
    private final BinanceApiRestClient client;
    private final SimpleDateFormat sdf = new SimpleDateFormat("H:mm:ss");


    public Triangulation() {
        BinanceApiClientFactory factory = BinanceApiClientFactory.newInstance("dKc3mh02OS6qNR8Q1f56q3MDMqslEj70irgx9JGKgq7Gd0mNLF3KCQrwV2MH1LU2", "UeAjxqAscRpbLXYhPlieLhdaI2i3u6aah6U1vHA0Z2pVdBqen9oXB0i7GaDivxQL");
        client = factory.newRestClient();
    }

    public PriceVariation followSpecificPath(List<Pair> pairs, List<String> cryptos){
        Map<String, Set<Trade>> trades = listAllTrades(pairs);
        LinkedList<Trade> path = new LinkedList<>();

        for(int i = 0; i<cryptos.size()-1; i++){
            String crypto = cryptos.get(i);
            final String nextCrypto = cryptos.get(i+1);
            Set<Trade> cryptoTrades = trades.get(crypto);
            Set<Trade> filter = Sets.filter(cryptoTrades, new Predicate<Trade>() {
                @Override
                public boolean apply(@Nullable Trade trade) {
                    return trade.getTarget().equals(nextCrypto);
                }
            });
            path.add(filter.iterator().next());
        }
        PriceVariation variation = findPriceVariation(path, true);
        return variation;
    }

    public void trianguleBataw(List<Pair> pairs) {
//        System.out.println("Listing trades...");
        Map<String, Set<Trade>> trades = listAllTrades(pairs);
//        System.out.println("Finding paths...");
        Set<Map.Entry<String, Set<Trade>>> entries = trades.entrySet();
        List<LinkedList<Trade>> totalPaths = new ArrayList<>();
        for (Map.Entry<String, Set<Trade>> entry : entries) {
            String originCrypto = entry.getKey();
            if(INITIAL_CRYPTO!=null && !INITIAL_CRYPTO.equals(originCrypto)){
                continue;
            }
            List<LinkedList<Trade>> paths = findPaths(originCrypto, NUMBER_OF_TRADES, trades);
            totalPaths.addAll(paths);
        }
//        System.out.println("Found "+totalPaths.size()+" paths!");
//        System.out.println("Calculating price variations...");
        List<PriceVariation> variations = new ArrayList<>();
        int i = 0;
        for(LinkedList<Trade> path:totalPaths){
            i++;
            PriceVariation variation = findPriceVariation(path, false);
            variations.add(variation);
//            System.out.println("Variation calculation count: "+i+"/"+totalPaths.size());
        }

//        System.out.println("Sort variations...");
        variations.sort((o1, o2) -> o2.getVariationAmount().compareTo(o1.getVariationAmount()));
//        System.out.println("Results!");
        if(DISPLAYED!=null) {
            for (i = 0; i < DISPLAYED; i++) {
                PriceVariation variation = variations.get(i);
                if (variation.getVariationAmount().compareTo(BigDecimal.valueOf(MINIMUM_PERCENT)) == 1) {
                    System.out.println(variation);
                }
            }
        }
        PriceVariation variation = findPriceVariation(variations.get(0).getPath(), true);
        if(MINIMUM_PERCENT == null || variation.getVariationAmount().doubleValue() > MINIMUM_PERCENT) {
            Calendar cal = Calendar.getInstance();
            String formatDate = sdf.format(cal.getTime());
            System.out.println(formatDate + ":" + variation);
        }
        return;
    }

    private PriceVariation findPriceVariation(LinkedList<Trade> path, boolean real) {
            BigDecimal variationAmount = BigDecimal.valueOf(100l);
            Iterator<Trade> iterator = path.iterator();
            while (iterator.hasNext()) {
                Trade trade = iterator.next();
                if (real) {
                    OrderBook orderBook = client.getOrderBook(trade.getPairSymbol(), 5);
                    BigDecimal bestPrice;
                    switch (trade.getOperation()) {
                        case BUY:
                            OrderBookEntry bestAsk = orderBook.getAsks().get(0);
                            bestPrice = new BigDecimal(bestAsk.getPrice());
                            trade.setBestSeller(bestPrice);
//                        System.out.println("BUY "+trade.getPairSymbol()+" - PRICE MARKET="+trade.getInitialPrice()+" BEST SELLER="+bestPrice);
                            break;
                        case SELL:
                            OrderBookEntry bestBid = orderBook.getBids().get(0);
                            BigDecimal bestBidPrice = new BigDecimal(bestBid.getPrice());
//                        System.out.println("SELL "+trade.getPairSymbol()+" - PRICE MARKET="+trade.getInitialPrice()+" BEST BUYER="+bestBidPrice);
                            trade.setBestBuyer(bestBidPrice);
                            bestPrice = invertPrice(bestBidPrice);
                            break;
                        default:
                            throw new RuntimeException("C'est quoi cette opération de merde??? " + trade.getOperation());
                    }
                    variationAmount = variationAmount.divide(bestPrice, 10, RoundingMode.HALF_EVEN);
                } else {
                    variationAmount = variationAmount.divide(trade.getPrice(), 10, RoundingMode.HALF_EVEN);
                }
                BigDecimal tradeFees = variationAmount.divide(BigDecimal.valueOf(100)).multiply(FEES);
                variationAmount = variationAmount.subtract(tradeFees);

            }
            return new PriceVariation(variationAmount.subtract(new BigDecimal(100l)).setScale(2, BigDecimal.ROUND_HALF_DOWN), path);
    }

    private List<LinkedList<Trade>> findPaths(String originCypto, int requestedLevel, Map<String, Set<Trade>> trades) {
        List<LinkedList<Trade>> paths = new ArrayList<>();
        int level = 2;
        Set<Trade> cryptoTrades = trades.get(originCypto);
        for (Trade trade : cryptoTrades) {
            LinkedList<Trade> newPath = new LinkedList<>();
            newPath.add(trade);
            paths.addAll(findLoops(newPath, level, requestedLevel, trades, originCypto));
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
            else if (filter.size() == 1) {
                Trade finalTrade = filter.iterator().next();
                path.add(finalTrade);
                paths.add(path);
                return paths;
            }else{
                //Si on ne peut pas revenir à la crypto d'origine on laisse tomber
                return null;
            }
        }
        level++;
        for (Trade newTrade : nextPossibleTrades) {
            Collection<Trade> filter = Collections2.filter(path, new Predicate<Trade>() {
                @Override
                public boolean apply(@Nullable Trade trade) {
                    return trade.getTarget().equals(newTrade.getTarget()) ;
                }
            });

            if(!filter.isEmpty()){
                continue;
            }else {
                LinkedList<Trade> newPath = new LinkedList<>();
                newPath.addAll(path);
                newPath.add(newTrade);
                if(newTrade.getTarget().equals(originCypto))
                {
                    paths.add(newPath);
                    return paths;
                }
                List<LinkedList<Trade>> loops = findLoops(newPath, level, requestedLevel, trades, originCypto);
                if(loops!=null) {
                    paths.addAll(loops);
                }
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
            Trade cryptoTrade = new Trade(crypto, masterCrypto, pair.getPrice(), pair.getPrice(), OperationEnum.SELL, pairSymbol);
            BigDecimal invertedPrice = invertPrice(pair.getPrice());
            Trade masterTrade = new Trade(masterCrypto, crypto, invertedPrice, pair.getPrice(), OperationEnum.BUY, pairSymbol);

            addNewTrade(trades, crypto, cryptoTrade);
            addNewTrade(trades, masterCrypto, masterTrade);
        }
        return trades;
    }

    private BigDecimal invertPrice(BigDecimal price) {
        return BigDecimal.ONE.divide(price, 10,
                        RoundingMode.HALF_EVEN);
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
