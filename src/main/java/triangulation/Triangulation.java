package triangulation;

import io.swagger.client.model.Pair;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

public class Triangulation {


    public void trianguleBataw(List<Pair> pairs) {

        Map<String, List<Couple>> convertions = listCouples(pairs);

        return;
    }

    private Map<String, List<Couple>> listCouples(List<Pair> pairs) {
        Map<String, List<Couple>> convertions = new HashMap<>();
        for(Pair pair : pairs){
            if(pair.getSymbol().equals("123456")){
                continue;
            }
            Couple couple = new Couple(pair.getSymbol(), pair.getPrice());
            if(convertions.get(couple.getCrypto())==null){
                List<Couple> couples = new ArrayList<>();
                couples.add(couple);
                convertions.put(couple.getCrypto(), couples);
            }else{
                convertions.get(couple.getCrypto()).add(couple);
            }

            if(convertions.get(couple.getMasterCrypto())==null){
                List<Couple> couples = new ArrayList<>();
                couples.add(couple);
                convertions.put(couple.getMasterCrypto(), couples);
            }else{
                convertions.get(couple.getMasterCrypto()).add(couple);
            }
        }
        return convertions;
    }

}

class Couple{
    private String crypto;
    private String masterCrypto;
    private BigDecimal price;

    public Couple(String pairSymbole, BigDecimal price) {
        this.masterCrypto = findMasterCrypto(pairSymbole);
        this.crypto = pairSymbole.replaceAll(masterCrypto,"");
        this.price = price;
    }

    private String findMasterCrypto(String pairSymbole){
        if(pairSymbole.matches(".*USDT")){
            return "USDT";
        }else if(pairSymbole.matches(".*BTC")){
            return "BTC";
        }else if(pairSymbole.matches(".*ETH")){
            return "ETH";
        }else if(pairSymbole.matches(".*BNB")){
            return "BNB";
        }
        throw new RuntimeException("WTF is the master crypto? : "+pairSymbole);
    }

    public BigDecimal getPrice() {
        return price;
    }

    public String getCrypto() {
        return crypto;
    }

    public String getMasterCrypto() {
        return masterCrypto;
    }
}
