import com.binance.api.price.ApiException;
import com.binance.api.price.api.DefaultApi;
import com.binance.api.price.model.Pair;
import trade.ChainTrade;
import triangulation.Triangulation;
import triangulation.Variation;

import java.util.ArrayList;
import java.util.List;

public class RunChainTrade {

    public static void main(String[] args) {

        ChainTrade chainTrade = new ChainTrade();
        List<String> cryptos = new ArrayList<>();
        chainTrade.runTrade(cryptos);

    }
}