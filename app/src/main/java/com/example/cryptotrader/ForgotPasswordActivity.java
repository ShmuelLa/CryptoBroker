package com.example.cryptotrader;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView backToLoginBtn;
    private EditText editTextEmail;
    private Button resetButton;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        editTextEmail = findViewById(R.id.email);
        resetButton = findViewById(R.id.forgotButton);
        progressBar = findViewById(R.id.progressBar);
        backToLoginBtn = findViewById(R.id.backToLogin);
        backToLoginBtn.setOnClickListener(this);
        mAuth = FirebaseAuth.getInstance();
        resetButton.setOnClickListener(this);
        setStatusBarColor();
    }

    @Override
    public void onClick(View view) {
        String emailAddr;
        switch (view.getId()){
            case R.id.forgotButton:
                resetPassword();
                startActivity(new Intent(this, LoginActivity.class));
                break;

            case R.id.backToLogin:
                startActivity(new Intent(this, LoginActivity.class));
                break;
        }
    }

    private void resetPassword() {
        String email = editTextEmail.getText().toString().trim();
        if(email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            editTextEmail.setError("Invalid E-Mail Address!");
            editTextEmail.requestFocus();
            return;
        }
        progressBar.setVisibility(View.VISIBLE);
        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(ForgotPasswordActivity.this,"Check your E-Mail to reset password", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(ForgotPasswordActivity.this, LoginActivity.class));
                }
                else{
                    Toast.makeText(ForgotPasswordActivity.this, "Failed! Please check that the email provided is correct registered to our service", Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
    }

    /**
     * Sets the status bar color to #121212, The apps main background color
     * This is used mainly for cosmetics in order to create an immersive feel while browsing the app
     */
    void setStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.status_bar, this.getTheme()));
        }
        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.status_bar));
        }
    }
}