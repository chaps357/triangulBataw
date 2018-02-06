package trade;

import com.binance.api.client.BinanceApiClientFactory;
import com.binance.api.client.BinanceApiRestClient;
import com.binance.api.client.domain.account.Account;

import java.util.Iterator;
import java.util.List;

public class ChainTrade {


    public void runTrade(List<String> cryptos) {

        BinanceApiClientFactory factory = BinanceApiClientFactory.newInstance("dKc3mh02OS6qNR8Q1f56q3MDMqslEj70irgx9JGKgq7Gd0mNLF3KCQrwV2MH1LU2", "UeAjxqAscRpbLXYhPlieLhdaI2i3u6aah6U1vHA0Z2pVdBqen9oXB0i7GaDivxQL");
        BinanceApiRestClient client = factory.newRestClient();
        Iterator<String> iterator = cryptos.iterator();
        Account account = client.getAccount();
        String startCrypto = iterator.next();
        String freeAmount = account.getAssetBalance(startCrypto).getFree();
        //exception pour le BNB ne pas prendre la totalité

        while(iterator.hasNext()) {
            //premiere pair
            //3 BNB
            //déterminer le montant de steems qu'on veut acheter
            //vérifier le prix du marché!
            //placer l'ordre en market

        }
    }
}
