import io.swagger.client.ApiException;
import io.swagger.client.api.DefaultApi;
import io.swagger.client.model.Pair;
import triangulation.Triangulation;

import java.util.List;

public class RunTriangulation {

    public static void main(String[] args) {

        DefaultApi apiInstance = new DefaultApi();
        try {
            List<Pair> result = apiInstance.price();
            Triangulation triangulation = new Triangulation();
            triangulation.trianguleBataw(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling DefaultApi#price");
            e.printStackTrace();
        }
    }
}