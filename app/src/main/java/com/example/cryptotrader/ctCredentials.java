package com.example.cryptotrader;
import java.util.LinkedList;
import java.util.List;

public class ctCredentials {
    public String key = "6soWfMT3L1eMYjYB8g2hocIRNZEgJFLtjtPxLnOa3cRnVUT8wUO6LWTjoOBwyqQl";
    public String secret = "Fn4ADF8zPJzEmKiObizu7XIIzKdm6QRzrYpmklb5AFaA2G0CAYBN4LioDLqVzRvl";
    private List<String> keys;
    private List<String> secrets;
    public ctCredentials(){
        keys = new LinkedList<>();
        secrets = new LinkedList<>();
    }

    public ctCredentials(String key, String secret){
        this.key = key;
        this.secret = secret;
    }
    public void addAccount(String key, String secret){
        try{
            if(! keys.contains(key) && ! secrets.contains(secret)){
                keys.add(key);
                secrets.add(secret);
            }else if( keys.contains(key)){
                throw new Exception("given key is present in this account");
            }else if(secrets.contains(secret)){
                throw new Exception("secret key is present in this account");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public String getKey(int index){
        try{
            if(keys.get(index) != null){
                return keys.get(index);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
    public String getSecret(int index){
        try{
            if(secrets.get(index) != null){
                return secrets.get(index);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

}
