package com.example.cryptotrader;

import static com.binance.api.client.domain.account.NewOrder.limitSell;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.collection.CircularArray;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.binance.api.client.BinanceApiAsyncRestClient;
import com.binance.api.client.BinanceApiCallback;
import com.binance.api.client.BinanceApiClientFactory;
import com.binance.api.client.domain.TimeInForce;
import com.binance.api.client.domain.account.Account;
import com.binance.api.client.domain.account.AssetBalance;
import com.binance.api.client.domain.general.Asset;
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
import java.util.HashMap;
import java.util.List;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener{

    private ImageButton wallet, chart, add, profile;
    private Button addButton, mainTraderButton, cancelTradeButton,bit_value,eth_value,ada_value,usdt_value;
    private Dialog myDialog;
    private EditText accountName, keyInput, secretInput;
    private DatabaseReference accounts_db;
    private FirebaseUser user;
    private ProgressBar progressBar;

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
        myDialog.setContentView(R.layout.popup_mywallets);
        ArrayList<String> nList = new ArrayList<>();
        ArrayList<ctCredentials> ctCredentialsArrayList = new ArrayList<>();
        bit_value = myDialog.findViewById(R.id.bit_value);
        eth_value = myDialog.findViewById(R.id.eth_value);
        ada_value = myDialog.findViewById(R.id.ada_value);
        usdt_value = myDialog.findViewById(R.id.usdt_value);
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

        AutoCompleteTextView textView = (AutoCompleteTextView) myDialog.findViewById(R.id.autoCompleteTextView);
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
                HashMap<String,AssetBalance > allAssets = new HashMap<>();
                client.getAccount(new BinanceApiCallback<Account>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onResponse(Account account) {
                        for(AssetBalance ass : account.getBalances()){
                            allAssets.put(ass.getAsset(),ass);
                        }
                        for (String coin : allAssets.keySet()){
                            switch (coin){
                                case "BTC":
                                    if(Double.parseDouble(allAssets.get(coin).getFree()) > 0) {
                                        bit_value.setText("Bitcoin:  " +
                                                new BigDecimal(Double.parseDouble(allAssets.get(coin).getFree()))
                                                        .setScale(10,BigDecimal.ROUND_HALF_EVEN).toPlainString());
                                    }else{
                                        bit_value.setText("Bitcoin:  " + "0.0000");
                                    }
                                    break;
                                case "ETH":
                                    if(Double.parseDouble(allAssets.get(coin).getFree()) > 0) {
                                        eth_value.setText("Ethereum:  " +
                                                new BigDecimal(Double.parseDouble(allAssets.get(coin).getFree()))
                                                        .setScale(10,BigDecimal.ROUND_HALF_EVEN).toPlainString());
                                    }else{
                                        eth_value.setText("Ethereum:  " + "0.0000");
                                    }
                                    break;
                                case "ADA":
                                    if(Double.parseDouble(allAssets.get(coin).getFree()) > 0) {
                                        ada_value.setText("Cardano:  " +
                                                new BigDecimal(Double.parseDouble(allAssets.get(coin).getFree()))
                                                        .setScale(10,BigDecimal.ROUND_HALF_EVEN).toPlainString());
                                    }else{
                                        ada_value.setText("Cardano:  " + "0.0000");
                                    }
                                    break;
                                case "USDT":
                                    if(Double.parseDouble(allAssets.get(coin).getFree()) > 0) {
                                        usdt_value.setText("Usdt:  " +
                                                new BigDecimal(Double.parseDouble(allAssets.get(coin).getFree()))
                                                        .setScale(10,BigDecimal.ROUND_HALF_EVEN).toPlainString());
                                    }else{
                                        usdt_value.setText("Tether/Usdt:  " + "0.0000");
                                    }
                                    break;
                            }
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
                boolean success = addUser(a_name,key,secret);
                if(success){
                    Toast.makeText(HomeActivity.this,
                            "Account added successfully",
                            Toast.LENGTH_LONG).show();
                    myDialog.dismiss();

                }
                if (!success){
                    Toast.makeText(HomeActivity.this, "Something went wrong. Please try again", Toast.LENGTH_LONG).show();
                }

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
    private boolean addUser(String name,String key,String secret){
        return addToDB(name,key,secret);
    }
    private boolean addToDB(String name,String key,String secret){
        final boolean[] success = {false};
        accounts_db.child(user.getUid()).child(name)
                .setValue(new ctCredentials(key,secret))
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            success[0] = true;
                        }
                    }
                });
        return success[0];
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