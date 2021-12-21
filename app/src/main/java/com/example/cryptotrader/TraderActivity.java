package com.example.cryptotrader;

import static com.binance.api.client.domain.account.NewOrder.limitBuy;
import static com.binance.api.client.domain.account.NewOrder.limitSell;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.binance.api.client.domain.account.Trade;
import com.binance.api.client.BinanceApiAsyncRestClient;
import com.binance.api.client.BinanceApiClientFactory;
import com.binance.api.client.domain.TimeInForce;
import com.binance.api.client.domain.account.NewOrder;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TraderActivity extends AppCompatActivity implements View.OnClickListener {
    private DatabaseReference accountsDB;
    private FirebaseUser user;
    private Button sendOrderButton;
    private TextView fundText, priceText;
    private final String[] tradeOptions = {"Buy", "Sell", "Limit Buy", "Limit Sell", "Futures Buy", "Futures Sell"};
    private final String[] symbolFundOptions = {"USDT", "BUSD", "BNB"};
    private final String[] symbolTargetOptions = {"BTC", "ETH", "ADA", "MANA", "BNB"};
    private Spinner clientSpinner;
    private Spinner tradeOptionsSpinner;
    private Spinner symbolFundSpinner;
    private Spinner symbolTargetSpinner;
    private ProgressBar progressBar;
    private ImageButton profileButton;
    ArrayList<String> clientsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trader);
        sendOrderButton = findViewById(R.id.initiateOrderButton);
        user = FirebaseAuth.getInstance().getCurrentUser();
        accountsDB = FirebaseDatabase.getInstance().getReference("Accounts");
        clientSpinner = findViewById(R.id.clientSpinner);
        tradeOptionsSpinner = findViewById(R.id.optionsSpinner);
        symbolFundSpinner = findViewById(R.id.symbolFundSpinner);
        symbolTargetSpinner = findViewById(R.id.symbolTargetSpinner);
        clientsList = ctAccount.getClientNamesListAsync(accountsDB, user, "All");
        ArrayAdapter<String> clientsAdapter = new ArrayAdapter<>
                (this, android.R.layout.simple_spinner_dropdown_item, clientsList);
        ArrayAdapter<String> tradeOptionsAdapter = new ArrayAdapter<>
                (this, android.R.layout.simple_spinner_dropdown_item, tradeOptions);
        ArrayAdapter<String> symbolFundsAdapter = new ArrayAdapter<>
                (this, android.R.layout.simple_spinner_dropdown_item, symbolFundOptions);
        ArrayAdapter<String> symbolTargetAdapter = new ArrayAdapter<>
                (this, android.R.layout.simple_spinner_dropdown_item, symbolTargetOptions);
        clientSpinner.setAdapter(clientsAdapter);
        tradeOptionsSpinner.setAdapter(tradeOptionsAdapter);
        symbolFundSpinner.setAdapter(symbolFundsAdapter);
        symbolTargetSpinner.setAdapter(symbolTargetAdapter);
        fundText = findViewById(R.id.fundsAmountText);
        priceText = findViewById(R.id.marketPriceText);
        sendOrderButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.initiateOrderButton) {
            String clientChoice = ctUtils.getSpinnerChosenText(clientSpinner);
            String tradeOptionChoice = ctUtils.getSpinnerChosenText(tradeOptionsSpinner);
            String symbolFundChoice = ctUtils.getSpinnerChosenText(symbolFundSpinner);
            String symbolTargetChoice = ctUtils.getSpinnerChosenText(symbolTargetSpinner);
            String symbol = symbolTargetChoice + symbolFundChoice;
            String fundsAmountTextString = fundText.getText().toString().trim();
            String marketPriceTextString = priceText.getText().toString().trim();
            if (clientChoice.equals("All")) {
                if (tradeOptionChoice.equals("Limit Buy") || tradeOptionChoice.equals("Limit Sell")) {
//                    initiateLimitOrder(symbol);
                }
                System.out.println(clientChoice);
                System.out.println(tradeOptionChoice + " " + symbol + " " + fundsAmountTextString + " " + marketPriceTextString);
            }
            else {
                if (tradeOptionChoice.equals("Limit Buy") || tradeOptionChoice.equals("Limit Sell")) {
//                    initiateLimitOrder(symbol);
                    accountsDB.child(user.getUid()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                            if (task.isSuccessful()) {
                                GenericTypeIndicator<HashMap<String, ctCredentials>> gType =
                                        new GenericTypeIndicator<HashMap<String, ctCredentials>>() {};
                                HashMap<String, ctCredentials> map = task.getResult().getValue(gType);
                                map.forEach((clientName, clientTokens) -> {
                                    if (clientName.equals(clientChoice)) {
                                        BinanceApiClientFactory factory = BinanceApiClientFactory
                                                .newInstance(clientTokens.getKey(), clientTokens.getSecret());
                                        BinanceApiAsyncRestClient client = factory.newAsyncRestClient();
                                        client.newOrder(limitSell(symbol, TimeInForce.GTC, fundsAmountTextString, marketPriceTextString)
                                                ,response -> System.out.println(response));
                                    }
                                });
                            }
                        }
                    });
                }
                System.out.println(clientChoice);
                System.out.println(tradeOptionChoice + " " + symbol);
            }
        }
    }
}