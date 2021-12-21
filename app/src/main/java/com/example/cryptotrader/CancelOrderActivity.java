package com.example.cryptotrader;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.binance.api.client.BinanceApiAsyncRestClient;
import com.binance.api.client.BinanceApiCallback;
import com.binance.api.client.BinanceApiClientFactory;
import com.binance.api.client.domain.account.Order;
import com.binance.api.client.domain.account.request.OrderRequest;
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
import java.util.List;

public class CancelOrderActivity extends AppCompatActivity {
    private DatabaseReference accountsDB;
    private FirebaseUser user;
    private Spinner clientSpinner, openOrdersSpinner;
    private ArrayList<String> clientsList = new ArrayList<>();
    private ArrayList<String> normalizedOrderList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cancel_order);
        clientSpinner = findViewById(R.id.clientSpinner);
        openOrdersSpinner = findViewById(R.id.openOrdersSpinner);
        user = FirebaseAuth.getInstance().getCurrentUser();
        accountsDB = FirebaseDatabase.getInstance().getReference("Accounts");
        clientsList = ctAccount.getClientNamesList(accountsDB, user);
        ArrayAdapter<String> clientsAdapter = new ArrayAdapter<>(this, android.R.layout
                .simple_spinner_dropdown_item, clientsList);
        clientSpinner.setAdapter(clientsAdapter);
        clientSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String chosenUser = clientSpinner.getSelectedItem().toString();

                accountsDB.child(user.getUid()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if(task.isSuccessful()) {
                            GenericTypeIndicator<HashMap<String, ctCredentials>> gType =
                                    new GenericTypeIndicator<HashMap<String,  ctCredentials>>() {};
                            HashMap<String,  ctCredentials> map = task.getResult().getValue(gType);
                            map.forEach((clientNameItr, clientTokens) ->
                            {
                                if (clientNameItr.equals(chosenUser)) {
                                    BinanceApiClientFactory factory = BinanceApiClientFactory
                                            .newInstance(clientTokens.getKey(), clientTokens.getSecret());
                                    BinanceApiAsyncRestClient client = factory.newAsyncRestClient();
                                    client.getOpenOrders(new OrderRequest(null), new BinanceApiCallback<List<Order>>() {
                                        @Override
                                        public void onResponse(List<Order> orders) {
                                            ArrayList<Order> orderList = new ArrayList(orders);
                                            ArrayAdapter<String> ordersAdapter = new ArrayAdapter(CancelOrderActivity.this, android.R.layout
                                                    .simple_spinner_dropdown_item, normalizeOrderListForSpinner(orderList));
                                            openOrdersSpinner.setAdapter(ordersAdapter);
                                        }
                                    });
                                }
                            });
                        }
                    }
                });

//                ArrayList<Order> orderList = ctAccount.getAllOpenOrdersList(chosenUser, accountsDB, user);
//                System.out.println(orderList.toString());
//                ArrayAdapter<String> ordersAdapter = new ArrayAdapter(CancelOrderActivity.this, android.R.layout
//                        .simple_spinner_dropdown_item, orderList);
//                openOrdersSpinner.setAdapter(ordersAdapter);
//                String chosenUser = ctUtils.getSpinnerChosenText(clientSpinner);
//                orderList = ctAccount.getAllOpenOrdersList(chosenUser, accountsDB, user);
//                System.out.println(orderList.toString());
//                normalizedOrderList = normalizeOrderListForSpinner(orderList);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        if (normalizedOrderList != null) {
            showOpenOrders();
        }

    }

    public ArrayList<String> normalizeOrderListForSpinner(ArrayList<Order> beforeList) {
        ArrayList<String> result = new ArrayList<>();
        for (Order order : beforeList) {
            String tmpOrder = order.getSymbol() + " " + order.getType() + " "  + order.getPrice();
            result.add(tmpOrder);
        }
        if (result.size() < 1) {
            result.add("None");
        }
        return result;
    }

    void showOpenOrders() {
        ArrayAdapter<String> ordersAdapter = new ArrayAdapter<>(this, android.R.layout
                .simple_spinner_dropdown_item, normalizedOrderList);
        openOrdersSpinner.setAdapter(ordersAdapter);
    }
}