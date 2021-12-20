package com.example.cryptotrader;
import java.util.LinkedList;
import java.util.List;

public class ctCredentials {
    private String _key;
    private String _secret;

    public ctCredentials(String key, String secret){
        this._key = key;
        this._secret = secret;
    }

    public String getKey() {
        return this._key;
    }

    public String getSecret() {
        return this._secret;
    }
}
