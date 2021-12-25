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
import com.litesoftwares.coingecko.domain.Coins.MarketChart;
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

    private static class CandleStickTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            CoinGeckoApiClient client = new CoinGeckoApiClientImpl();
            MarketChart marketchart = client.getCoinMarketChartById("bitcoin","usd",1);
            System.out.println(marketchart.toString());
            client.shutdown();
            return null;
        }
    }



}