package com.binance.api;

import com.binance.api.client.BinanceApiClientFactory;
import com.binance.api.client.BinanceApiRestClient;
import com.binance.api.client.domain.account.Account;

public class TestBalance {

    public static void main(String [] args){
        BinanceApiClientFactory factory = BinanceApiClientFactory.newInstance("dKc3mh02OS6qNR8Q1f56q3MDMqslEj70irgx9JGKgq7Gd0mNLF3KCQrwV2MH1LU2", "UeAjxqAscRpbLXYhPlieLhdaI2i3u6aah6U1vHA0Z2pVdBqen9oXB0i7GaDivxQL");
        BinanceApiRestClient client = factory.newRestClient();
        Account account = client.getAccount();
        System.out.println(account.getBalances());
        System.out.println(account.getAssetBalance("ETH").getFree());

    }

}
