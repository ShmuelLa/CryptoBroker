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

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText editTextEmail, editTextPassword;
    private TextView register, forgotPassword;
    private Button login;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        editTextEmail = findViewById(R.id.email);
        editTextPassword = findViewById(R.id.password);
        forgotPassword = findViewById(R.id.forgotpassword);
        forgotPassword.setOnClickListener(this);
        register = findViewById(R.id.register);
        register.setOnClickListener(this);
        login = findViewById(R.id.loginButton);
        login.setOnClickListener(this);
        progressBar = findViewById(R.id.progressBar);
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.loginButton:
                String[] emailAndPass = validateEmailAndPass();
                if (emailAndPass == null){
                    break;
                }
                showProgression(true);
                userLogin(emailAndPass[0],emailAndPass[1]);

                break;
            case R.id.register:
                startActivity(new Intent(this, RegisterActivity.class));
                break;
            case R.id.forgotpassword:
                startActivity(new Intent(this, ForgotPasswordActivity.class));
                break;

        }
    }
    private void showProgression(boolean show){
        if (show){
            progressBar.setVisibility(View.VISIBLE);
        }
        if(!show){
            progressBar.setVisibility(View.GONE);
        }
    }
    private String[] validateEmailAndPass(){
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            editTextEmail.setError("Invalid E-Mail address! Please provide a valid address");
            editTextEmail.requestFocus();
        }
        if (password.length() < 6){
            editTextPassword.setError("Invalid Password! Please provide at least 6 characters");
            editTextPassword.requestFocus();
            return null;
        }
        return new String[] {email,password};
    }
    /*
    * 0  success
    * 1 need to verify email
    * 2 failure
    * */
    private void announceSuccess(int event){
        showProgression(false);
        if (event == 0){
            return;
        }
        notifyFailure(event);

    }
    private void notifyFailure(int event){
        String txt = "";
        if (event == 1){
            txt = "Verification mail sent again. Please check your e-mail";
        }
        if (event == 2){
            txt = "Failed to Login! Please try again";
        }
        Toast.makeText(LoginActivity.this
                , txt
                ,Toast.LENGTH_LONG).show();
    }

    private void userLogin(String email,String password) {


        mAuth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    if (user.isEmailVerified()) {
                        announceSuccess(0);
                        startActivity(new Intent(LoginActivity.this, HomeActivity.class));

                    }
                    else {
                        user.sendEmailVerification();
                        announceSuccess(1);

                    }
                }
                else {
                    announceSuccess(2);

                }
            }
        });
    }
}