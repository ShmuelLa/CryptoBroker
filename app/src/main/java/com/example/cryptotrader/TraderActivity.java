package com.example.cryptotrader;

import static com.binance.api.client.domain.account.NewOrder.limitBuy;
import static com.binance.api.client.domain.account.NewOrder.limitSell;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Dialog;
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
import com.binance.api.client.BinanceApiCallback;
import com.binance.api.client.BinanceApiClientFactory;
import com.binance.api.client.domain.TimeInForce;
import com.binance.api.client.domain.account.NewOrderResponse;
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
    private TextView fundText, priceText, inputMessage, popupTopic;
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
    private String chosenFundCoin;
    private String chosenTargetCoin;
    private String chosenCoinAmount;
    private String chosenCoinTargetPrice;
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
            chosenClient = ctUtils.getSpinnerChosenText(clientSpinner);
            chosenOrderType = ctUtils.getSpinnerChosenText(tradeOptionsSpinner);
            chosenFundCoin = ctUtils.getSpinnerChosenText(symbolFundSpinner);
            chosenTargetCoin = ctUtils.getSpinnerChosenText(symbolTargetSpinner);
            chosenCoinAmount = fundText.getText().toString();
            chosenCoinTargetPrice = priceText.getText().toString();
            chosenSymbol = chosenTargetCoin + chosenFundCoin;
//            System.out.println("Beforeeee" + chosenClient+" "+chosenOrderType+" "+ chosenFundCoin +" "+ chosenTargetCoin +" "+chosenSymbol
//                    + chosenCoinAmount + " " + chosenCoinTargetPrice);
            if (checkOrderValidity()) return;
            if (chosenOrderType.equals("Limit Buy") || chosenOrderType.equals("Limit Sell")) {
//                System.out.println(chosenClient+" "+chosenOrderType+" "+ chosenFundCoin +" "+ chosenTargetCoin +" "+chosenSymbol
//                + chosenCoinAmount + " " + chosenCoinTargetPrice);
                initiateLimitOrder();
            }
        }
    }

    @SuppressLint("SetTextI18n")
    boolean checkOrderValidity() {
        if (chosenCoinAmount == null) {
            showOrderPopup("Error", "Any order must have a coin amount (Null)");
            return true;
        }
        else if (chosenCoinAmount.isEmpty()) {
            showOrderPopup("Error", "Any order must have a coin amount (isEmpty)");
            return true;
        }
        else if ((chosenOrderType.equals("Limit Buy") || chosenOrderType.equals("Limit Sell"))
                && (chosenCoinTargetPrice == null || Integer.parseInt(chosenCoinTargetPrice) == 0
                || Float.parseFloat(chosenCoinTargetPrice) == 0) || chosenCoinTargetPrice.isEmpty()) {
            showOrderPopup("Error", "Limit order must have a coin market value");
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
                            if (chosenOrderType.equals("Limit Buy") && !chosenClient.equals("All")) {
                                client.newOrder(limitBuy(chosenSymbol, TimeInForce.GTC, chosenCoinAmount, chosenCoinTargetPrice)
                                        ,new BinanceApiCallback<NewOrderResponse>() {
                                            @Override
                                            public void onResponse(NewOrderResponse newOrderResponse) {
                                                showOrderPopup("Success"
                                                        , "Order " + newOrderResponse.getClientOrderId() + " "
                                                                + newOrderResponse.getSymbol() + " Created");
                                            }

                                            @Override
                                            public void onFailure(Throwable cause) {
                                                try {
                                                    throw cause;
                                                } catch (Throwable throwable) {
                                                    throwable.printStackTrace();
                                                }
                                            }
                                        });
                            }
                            else if (chosenOrderType.equals("Limit Sell") && !chosenClient.equals("All")) {
                                System.out.println("stage 88888 " + chosenSymbol);
                                client.newOrder(limitSell(chosenSymbol, TimeInForce.GTC, chosenCoinAmount, chosenCoinTargetPrice)
                                        ,new BinanceApiCallback<NewOrderResponse>() {
                                            @Override
                                            public void onResponse(NewOrderResponse newOrderResponse) {
                                                showOrderPopup("Success"
                                                        , "Order " + newOrderResponse.getClientOrderId() + " "
                                                + newOrderResponse.getSymbol() + " Created");
                                            }

                                            @Override
                                            public void onFailure(Throwable cause) {
                                                try {
                                                    throw cause;
                                                } catch (Throwable throwable) {
                                                    throwable.printStackTrace();
                                                }
                                            }
                                        });
                            }
                        }
                    });
                }
            }
        });
    }

    void showOrderPopup(String topic, String msg) {
        myDialog.setContentView(R.layout.popup_invalid_order_warning);
        inputMessage = myDialog.findViewById(R.id.orderInputErrorText);
        popupTopic = myDialog.findViewById(R.id.orderInputTopic);
        popupTopic.setText(topic);
        inputMessage.setText(msg);
        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        myDialog.show();
    }
}