package com.example.cryptotrader;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
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

public class CancelOrderActivity extends AppCompatActivity {
    private DatabaseReference accountsDB;
    private FirebaseUser user;
    private Spinner clientSpinner;
    ArrayList<String> clientsList = new ArrayList<>();

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

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }
}