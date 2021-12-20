package com.example.cryptotrader;

import com.binance.api.client.BinanceApiAsyncRestClient;
import com.binance.api.client.BinanceApiCallback;
import com.binance.api.client.BinanceApiClientFactory;
import com.binance.api.client.domain.account.Account;

public class ctAccount {
    ctCredentials credentials;
    String id;
    String clientName;
    int totalUSDT;
    int lockedUSDT;
    int freeUSDT;

    public ctAccount(ctCredentials otherCredentials, String name) {
        credentials = otherCredentials;
        clientName = name;
    }

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
