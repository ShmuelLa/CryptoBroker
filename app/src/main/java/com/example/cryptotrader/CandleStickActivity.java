package com.example.cryptotrader;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.litesoftwares.coingecko.CoinGeckoApiClient;
import com.litesoftwares.coingecko.domain.Coins.MarketChart;
import com.litesoftwares.coingecko.impl.CoinGeckoApiClientImpl;

import java.util.ArrayList;
import java.util.List;

public class CandleStickActivity extends AppCompatActivity {
    private ArrayList<BarEntry> chartEntries;
    private MarketChart resultMarketChart;
    BarChart barChart;
    BarDataSet barDataSet;
    private int chartCounter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_candel_stick);
        chartEntries = new ArrayList<>();
        barChart = findViewById(R.id.barChart);
        new barChartTask().execute();
        barChart.animateY(2000);
    }

    private class barChartTask extends AsyncTask {

        private barChartTask() {
        }

        @Override
        protected Object doInBackground(Object[] objects) {
            CoinGeckoApiClient client = new CoinGeckoApiClientImpl();
            resultMarketChart = client.getCoinMarketChartById("bitcoin","usd",1);
            System.out.println(resultMarketChart.getPrices());
            for (List<String> list : resultMarketChart.getPrices()) {
                BarEntry tempEntry = new BarEntry(chartCounter, Float.parseFloat(list.get(1)));
                chartCounter++;
                System.out.println(tempEntry.toString());
                chartEntries.add(tempEntry);
            }
            barDataSet = new BarDataSet(chartEntries, "Price");
            barDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
            barDataSet.setValueTextColor(Color.BLACK);
            barDataSet.setValueTextSize(16f);
            client.shutdown();
            BarData barData = new BarData(barDataSet);
            barChart.setFitBars(true);
            barChart.setData(barData);
            barChart.getDescription().setText("bitcoin daily zibi");
            return null;
        }
    }
}