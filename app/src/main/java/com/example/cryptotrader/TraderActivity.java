package com.example.cryptotrader;

import static com.binance.api.client.domain.account.NewOrder.limitSell;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.binance.api.client.BinanceApiAsyncRestClient;
import com.binance.api.client.BinanceApiClientFactory;
import com.binance.api.client.domain.TimeInForce;

public class TraderActivity extends AppCompatActivity implements View.OnClickListener {
    private Button sellButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trader);
        sellButton = findViewById(R.id.sellbutton);
        sellButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sellbutton:
                try {
                    BinanceApiClientFactory factory = BinanceApiClientFactory.newInstance("", "");
                    BinanceApiAsyncRestClient client = factory.newAsyncRestClient();
                    client.newOrder(limitSell("ETHUSDT", TimeInForce.GTC, "0.2", "5000"),
                            response -> System.out.println(response));
                } catch (Exception e) {
                    System.out.println(e);
                }
                break;
        }
    }
}