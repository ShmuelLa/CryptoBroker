package com.example.cryptotrader;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

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
    private Spinner clientSpinner;
    private Spinner tradeOptionsSpinner;
    private Spinner symbolFundSpinner;
    private Spinner symbolTargetSpinner;
    ArrayList<String> clientsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trader);
        user = FirebaseAuth.getInstance().getCurrentUser();
        accounts_db = FirebaseDatabase.getInstance().getReference("Accounts");
        clientSpinner = findViewById(R.id.clientSpinner);
        tradeOptionsSpinner = findViewById(R.id.optionsSpinner);
        symbolFundSpinner = findViewById(R.id.symbolFundSpinner);
        symbolTargetSpinner = findViewById(R.id.symbolTargetSpinner);
        clientsList.add("All");
        accounts_db.child(user.getUid()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(task.isSuccessful()) {
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
        clientSpinner.setAdapter(clientsAdapter);
        tradeOptionsSpinner.setAdapter(tradeOptionsAdapter);
        symbolFundSpinner.setAdapter(symbolFundsAdapter);
        symbolTargetSpinner.setAdapter(symbolTargetAdapter);
//        spinnerColletion(clientSpinner, tradeOptionsSpinner, symbolFundSpinner, symbolTargetSpinner);
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

    public String getSpinnerChosenText(Spinner spinner) {
        TextView textView = (TextView)spinner.getSelectedView();
        String result = textView.getText().toString();
        return result;
    }
}