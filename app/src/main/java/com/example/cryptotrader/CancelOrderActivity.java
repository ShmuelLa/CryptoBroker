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
    private List<Order> orderList;
    private ArrayList<String> normalizedOrderList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cancel_order);
        clientSpinner = findViewById(R.id.clientSpinner);
        user = FirebaseAuth.getInstance().getCurrentUser();
        accountsDB = FirebaseDatabase.getInstance().getReference("Accounts");
        clientsList = ctAccount.getClientNamesList(accountsDB, user);
        ArrayAdapter<String> clientsAdapter = new ArrayAdapter<>(this, android.R.layout
                .simple_spinner_dropdown_item, clientsList);
        clientSpinner.setAdapter(clientsAdapter);
        clientSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String chosenUser = ctUtils.getSpinnerChosenText(clientSpinner);
                orderList = ctAccount.getAllOpenOrdersList(chosenUser, accountsDB, user);
                System.out.println(orderList.toString());
                normalizedOrderList = normalizeOrderListForSpinner(orderList);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        if (normalizedOrderList != null) {
            showOpenOrders();
        }

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

    void showOpenOrders() {
        ArrayAdapter<String> ordersAdapter = new ArrayAdapter<>(this, android.R.layout
                .simple_spinner_dropdown_item, normalizedOrderList);
        openOrdersSpinner.setAdapter(ordersAdapter);
    }
}