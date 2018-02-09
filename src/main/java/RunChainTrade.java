import trade.ChainTrade;

import java.util.ArrayList;
import java.util.List;

public class RunChainTrade {

    public static void main(String[] args) {

        ChainTrade chainTrade = new ChainTrade();
        List<String> cryptos = new ArrayList<>();
        chainTrade.runTrade(cryptos);

    }
}