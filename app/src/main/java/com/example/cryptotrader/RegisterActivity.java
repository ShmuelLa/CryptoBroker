package com.example.cryptotrader;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener{
    private TextView registered;
    private Button registerButton;
    private ProgressBar progressBar;
    private EditText editTextEmail, editTextPassword, editTextPasswordValidation;
    private FirebaseAuth mAuth;
    private static final int PASS_LENGTH = 6 ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mAuth = FirebaseAuth.getInstance();
        editTextEmail = findViewById(R.id.email);
        editTextPassword = findViewById(R.id.password);
        editTextPasswordValidation = findViewById(R.id.passwordvalidation);
        registered = findViewById(R.id.registered);
        registered.setOnClickListener(this);
        registerButton = findViewById(R.id.registerButton);
        registerButton.setOnClickListener(this);
        progressBar = findViewById(R.id.progressBar);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.registered:
                startActivity(new Intent(this, LoginActivity.class));
                break;
            case R.id.registerButton:
                String[] emailAndPass = validateEmailAndPass();
                if(emailAndPass == null){
                    break;
                }
                showProgression(true);
                registerUser(emailAndPass[0],emailAndPass[1]);
        }
    }
    /*take an advice from other group devs if splitting this function is the right thing to do*/
    private String[] validateEmailAndPass(){
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String passwordValidation = editTextPasswordValidation.getText().toString().trim();
        if(email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            editTextEmail.setError("Invalid E-Mail address! Please provide a valid address");
            editTextEmail.requestFocus();
        }
        if(password.length() < PASS_LENGTH){
            editTextPassword.setError("Invalid Password! Please provide at least 6 characters");
            editTextPassword.requestFocus();
            return null;
        }
        if(!password.equals(passwordValidation)){
            editTextPasswordValidation.setError("Re-Entered Password does not match the above");
            editTextPasswordValidation.requestFocus();
            return null;
        }
        return new String[] {email,password};
    }


    private void showProgression(boolean show){
        if (show){
            progressBar.setVisibility(View.VISIBLE);
        }
        if(!show){
            progressBar.setVisibility(View.GONE);
        }
    }


    private void announceSuccess(boolean success){
        showProgression(false);
        if(!success){
            Toast.makeText(RegisterActivity.this, "Registration Failed!", Toast.LENGTH_LONG).show();
        }
        if(success){
            Toast.makeText(RegisterActivity.this, "User Registered Successfully! Please verify your e-mail address",
                    Toast.LENGTH_LONG).show();
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
        }


    }


    private void registerUser(String email,String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(task.isSuccessful()){
                            ctUser ctUser = new ctUser(email);

                            FirebaseDatabase.getInstance().getReference("Users")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(ctUser).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        announceSuccess(true);
                                        FirebaseUser u = mAuth.getCurrentUser();
                                        u.sendEmailVerification();

                                    }
                                    else{
                                        announceSuccess(false);

                                    }
                                }
                            });
                        }
                        else{
                            announceSuccess(false);
                        }
                    }
                });
    }
}