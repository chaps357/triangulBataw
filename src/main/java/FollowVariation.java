import io.swagger.client.*;
import io.swagger.client.api.DefaultApi;
import io.swagger.client.model.Pair;
import triangulation.Triangulation;
import triangulation.Variation;

import java.util.*;

public class FollowVariation {

    public static void main(String[] args) {

        DefaultApi apiInstance = new DefaultApi();
        try {
            Triangulation triangulation = new Triangulation();
            List<String> cryptos = new ArrayList<>();

            cryptos.add("BTC");
            cryptos.add("ARN");
            cryptos.add("ETH");
            cryptos.add("INS");
            cryptos.add("BTC");

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