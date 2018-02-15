import com.binance.api.price.ApiException;
import com.binance.api.price.api.DefaultApi;
import com.binance.api.price.model.Pair;
import triangulation.Triangulation;
import triangulation.PriceVariation;

import java.text.SimpleDateFormat;
import java.util.*;

public class FollowVariation {

    public static void main(String[] args) {

        DefaultApi apiInstance = new DefaultApi();
        try {
            Triangulation triangulation = new Triangulation();
            List<String> cryptos = new ArrayList<>();

            cryptos.add("BTC");
            cryptos.add("AE");
            cryptos.add("BNB");
            cryptos.add("BTC");

            SimpleDateFormat sdf = new SimpleDateFormat("H:mm:ss");

            while(true) {
                PriceVariation variation = triangulation.followSpecificPath(cryptos);
                Calendar cal = Calendar.getInstance();
                String formatDate = sdf.format(cal.getTime());
                System.out.println(formatDate+":"+variation);
            }
        } catch (Exception e) {
            System.err.println("Exception when calling DefaultApi#price");
            e.printStackTrace();
        }
    }
}