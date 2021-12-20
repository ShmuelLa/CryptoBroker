package com.example.cryptotrader;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
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
    private DatabaseReference accounts_db;
    private FirebaseUser user;
    private String[] tradeOptions = {"Buy", "Sell", "Limit Buy", "Limit Sell", "Futures Buy", "Futures Sell"};
    private String[] symbolFundOptions = {"USDT", "BUSD", "BNB"};
    private String[] symbolTargetOptions = {"BTC", "ETH", "ADA", "MANA", "BNB"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trader);
        user = FirebaseAuth.getInstance().getCurrentUser();
        accounts_db = FirebaseDatabase.getInstance().getReference("Accounts");
        Spinner clientDropdown = findViewById(R.id.clientSpinner);
        Spinner tradeOptionsDropdown = findViewById(R.id.optionsSpinner);
        Spinner symbolFundDropdown = findViewById(R.id.symbolFundSpinner);
        Spinner symbolTargetDropdown = findViewById(R.id.symbolTargetSpinner);
        ArrayList<String> clientsList = new ArrayList<>();
        clientsList.add("All");
        accounts_db.child(user.getUid()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(task.isSuccessful()){
                    GenericTypeIndicator<HashMap<String, ctCredentials>> gType = new GenericTypeIndicator<HashMap<String,  ctCredentials>>() {};
                    HashMap<String,  ctCredentials> map = task.getResult().getValue(gType);
                    map.forEach((clientName, clientTokens) -> clientsList.add(clientName));
                }
            }
        });
        ArrayAdapter<String> clientsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, clientsList);
        ArrayAdapter<String> tradeOptionsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, tradeOptions);
        ArrayAdapter<String> symbolFundsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, symbolFundOptions);
        ArrayAdapter<String> symbolTargetAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, symbolTargetOptions);
        clientDropdown.setAdapter(clientsAdapter);
        tradeOptionsDropdown.setAdapter(tradeOptionsAdapter);
        symbolFundDropdown.setAdapter(symbolFundsAdapter);
        symbolTargetDropdown.setAdapter(symbolTargetAdapter);
    }

    @Override
    public void onClick(View v) {
/*        switch (v.getId()) {
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
        }*/
    }
}