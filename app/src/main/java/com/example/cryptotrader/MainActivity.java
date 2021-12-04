package com.example.cryptotrader;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.binance.api.client.BinanceApiClientFactory;
import com.binance.api.client.BinanceApiRestClient;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        System.out.println("sdsdsdasdfsdfsdf123123123123");
    }

    public void onButton2Click(View view) {
        TextView helloText = findViewById(R.id.txtHello);
        helloText.setText("Shooshool Doodool");
        new RetrieveFeedTask().execute();
    }
}