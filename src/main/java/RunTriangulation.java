import com.binance.api.price.ApiException;
import com.binance.api.price.api.DefaultApi;
import com.binance.api.price.model.Pair;
import triangulation.Triangulation;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class RunTriangulation {

    public static void main(String[] args) {

        DefaultApi apiInstance = new DefaultApi();
        Triangulation triangulation = new Triangulation();
        SimpleDateFormat sdf = new SimpleDateFormat("H:mm:ss");
        while(true) {
            try {
                List<Pair> result = apiInstance.price();
                triangulation.trianguleBataw(result);
            } catch (ApiException e) {
                Calendar cal = Calendar.getInstance();
                String formatDate = sdf.format(cal.getTime());
                System.err.println(formatDate+" - "+e.getMessage());
            }
        }
    }
}