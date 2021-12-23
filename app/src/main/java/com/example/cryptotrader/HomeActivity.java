package com.example.cryptotrader;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.binance.api.client.BinanceApiAsyncRestClient;
import com.binance.api.client.BinanceApiCallback;
import com.binance.api.client.BinanceApiClientFactory;
import com.binance.api.client.domain.account.Account;
import com.binance.api.client.domain.account.AssetBalance;
import com.binance.api.client.domain.market.TickerPrice;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;


public class HomeActivity extends AppCompatActivity implements View.OnClickListener{

    private ImageButton wallet, chart, add, profile;
    private Button addButton, mainTraderButton, cancelTradeButton,bit_value,eth_value,ada_value,mana_value,bnb_value;
    private Dialog myDialog;
    private EditText accountName, keyInput, secretInput;
    private DatabaseReference accounts_db;
    private FirebaseUser user;
    private TextView preview;
    private ProgressBar progressBar;
    List<String> currencies;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myDialog = new Dialog(this);
        setContentView(R.layout.activity_home);
        add = findViewById(R.id.add);
        profile = findViewById(R.id.profile);
        wallet = findViewById(R.id.wallet);
        chart = findViewById(R.id.chart);
        wallet.setOnClickListener(this);
        chart.setOnClickListener(this);
        add.setOnClickListener(this);
        profile.setOnClickListener(this);
        user = FirebaseAuth.getInstance().getCurrentUser();
        accounts_db = FirebaseDatabase.getInstance().getReference("Accounts");
        progressBar = findViewById(R.id.progressBar);
        preview = findViewById(R.id.preveiw);
        preview.setMovementMethod(new ScrollingMovementMethod());
        TreeMap<String,String> prices = new TreeMap<>();
        TreeMap<String,String> cAmounts = new TreeMap<>();
        ArrayList<String> text_preveiw = new ArrayList<>();
        text_preveiw.add("Accounts balances:\n");
        currencies= Arrays.asList("BTC","ETH","ADA","BNB","MANA","USDT");
        accounts_db.child(user.getUid()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(task.isSuccessful()){
                    GenericTypeIndicator<HashMap<String, ctCredentials>> gType = new GenericTypeIndicator<HashMap<String,  ctCredentials>>() {};
                    HashMap<String, ctCredentials> map = task.getResult().getValue(gType);
//                    for(String clientName : map.keySet()){
                    map.forEach((clientName, credentials) ->{
                        BinanceApiClientFactory factory = BinanceApiClientFactory.newInstance(credentials.getKey(), credentials.getSecret());
                        BinanceApiAsyncRestClient client = factory.newAsyncRestClient();
                        client.getAccount(new BinanceApiCallback<Account>() {
                            @Override
                            public void onResponse(Account account) {
                                for(AssetBalance ass : account.getBalances()){
                                    String coin = ass.getAsset();
                                    if(currencies.contains(coin)){
                                        cAmounts.put(coin,ass.getFree());
                                    }
                                }
                                client.getAllPrices(new BinanceApiCallback<List<TickerPrice>>() {
                                    @Override
                                    public void onResponse(List<TickerPrice> tickerPrices) {
                                        double sum = 0;
                                        text_preveiw.add(clientName);
                                        text_preveiw.add("Total Free in USDT: ");
                                        sum+= Double.parseDouble(cAmounts.get("USDT"));
                                        for(TickerPrice tp : tickerPrices){
                                            String symbol = tp.getSymbol();
                                            if(symbol.contains("USDT")){
                                                String cName = symbol.replaceAll("USDT","");
                                                if(currencies.contains(cName)) {
                                                    double price = Double.parseDouble(tp.getPrice());
                                                    double amount = Double.parseDouble(cAmounts.get(cName));
                                                    sum += (price * amount);
                                                }
                                            }
                                        }
                                        text_preveiw.add(Double.toString(sum));
                                        text_preveiw.add("\n");
                                        StringBuilder ptxt = new StringBuilder();
                                        text_preveiw.forEach((t) ->{ptxt.append(t).append("\n");});
                                        preview.setText(ptxt);
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
                            @Override
                            public void onFailure(Throwable cause) {
                                try {
                                    throw cause;
                                } catch (Throwable throwable) {
                                    throwable.printStackTrace();
                                }
                            }
                        });
                    });
                }else{
                    preview.setText("Please add wallets to your account.\n You can do so at the Add button in the middle of the taskbar.");
                }
            }

        });
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.wallet:
                showWallet(view);
                break;
            case R.id.chart:
                showPopupTradeChooser(view);
                break;
            case R.id.add:
                showPopupAdd(view);
                break;
            case R.id.profile:
                progressBar.setVisibility(View.VISIBLE);
                startActivity(new Intent(HomeActivity.this, ProfileActivity.class));
                progressBar.setVisibility(View.GONE);
                break;
        }
    }

