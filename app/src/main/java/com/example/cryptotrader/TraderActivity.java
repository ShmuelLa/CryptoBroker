package com.example.cryptotrader;

import static com.binance.api.client.domain.account.NewOrder.limitSell;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.binance.api.client.BinanceApiAsyncRestClient;
import com.binance.api.client.BinanceApiCallback;
import com.binance.api.client.BinanceApiClientFactory;
import com.binance.api.client.domain.TimeInForce;
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

public class TraderActivity extends AppCompatActivity implements View.OnClickListener {
    private Button sellButton;
    private DatabaseReference accounts_db;
    private FirebaseUser user;
    private int numberOfClients;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trader);
        user = FirebaseAuth.getInstance().getCurrentUser();
        accounts_db = FirebaseDatabase.getInstance().getReference("Accounts");
        Spinner dropdown = findViewById(R.id.spinner1);
        ArrayList<String> items = new ArrayList<>();
        items.add("All");
        accounts_db.child(user.getUid()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(task.isSuccessful()){
                    GenericTypeIndicator<HashMap<String, ctCredentials>> gType = new GenericTypeIndicator<HashMap<String,  ctCredentials>>() {};
                    HashMap<String,  ctCredentials> map = task.getResult().getValue(gType);
                    map.forEach((clientName, clientTokens) -> items.add(clientName));
                }
            }
        });
        System.out.println(items.toString());
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        dropdown.setAdapter(adapter);
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