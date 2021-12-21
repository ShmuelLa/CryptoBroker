package com.example.cryptotrader;

import androidx.annotation.NonNull;

import com.binance.api.client.BinanceApiAsyncRestClient;
import com.binance.api.client.BinanceApiCallback;
import com.binance.api.client.BinanceApiClientFactory;
import com.binance.api.client.domain.account.Account;
import com.binance.api.client.domain.account.Order;
import com.binance.api.client.domain.account.request.OrderRequest;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.GenericTypeIndicator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ctAccount {
    ctCredentials credentials;
    String id;
    String clientName;
    int totalUSDT;
    int lockedUSDT;
    int freeUSDT;
    public static List<Order> result = new ArrayList<>();

    public ctAccount(ctCredentials otherCredentials, String name) {
        credentials = otherCredentials;
        clientName = name;
    }

    public static ArrayList<String> getClientNamesList(DatabaseReference db, FirebaseUser user) {
        ArrayList<String> result = new ArrayList<>();
        result.add("All");
        db.child(user.getUid()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(task.isSuccessful()) {
                    GenericTypeIndicator<HashMap<String, ctCredentials>> gType =
                            new GenericTypeIndicator<HashMap<String,  ctCredentials>>() {};
                    HashMap<String,  ctCredentials> map = task.getResult().getValue(gType);
                    map.forEach((clientName, clientTokens) -> result.add(clientName));
                }
            }
        });
        return result;
    }

    public static List<Order> getAllOpenOrdersList(String clientName, DatabaseReference accountsDB, FirebaseUser user) {
        accountsDB.child(user.getUid()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(task.isSuccessful()) {
                    GenericTypeIndicator<HashMap<String, ctCredentials>> gType =
                            new GenericTypeIndicator<HashMap<String,  ctCredentials>>() {};
                    HashMap<String,  ctCredentials> map = task.getResult().getValue(gType);
                    map.forEach((clientName, clientTokens) ->
                    {
                        if (clientName.equals(clientName)) {
                            BinanceApiClientFactory factory = BinanceApiClientFactory
                                    .newInstance(clientTokens.getKey(), clientTokens.getSecret());
                            BinanceApiAsyncRestClient client = factory.newAsyncRestClient();
                            client.getOpenOrders(new OrderRequest(null), new BinanceApiCallback<List<Order>>() {
                                @Override
                                public void onResponse(List<Order> orders) {
                                    result = orders;
                                }
                            });
                        }
                    });
                }
            }
        });
        return result;
    }

//    public static

//    BinanceApiClientFactory factory = BinanceApiClientFactory
//            .newInstance(otherCredentials.getKey(), otherCredentials.getSecret());
//    BinanceApiAsyncRestClient client = factory.newAsyncRestClient();
//        client.getAccount(new BinanceApiCallback<Account>() {
//        @Override
//        public void onResponse(Account account) {
//            System.out.println(clientName + "  " + account.getBalances());
//        }
//    });
}