    private void showWallet(View view) {
        ArrayList<String> nList = new ArrayList<>();
        ArrayList<ctCredentials> ctCredentialsArrayList = new ArrayList<>();
        myDialog.setContentView(R.layout.popup_mywallets);
        bit_value = myDialog.findViewById(R.id.bit_value);
        eth_value = myDialog.findViewById(R.id.eth_value);
        ada_value = myDialog.findViewById(R.id.ada_value);
        bnb_value = myDialog.findViewById(R.id.bnb_value);
        mana_value = myDialog.findViewById(R.id.mana_value);
        accounts_db.child(user.getUid()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(task.isSuccessful()){
                    GenericTypeIndicator<HashMap<String, ctCredentials>> gType = new GenericTypeIndicator<HashMap<String,  ctCredentials>>() {};
                    HashMap<String, ctCredentials> map = task.getResult().getValue(gType);
                    map.forEach((clientName, clientTokens) -> {nList.add(clientName); ctCredentialsArrayList.add(clientTokens); });
                }
            }
        });
        AutoCompleteTextView textView =  myDialog.findViewById(R.id.autoCompleteTextView);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, nList);
        textView.setAdapter(adapter);
        ArrayList<String> cname = new ArrayList<>();
        textView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                cname.add(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                ctCredentials credentials = ctCredentialsArrayList.get(nList.indexOf(s.toString()));
                BinanceApiClientFactory factory = BinanceApiClientFactory.newInstance(credentials.getKey(), credentials.getSecret());
                BinanceApiAsyncRestClient client = factory.newAsyncRestClient();
                client.getAccount(new BinanceApiCallback<Account>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onResponse(Account account) {
                        for(AssetBalance ass : account.getBalances()){
                            String coin = ass.getAsset();
                            BigDecimal biggy = new BigDecimal(0);
                            switch (coin) {
                                case "BTC":
                                    if (Double.parseDouble(ass.getFree()) > 0) {
                                        biggy = new BigDecimal(Double.parseDouble(ass.getFree()))
                                                .setScale(9, BigDecimal.ROUND_HALF_EVEN);
                                    }
                                    bit_value.setText("BTC:  " + biggy.toPlainString());
                                    break;
                                case "ETH":
                                    if (Double.parseDouble(ass.getFree()) > 0) {
                                        biggy = new BigDecimal(Double.parseDouble(ass.getFree()))
                                                .setScale(6, BigDecimal.ROUND_HALF_EVEN);
                                    }
                                    eth_value.setText("ETH:  " + biggy.toPlainString());
                                    break;
                                case "ADA":
                                    if (Double.parseDouble(ass.getFree()) > 0) {
                                        biggy = new BigDecimal(Double.parseDouble(ass.getFree()))
                                                .setScale(3, BigDecimal.ROUND_HALF_EVEN);
                                    }
                                    ada_value.setText("ADA:  " + biggy.toPlainString());
                                    break;
                                case "BNB":
                                    if (Double.parseDouble(ass.getFree()) > 0) {
                                        biggy = new BigDecimal(Double.parseDouble(ass.getFree()))
                                                .setScale(6, BigDecimal.ROUND_HALF_EVEN);
                                    }
                                    bnb_value.setText("BNB:  " + biggy.toPlainString());
                                    break;
                                case "MANA":
                                    if (Double.parseDouble(ass.getFree()) > 0) {
                                        biggy = new BigDecimal(Double.parseDouble(ass.getFree()))
                                                .setScale(3, BigDecimal.ROUND_HALF_EVEN);
                                    }
                                    mana_value.setText("MANA:  " + biggy.toPlainString());
                                    break;
                            }
                        }
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
        });
        TextView close_pop = myDialog.findViewById(R.id.txtclose);
        close_pop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialog.dismiss();
            }
        });
        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        myDialog.show();
    }
    private void showPopupAdd(View view){
        myDialog.setContentView(R.layout.popup_add);
        addButton = myDialog.findViewById(R.id.btnadd);
        accountName = myDialog.findViewById(R.id.account_name_input);
        keyInput = myDialog.findViewById(R.id.key_input);
        secretInput = myDialog.findViewById(R.id.secret_input);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String a_name = accountName.getText().toString().trim();
                String key = keyInput.getText().toString().trim();
                String secret = secretInput.getText().toString().trim();
                accounts_db.child(user.getUid()).child(a_name)
                        .setValue(new ctCredentials(key,secret))
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(HomeActivity.this,
                                            "Account added successfully",
                                            Toast.LENGTH_LONG).show();
                                    myDialog.dismiss();
                                }
                                else {
                                    Toast.makeText(HomeActivity.this, "Something went wrong. Please try again", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
            }
        });
        TextView closePopupText = myDialog.findViewById(R.id.txtclose);
        closePopupText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialog.dismiss();
            }
        });
        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        myDialog.show();
    }

    void showPopupTradeChooser(View view) {
        myDialog.setContentView(R.layout.popup_trader_chooser);
        TextView closePopupText = myDialog.findViewById(R.id.txtclose);
        mainTraderButton = myDialog.findViewById(R.id.mainTraderButton);
        mainTraderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, TraderActivity.class));
            }
        });
        cancelTradeButton = myDialog.findViewById(R.id.cancelTradeButton);
        cancelTradeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, CancelOrderActivity.class));
            }
        });
        closePopupText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialog.dismiss();
            }
        });
        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        myDialog.show();
    }
}


//          case "USDT":
//                  if(Double.parseDouble(allAssets.get(coin).getFree()) > 0) {
//                  biggy = new BigDecimal(Double.parseDouble(allAssets.get(coin).getFree()))
//                  .setScale(3,BigDecimal.ROUND_HALF_EVEN);
//                  }else{
//                  balance.put("USDT",new BigDecimal(0));
//                  }
//                  break;