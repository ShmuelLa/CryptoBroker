package com.example.cryptotrader;

public class ctAccount {
    String _id;
    String _api_key;
    String _api_secret;
    String _client_name;
    int _total_usdt;
    int _locked_usdt;
    int _free_usdt;

    public ctAccount(String key, String secret) {
        _api_key = key;
        _api_secret = secret;
    }
}
