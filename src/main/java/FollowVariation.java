import com.binance.api.price.ApiException;
import com.binance.api.price.api.DefaultApi;
import com.binance.api.price.model.Pair;
import triangulation.Triangulation;
import triangulation.Variation;

import java.util.*;

public class FollowVariation {

    public static void main(String[] args) {

        DefaultApi apiInstance = new DefaultApi();
        try {
            Triangulation triangulation = new Triangulation();
            List<String> cryptos = new ArrayList<>();

            cryptos.add("BNB");
            cryptos.add("ETH");
            cryptos.add("MDA");
            cryptos.add("BTC");
            cryptos.add("BNB");

            while(true) {
                List<Pair> result = apiInstance.price();
                Variation variation = triangulation.followSpecificPath(result, cryptos);
                System.out.println(variation);
            }
        } catch (ApiException e) {
            System.err.println("Exception when calling DefaultApi#price");
            e.printStackTrace();
        }
    }
}