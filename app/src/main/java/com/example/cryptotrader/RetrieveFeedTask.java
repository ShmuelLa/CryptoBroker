package com.example.cryptotrader;

import android.os.AsyncTask;
import com.binance.api.client.BinanceApiClientFactory;
import com.binance.api.client.BinanceApiRestClient;

class RetrieveFeedTask extends AsyncTask<String, Void, ctTrader> {

    private Exception exception;

    @Override
    protected ctTrader doInBackground(String... strings) {
        try {
            ctCredentials shm = new ctCredentials();
            ctAccount shmuel = new ctAccount(shm.key, shm.secret);
            BinanceApiClientFactory factory = BinanceApiClientFactory.newInstance(shmuel._api_key, shmuel._api_secret);
            BinanceApiRestClient client = factory.newRestClient();
            try {
                System.out.println(client.getAccount().getAssetBalance("BTC"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            this.exception = e;

            return null;
        }
        return null;
    }
}