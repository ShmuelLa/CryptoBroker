package com.example.cryptotrader;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.binance.api.client.BinanceApiAsyncRestClient;
import com.binance.api.client.BinanceApiCallback;
import com.binance.api.client.BinanceApiClientFactory;
import com.binance.api.client.domain.account.Order;
import com.binance.api.client.domain.account.request.CancelOrderRequest;
import com.binance.api.client.domain.account.request.CancelOrderResponse;
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CancelOrderActivity extends AppCompatActivity {
    private DatabaseReference accountsDB;
    private FirebaseUser user;
    private Button cancelOrderButton, emergencyButton;
    private Spinner clientSpinner, openOrdersSpinner;
    private ArrayList<String> clientsList = new ArrayList<>();
    private ArrayList<String> normalizedOrderList;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cancel_order);
        clientSpinner = findViewById(R.id.clientSpinner);
        openOrdersSpinner = findViewById(R.id.openOrdersSpinner);
        user = FirebaseAuth.getInstance().getCurrentUser();
        accountsDB = FirebaseDatabase.getInstance().getReference("Accounts");
        ArrayList<String> clientsList = ctAccount.getClientNamesListAsync(accountsDB, user, "None");
        ArrayAdapter<String> clientsAdapter = new ArrayAdapter<>(this, android.R.layout
                .simple_spinner_dropdown_item, clientsList);
        clientSpinner.setAdapter(clientsAdapter);
        createClientsSpinner();
        cancelOrderButton = findViewById(R.id.sendCancelOderButton);
        emergencyButton = findViewById(R.id.emergencyButton);
        progressBar = findViewById(R.id.progressBar);
    }

    public ArrayList<String> normalizeOrderListForSpinner(List<Order> beforeList) {
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

    void createClientsSpinner() {
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
                                            cancelOrderButton.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    if(!openOrdersSpinner.getSelectedItem().toString().equals("None"))
                                                    {
                                                        String[] order = openOrdersSpinner.getSelectedItem().toString().trim().split(" ");
                                                        Long oid = Long.parseLong(order[0].substring(1, order[0].length() - 1).trim());
                                                        String symbol = order[1].trim();
                                                        client.cancelOrder(new CancelOrderRequest(symbol, oid), new BinanceApiCallback<CancelOrderResponse>() {
                                                            @Override
                                                            public void onResponse(CancelOrderResponse cancelOrderResponse) {
                                                                System.out.println(cancelOrderResponse.getStatus());
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
                                                    else{
                                                        Toast.makeText(CancelOrderActivity.this, "No Orders to cancel!", Toast.LENGTH_LONG).show();
                                                    }
                                                }
                                            });
                                        }

                                        @Override
                                        public void onFailure(Throwable cause) {
                                            try {
                                                Toast.makeText(CancelOrderActivity.this, "Couldn't retrieve Open Orders. Please try again.", Toast.LENGTH_LONG).show();
                                                throw cause;
                                            } catch (Throwable throwable) {
                                                throwable.printStackTrace();
                                            }
                                        }
                                    });
                                }
                            });
                        }
                    }
                });


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
            String tmpOrder = "[" + order.getOrderId()+ "] " + order.getSymbol() + " " + order.getType() + " "  + order.getPrice();
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