package com.example.cryptotrader;

import androidx.annotation.NonNull;

import com.binance.api.client.BinanceApiAsyncRestClient;
import com.binance.api.client.BinanceApiCallback;
import com.binance.api.client.BinanceApiClientFactory;
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
import java.util.concurrent.Executor;

public class ctAccount {
    private final Executor executor;
    ctCredentials credentials;
    String id;
    String clientName;
    private ArrayList<String> resultSync = new ArrayList<>();
    int totalUSDT;
    int lockedUSDT;
    int freeUSDT;
    public static List<Order> result = new ArrayList<>();

    public ctAccount(Executor executor) {
        this.executor = executor;
    }

    public ctAccount(Executor executor, ctCredentials otherCredentials, String name) {
        this.executor = executor;
        credentials = otherCredentials;
        clientName = name;
    }

    /**
     * -= Async Task May cause inconsistent performance when used incorrectly =-
     * Returns a list of current client names as String.
     * This method is for use on spinners and other methods that require a String representation
     * of client name.
     *
     * @param db FireBase DB object of the current connected user
     * @param user FireBase user object of the current connected user
     * @return ArrayList of name representing the current user clients
     */
    public static ArrayList<String> getClientNamesListAsync(DatabaseReference db, FirebaseUser user) {
        ArrayList<String> result = new ArrayList<>();
        result.add("All");
        db.child(user.getUid()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(task.isSuccessful()) {
                    GenericTypeIndicator<HashMap<String, ctCredentials>> gType =
                            new GenericTypeIndicator<HashMap<String,  ctCredentials>>() {};
                    HashMap<String, ctCredentials> map = task.getResult().getValue(gType);
                    map.forEach((clientName, clientTokens) -> result.add(clientName));
                }
            }
        });
        return result;
    }

    public ArrayList<String> getClientNamesList(DatabaseReference db, FirebaseUser user) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                resultSync.add("All");
                db.child(user.getUid()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if(task.isSuccessful()) {
                            GenericTypeIndicator<HashMap<String, ctCredentials>> gType =
                                    new GenericTypeIndicator<HashMap<String,  ctCredentials>>() {};
                            HashMap<String,  ctCredentials> map = task.getResult().getValue(gType);
                            map.forEach((clientName, clientTokens) -> resultSync.add(clientName));
                        }
                    }
                });
            }
        });
        return resultSync;
    }

    /**
     * -= Async Task May cause inconsistent performance when used incorrectly =-
     * Returns a List of BinanceAPI Order objects the are currently open for a specific requested
     * user (via username).
     *
     * @param clientName The current client name as shown on a spinner for example
     * @param accountsDB FireBase DB object of the current connected user
     * @param user FireBase user object of the current connected user
     * @return List of BinanceAPI Order objects
     */
    public static List<Order> getAllOpenOrdersListAsync(String clientName, DatabaseReference accountsDB, FirebaseUser user) {
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
