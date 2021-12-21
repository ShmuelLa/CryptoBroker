package com.example.cryptotrader;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.binance.api.client.domain.account.Trade;
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
        clientsList = ctAccount.getClientNamesList(accountsDB, user);
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
        progressBar = findViewById(R.id.progressBar);
//        spinnerColletion(clientSpinner, tradeOptionsSpinner, symbolFundSpinner, symbolTargetSpinner);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.initiateOrderButton:
                switch (ctUtils.getSpinnerChosenText(clientSpinner)) {
                    case "All":
                        break;
                }
                break;
            case R.id.profile:
                progressBar.setVisibility(View.VISIBLE);
                startActivity(new Intent(TraderActivity.this, ProfileActivity.class));
                progressBar.setVisibility(View.GONE);
                break;
        }

        }
    }

//    public void spinnerColletion(Spinner first, Spinner second, Spinner third, Spinner fourth) {
//        first.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                TextView textView = (TextView)first.getSelectedView();
//                switch(textView.getText().toString()) {
//
//                }
//
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });
//    }
