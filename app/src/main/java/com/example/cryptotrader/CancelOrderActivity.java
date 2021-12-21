package com.example.cryptotrader;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.binance.api.client.domain.account.Order;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CancelOrderActivity extends AppCompatActivity {
    private DatabaseReference accountsDB;
    private FirebaseUser user;

    private Spinner clientSpinner, openOrdersSpinner;
    private List<Order> orderList;
    private ArrayList<String> normalizedOrderList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ExecutorService executor = Executors.newSingleThreadExecutor();
        ctAccount currentAccount = new ctAccount(executor);
        setContentView(R.layout.activity_cancel_order);
        clientSpinner = findViewById(R.id.clientSpinner);
        openOrdersSpinner = findViewById(R.id.openOrdersSpinner);
        user = FirebaseAuth.getInstance().getCurrentUser();
        accountsDB = FirebaseDatabase.getInstance().getReference("Accounts");
        ArrayList<String> clientsList = ctAccount.getClientNamesListAsync(accountsDB, user);
        ArrayAdapter<String> clientsAdapter = new ArrayAdapter<>(this, android.R.layout
                .simple_spinner_dropdown_item, clientsList);
        clientSpinner.setAdapter(clientsAdapter);
        createClientsSpinner();
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
                String chosenUser = ctUtils.getSpinnerChosenText(clientSpinner);
                orderList = ctAccount.getAllOpenOrdersListAsync(chosenUser, accountsDB, user);
//                orderList = currentAccount.getAllOpenOrdersList(chosenUser, accountsDB, user);
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

    void showOpenOrders() {
        ArrayAdapter<String> ordersAdapter = new ArrayAdapter<>(this, android.R.layout
                .simple_spinner_dropdown_item, normalizedOrderList);
        openOrdersSpinner.setAdapter(ordersAdapter);
    }
}