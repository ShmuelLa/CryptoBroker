package com.example.cryptotrader;

import static com.binance.api.client.domain.account.NewOrder.limitBuy;
import static com.binance.api.client.domain.account.NewOrder.limitSell;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.binance.api.client.BinanceApiAsyncRestClient;
import com.binance.api.client.BinanceApiClientFactory;
import com.binance.api.client.domain.TimeInForce;
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

public class TraderActivity extends AppCompatActivity implements View.OnClickListener {
    private DatabaseReference accountsDB;
    private FirebaseUser user;
    private Button sendOrderButton;
    private TextView fundText, priceText, inputErrorMessage;
    private final String[] tradeOptions = {"Buy", "Sell", "Limit Buy", "Limit Sell", "Futures Buy", "Futures Sell"};
    private final String[] symbolFundOptions = {"USDT", "BUSD", "BNB"};
    private final String[] symbolTargetOptions = {"BTC", "ETH", "ADA", "MANA", "BNB"};
    private Spinner clientSpinner;
    private Spinner tradeOptionsSpinner;
    private Spinner symbolFundSpinner;
    private Spinner symbolTargetSpinner;
    private String chosenSymbol;
    private String chosenOrderType;
    private String chosenClient;
    private String chosenCoinAmount;
    private String chosenCoinMarketValue;
    private ProgressBar progressBar;
    private ImageButton profileButton;
    private Dialog myDialog;
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

        myDialog = new Dialog(this);
        sendOrderButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.initiateOrderButton) {
            if (checkOrderValidity()) return;
            chosenClient = ctUtils.getSpinnerChosenText(clientSpinner);
            chosenOrderType = ctUtils.getSpinnerChosenText(tradeOptionsSpinner);
            chosenCoinAmount = ctUtils.getSpinnerChosenText(symbolFundSpinner);
            chosenCoinMarketValue = ctUtils.getSpinnerChosenText(symbolTargetSpinner);
            chosenSymbol = chosenCoinMarketValue + chosenCoinAmount;
            String fundsAmountTextString = fundText.getText().toString().trim();
            String marketPriceTextString = priceText.getText().toString().trim();
            if (chosenClient.equals("All")) {
                if (chosenOrderType.equals("Limit Buy") || chosenOrderType.equals("Limit Sell")) {
                    initiateLimitOrder();
                }
                System.out.println(chosenClient);
                System.out.println(chosenOrderType + " " + chosenSymbol + " " + fundsAmountTextString + " " + marketPriceTextString);
            }
            else {
                if (chosenOrderType.equals("Limit Buy") || chosenOrderType.equals("Limit Sell")) {
                    initiateLimitOrder();

                }
                System.out.println(chosenClient);
                System.out.println(chosenOrderType + " " + chosenSymbol);
            }
        }
    }

    @SuppressLint("SetTextI18n")
    boolean checkOrderValidity() {
        if (chosenCoinAmount == null) {
            showOrderErrorPopup("Any order must have a coin amount");
            return true;
        }
        else if (chosenCoinAmount.isEmpty()) {
            showOrderErrorPopup("Any order must have a coin amount");
            return true;
        }
        else if ((chosenOrderType.equals("Limit Buy") || chosenOrderType.equals("Limit Sell"))
                && (chosenCoinMarketValue == null || Integer.parseInt(chosenCoinMarketValue) == 0
                || Float.parseFloat(chosenCoinMarketValue) == 0) || chosenCoinMarketValue.isEmpty()) {
            showOrderErrorPopup("Limit order must have a coin market value");
            return true;
        }

        return false;
    }

    void initiateLimitOrder() {
        accountsDB.child(user.getUid()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    GenericTypeIndicator<HashMap<String, ctCredentials>> gType =
                            new GenericTypeIndicator<HashMap<String, ctCredentials>>() {};
                    HashMap<String, ctCredentials> map = task.getResult().getValue(gType);
                    assert map != null;
                    map.forEach((clientName, clientTokens) -> {
                        if (clientName.equals(chosenClient)) {
                            BinanceApiClientFactory factory = BinanceApiClientFactory
                                    .newInstance(clientTokens.getKey(), clientTokens.getSecret());
                            BinanceApiAsyncRestClient client = factory.newAsyncRestClient();
                            try {
                                if (chosenOrderType.equals("Limit Buy") && !chosenClient.equals("All")) {
                                    client.newOrder(limitBuy(chosenSymbol, TimeInForce.GTC, chosenCoinAmount, chosenCoinMarketValue)
                                            ,response -> System.out.println(response));
                                }
                                else if (chosenOrderType.equals("Limit Sell") && !chosenClient.equals("All")) {
                                    client.newOrder(limitSell(chosenSymbol, TimeInForce.GTC, chosenCoinAmount, chosenCoinMarketValue)
                                            ,response -> System.out.println(response));
                                }
                            }
                            catch (Exception e) {
                                System.out.println("Limit order exception");
                            }
                        }
                    });
                }
            }
        });
    }

    void showOrderErrorPopup(String errorMsg) {
        myDialog.setContentView(R.layout.popup_invalid_order_warning);
        inputErrorMessage = myDialog.findViewById(R.id.orderInputErrorText);
        inputErrorMessage.setText(errorMsg);
//        TextView closePopupText = myDialog.findViewById(R.id.txtclose);
//        mainTraderButton = myDialog.findViewById(R.id.mainTraderButton);
//        mainTraderButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(HomeActivity.this, TraderActivity.class));
//            }
//        });
//        cancelTradeButton = myDialog.findViewById(R.id.cancelTradeButton);
//        cancelTradeButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(HomeActivity.this, CancelOrderActivity.class));
//            }
//        });
//        closePopupText.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                myDialog.dismiss();
//            }
//        });
        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        myDialog.show();
    }
}