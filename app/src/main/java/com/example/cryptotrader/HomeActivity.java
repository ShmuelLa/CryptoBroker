package com.example.cryptotrader;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener{

    private ImageButton wallet,chart,add,profile;
    private Button add_button;
    private Dialog mydialog;
    private EditText account_name,key_input,secret_input;
    private DatabaseReference accounts_db;
    private String userId;
    private FirebaseUser user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mydialog = new Dialog(this);
        setContentView(R.layout.activity_home);
        add = findViewById(R.id.add);
        profile = findViewById(R.id.profile);
        wallet = findViewById(R.id.wallet);
        chart = findViewById(R.id.chart);
        wallet.setOnClickListener(this);
        chart.setOnClickListener(this);
        add.setOnClickListener(this);
        profile.setOnClickListener(this);
        user = FirebaseAuth.getInstance().getCurrentUser();
        accounts_db = FirebaseDatabase.getInstance().getReference("Accounts");
//        account_name = findViewById(R.id.account_name);
//        key_input = findViewById(R.id.key_input);
//        secret_input = findViewById(R.id.secret_input);
    }
    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.wallet:
                showpop_wallet(view);
                break;
            case R.id.chart:
                break;
            case R.id.add:
                showpop_add(view);
                break;
            case R.id.profile:
                startActivity(new Intent(HomeActivity.this, ProfileActivity.class));
        }
    }

    private void showpop_wallet(View view) {
        accounts_db.child("Gilad").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                System.out.println(task.getResult().getValue(ctCredentials.class).key);

            }
        });
    }

    private void showpop_add(View view){
        mydialog.setContentView(R.layout.popup_add);
        add_button = mydialog.findViewById(R.id.btnadd);
        account_name = mydialog.findViewById(R.id.account_name_input);
        key_input = mydialog.findViewById(R.id.key_input);
        secret_input = mydialog.findViewById(R.id.secret_input);
        add_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String a_name = account_name.getText().toString().trim();
                String key = key_input.getText().toString().trim();
                String secret = secret_input.getText().toString().trim();
                accounts_db.child(user.getUid()).child(a_name).setValue(new ctCredentials(key,secret)).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(HomeActivity.this, "Account added successfully", Toast.LENGTH_LONG).show();
                            mydialog.dismiss();
                        }
                        else{
                            Toast.makeText(HomeActivity.this, "Something went wrong. Please try again", Toast.LENGTH_LONG).show();

                        }
                    }
                });
            }
        });
        TextView close_pop = mydialog.findViewById(R.id.txtclose);
        close_pop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mydialog.dismiss();
            }
        });
        mydialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mydialog.show();







    }
}