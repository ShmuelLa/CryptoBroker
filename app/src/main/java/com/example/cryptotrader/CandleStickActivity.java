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

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.Executor;

public class CandleStickActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_candel_stick);
//        new CandleStickTask("BTCUSDT", CandlestickInterval.DAILY).execute();
        BinanceApiClientFactory factory = BinanceApiClientFactory.newInstance();
        BinanceApiRestClient client = factory.newRestClient();
        client.getCandlestickBars("BTCUSDT", CandlestickInterval.DAILY);
    }

    private class CandleStickTask extends AsyncTask<String, Void, String> {
        private Map<Long, Candlestick> candlesticksCache;
        String symbolToExecute;
        CandlestickInterval intervalToExecute;

        public CandleStickTask(String symbol, CandlestickInterval interval) {
            this.symbolToExecute = symbol;
            this.intervalToExecute = interval;
        }

        /**
         * Initializes candlesticks by using the Binance REST API.
         */
        private void initializeCandlestickCache(String symbol, CandlestickInterval interval) {





//            BinanceApiClientFactory factory = BinanceApiClientFactory.newInstance();
//            BinanceApiAsyncRestClient client = factory.newAsyncRestClient();
//            client.getCandlestickBars(symbol.toUpperCase(), interval, new BinanceApiCallback<List<Candlestick>>() {
//                @Override
//                public void onResponse(List<Candlestick> candlesticks) {
//                    System.out.println("zibi");
//                    for (Candlestick candle : candlesticks) {
//                        System.out.println(candle.toString());
//                    }
//                }
//
//                @Override
//                public void onFailure(Throwable cause) {
//                    System.out.println("zababir");
//                    System.out.println(cause.toString());
//                }
//            });




//            this.candlesticksCache = new TreeMap<>();
//            for (Candlestick candlestickBar : candlestickBars) {
//                candlesticksCache.put(candlestickBar.getOpenTime(), candlestickBar);
//            }
        }

//        /**
//         * Begins market data streaming
//         */
//        private void startCandlestickEventStreaming(String symbol, CandlestickInterval interval) {
//            BinanceApiClientFactory factory = BinanceApiClientFactory.newInstance();
//            BinanceApiWebSocketClient client = factory.newWebSocketClient();
//
//            client.onCandlestickEvent(symbol.toLowerCase(), interval, response -> {
//                Long openTime = response.getOpenTime();
//                Candlestick updateCandlestick = candlesticksCache.get(openTime);
//                if (updateCandlestick == null) {
//                    // new candlestick
//                    updateCandlestick = new Candlestick();
//                }
//                // update candlestick with the stream data
//                updateCandlestick.setOpenTime(response.getOpenTime());
//                updateCandlestick.setOpen(response.getOpen());
//                updateCandlestick.setLow(response.getLow());
//                updateCandlestick.setHigh(response.getHigh());
//                updateCandlestick.setClose(response.getClose());
//                updateCandlestick.setCloseTime(response.getCloseTime());
//                updateCandlestick.setVolume(response.getVolume());
//                updateCandlestick.setNumberOfTrades(response.getNumberOfTrades());
//                updateCandlestick.setQuoteAssetVolume(response.getQuoteAssetVolume());
//                updateCandlestick.setTakerBuyQuoteAssetVolume(response.getTakerBuyQuoteAssetVolume());
//                updateCandlestick.setTakerBuyBaseAssetVolume(response.getTakerBuyQuoteAssetVolume());
//
//                // Store the updated candlestick in the cache
//                candlesticksCache.put(openTime, updateCandlestick);
//                System.out.println(updateCandlestick);
//            });
//        }
//
//        /**
//         * @return a klines/candlestick cache, containing the open/start time of the candlestick as the key,
//         * and the candlestick data as the value.
//         */
//        public Map<Long, Candlestick> getCandlesticksCache() {
//            return candlesticksCache;
//        }
//
        @Override
        protected String doInBackground(String... strings) {
            initializeCandlestickCache(this.symbolToExecute, this.intervalToExecute);
            return null;
        }
    }



}