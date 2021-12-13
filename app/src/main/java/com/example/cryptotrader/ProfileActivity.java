package com.example.cryptotrader;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.widget.EditText;
import android.widget.Toast;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener{
    private Button logOut, tradeButton;
    private FirebaseUser user;
    private DatabaseReference ref;
    private TextView email;
    private String userId;
    private ImageButton wallet;
    private ImageButton chart;
    private ImageButton add;
    private Dialog mydialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        user = FirebaseAuth.getInstance().getCurrentUser();
        ref = FirebaseDatabase.getInstance().getReference("Users");
        userId = user.getUid();
        add = findViewById(R.id.add);
        wallet = findViewById(R.id.wallet);
        chart = findViewById(R.id.chart);
        wallet.setOnClickListener(this);
        chart.setOnClickListener(this);
        add.setOnClickListener(this);
        mydialog = new Dialog(this);
        setContentView(R.layout.activity_profile);
        tradeButton = findViewById(R.id.tradebutton);
        tradeButton.setOnClickListener(this);

        ref.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User userProfile = snapshot.getValue(User.class);
                if(userProfile != null){
//                    String emailStr = userProfile.email;
//                    email.setText(emailStr);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ProfileActivity.this, "Something went wrong!", Toast.LENGTH_LONG).show();
            }
        });
//        logOut.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.logOut:
//                FirebaseAuth.getInstance().signOut();
//                startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
                break;
            case R.id.wallet:
                break;
            case R.id.chart:
                break;
            case R.id.add:
                showpop(view);
                break;
            case R.id.tradebutton:
                startActivity(new Intent(this, TraderActivity.class));
                break;
        }
    }
    private void showpop(View view){
        mydialog.setContentView(R.layout.popup);
        TextView close_pop = mydialog.findViewById(R.id.txtclose);
        close_pop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mydialog.dismiss();
            }
        });
        mydialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mydialog.show();
        String given_key =  findViewById(R.id.key_input).toString().trim();
        String given_secret = findViewById(R.id.secret_input).toString().trim();


    }
}