package com.example.cryptotrader;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.collection.CircularArray;

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
import com.binance.api.client.domain.account.Account;
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

public class HomeActivity extends AppCompatActivity implements View.OnClickListener{

    private ImageButton wallet, chart, add, profile;
    private Button addButton, mainTraderButton, cancelTradeButton;
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
        textView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                System.out.println(s.toString()+"ONONONONONONONONONONONONONONONONONONONONONONONONON");
            }

            @Override
            public void afterTextChanged(Editable s) {
                System.out.println(s.toString()+"AFTERAFTERAFTERAFTERAFTERAFTERAFTERAFTERAFTERAFTERAFTER");
            }
        });

//        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_dropdown_item,temp);
//        spinner.setAdapter(adapter);
//
//
//        AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
//        builder.setView(spinner);
//        builder.create();
//        builder.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                System.out.println("sadahdslkhkasdsa");
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });
        TextView close_pop = myDialog.findViewById(R.id.txtclose);
        close_pop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialog.dismiss();
            }
        });
        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        myDialog.show();

//        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                String selected = (String) parent.getItemAtPosition(position);
//                ctCredentials credentials = ctCredentialsArrayList.get(temp.indexOf(selected));
//                BinanceApiClientFactory factory = BinanceApiClientFactory
//                        .newInstance(credentials.getKey(), credentials.getSecret());
//                BinanceApiAsyncRestClient client = factory.newAsyncRestClient();
//                System.out.println("12333131231312312312312");
//                client.getAccount(new BinanceApiCallback<Account>() {
//                    @Override
//                    public void onResponse(Account account) {
//                        System.out.println(" !@#!@#!@#!@#!@#!@#!@#!@#!@#!@#!@#!@#!@#!@#!@3 " + account.getBalances());
//                    }
//                });
//            }
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//            }
//        });

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
