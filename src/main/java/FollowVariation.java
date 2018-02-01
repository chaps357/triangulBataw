import com.binance.api.price.ApiException;
import com.binance.api.price.api.DefaultApi;
import com.binance.api.price.model.Pair;
import triangulation.Triangulation;
import triangulation.Variation;

import java.text.SimpleDateFormat;
import java.util.*;

public class FollowVariation {

    public static void main(String[] args) {

        DefaultApi apiInstance = new DefaultApi();
        try {
            Triangulation triangulation = new Triangulation();
            List<String> cryptos = new ArrayList<>();

            cryptos.add("BNB");
            cryptos.add("RDN");
            cryptos.add("BTC");
            cryptos.add("RLC");
            cryptos.add("BNB");

            SimpleDateFormat sdf = new SimpleDateFormat("H:mm:ss");

            while(true) {
                List<Pair> result = apiInstance.price();
                Variation variation = triangulation.followSpecificPath(result, cryptos);
                Calendar cal = Calendar.getInstance();
                String formatDate = sdf.format(cal.getTime());
                System.out.println(formatDate+":"+variation);
            }
        } catch (ApiException e) {
            System.err.println("Exception when calling DefaultApi#price");
            e.printStackTrace();
        }
    }
}