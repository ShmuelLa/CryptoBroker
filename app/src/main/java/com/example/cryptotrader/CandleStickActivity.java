package com.example.cryptotrader;

import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.content.AsyncTaskLoader;

import android.os.AsyncTask;
import android.os.Bundle;

import com.binance.api.client.BinanceApiAsyncRestClient;
import com.binance.api.client.BinanceApiCallback;
import com.binance.api.client.BinanceApiClientFactory;
import com.binance.api.client.BinanceApiRestClient;
import com.binance.api.client.BinanceApiWebSocketClient;
import com.binance.api.client.domain.market.Candlestick;
import com.binance.api.client.domain.market.CandlestickInterval;

import com.litesoftwares.coingecko.CoinGeckoApiClient;
import com.litesoftwares.coingecko.constant.Currency;
import com.litesoftwares.coingecko.domain.Coins.CoinData.DeveloperData;
import com.litesoftwares.coingecko.domain.Coins.CoinData.IcoData;
import com.litesoftwares.coingecko.domain.Coins.CoinFullData;
import com.litesoftwares.coingecko.domain.Coins.CoinList;
import com.litesoftwares.coingecko.domain.Coins.CoinMarkets;
import com.litesoftwares.coingecko.domain.Coins.CoinTickerById;
import com.litesoftwares.coingecko.impl.CoinGeckoApiClientImpl;

import java.util.List;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.Executor;

public class CandleStickActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_candel_stick);
        new CandleStickTask().execute();
    }

    private class CandleStickTask extends AsyncTask<String, Void, String> {

        public CandleStickTask() {
        }

        @Override
        protected String doInBackground(String... strings) {
            String OMGContract = "0xd26114cd6EE289AccF82350c8d8487fedB8A0C07";
            String platform = "ethereum";

            CoinGeckoApiClient client = new CoinGeckoApiClientImpl();

            List<CoinList> coinList = client.getCoinList();
            System.out.println(coinList);

            long totalCoins = coinList.size();
            System.out.println(totalCoins);

            List<CoinMarkets> coinMarkets = client.getCoinMarkets(Currency.USD);
            System.out.println(coinMarkets);

            CoinFullData bitcoinInfo = client.getCoinById("bitcoin");
            System.out.println(bitcoinInfo);

            String genesisDate = bitcoinInfo.getGenesisDate();
            System.out.println(genesisDate);


            DeveloperData bitcoinDevData = bitcoinInfo.getDeveloperData();
            System.out.println(bitcoinDevData);

            long bitcoinGithubStars = bitcoinDevData.getStars();
            System.out.println(bitcoinGithubStars);

            CoinTickerById bitcoinTicker = client.getCoinTickerById("bitcoin");
            System.out.println(bitcoinTicker);

            CoinFullData omiseGoInfo = client.getCoinInfoByContractAddress(platform, OMGContract);
            System.out.println(omiseGoInfo);

            IcoData omiseGoIcoInfo = omiseGoInfo.getIcoData();
            String icoStartDate = omiseGoIcoInfo.getIcoStartDate();
            System.out.println(icoStartDate);

            client.shutdown();
            return null;
        }
    }



}