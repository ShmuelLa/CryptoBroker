package com.example.cryptotrader;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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
import java.util.HashMap;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener{

    private ImageButton wallet, chart, add, profile;
    private Button addButton, mainTraderButton, cancelTradeButton;
    private Dialog myDialog;
    private EditText accountName, keyInput, secretInput;
    private DatabaseReference accounts_db;
    private FirebaseUser user;

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
//        account_name = findViewById(R.id.account_name);
//        key_input = findViewById(R.id.key_input);
//        secret_input = findViewById(R.id.secret_input);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.wallet:
                showpop_wallet(view);
                break;
            case R.id.chart:
                showPopupTradeChooser(view);
                break;
            case R.id.add:
                showPopupAdd(view);
                break;
            case R.id.profile:
                startActivity(new Intent(HomeActivity.this, ProfileActivity.class));
                break;
        }
    }

    private void showpop_wallet(View view) {
        accounts_db.child(user.getUid()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(task.isSuccessful()){
                    GenericTypeIndicator<HashMap<String, ctCredentials>> gType = new GenericTypeIndicator<HashMap<String,  ctCredentials>>() {};
                    HashMap<String,  ctCredentials> map = task.getResult().getValue(gType);
                    map.forEach((clientName, clientTokens)
                            ->
                            {
                                BinanceApiClientFactory factory = BinanceApiClientFactory
                                        .newInstance(clientTokens.getKey(), clientTokens.getSecret());
                                BinanceApiAsyncRestClient client = factory.newAsyncRestClient();
                                client.getAccount(new BinanceApiCallback<Account>() {
                                    @Override
                                    public void onResponse(Account account) {
                                        System.out.println(clientName + "  " + account.getBalances());
                                    }
                                });
                            }
                    );
                }
            }
        });
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