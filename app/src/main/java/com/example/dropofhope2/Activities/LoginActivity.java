package com.example.dropofhope2.Activities;

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

import com.example.dropofhope2.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    private EditText userEmailEt, userPasswordEt;
    private TextView registerTv;
    private Button loginBt;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initViews();
        mAuth = FirebaseAuth.getInstance();
        loginBt.setOnClickListener(this::LoginUser);
        registerTv.setOnClickListener(this::RegisterUser);
        hideProgressBar();
    }

    private void LoginUser(View view) {
        if (!validateEmailAddress() | !validatePassword()) {
            return;
        }
        String email = userEmailEt.getText().toString();
        String password = userPasswordEt.getText().toString();
        showProgressBar();
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            hideProgressBar();
                            showMessage("Logged in...");
                            UpdateUI();
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            LoginActivity.this.finish();
                        } else {
                            hideProgressBar();
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                userPasswordEt.setError("Invalid password");
                                showMessage("Invalid Password");
                            } else if (task.getException() instanceof FirebaseAuthInvalidUserException) {
                                userEmailEt.setError("Email not in use");
                                showMessage("Email not in use");
                            }
                        }
                    }
                });
    }

    private void RegisterUser(View view) {
        startActivity(new Intent(LoginActivity.this, OrganizationOrPersonActivity.class));
    }

    private boolean validateEmailAddress() {
        String email = userEmailEt.getText().toString();
        if (email.isEmpty()) {
            userEmailEt.setError("Email is required. Can't be empty.");
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            userEmailEt.setError("Invalid Email. Enter valid email address.");
            return false;
        } else {
            userEmailEt.setError(null);
            return true;
        }
    }

    private boolean validatePassword() {

        String password = userPasswordEt.getText().toString();

        if (password.isEmpty()) {
            userPasswordEt.setError("Password is required. Can't be empty.");
            return false;
        } else if (password.length() < 6) {
            userPasswordEt.setError("Password length short. Minimum 6 characters required.");
            return false;
        } else {
            userPasswordEt.setError(null);
            return true;
        }
    }

    private void initViews() {
        userEmailEt = findViewById(R.id.user_email);
        userPasswordEt = findViewById(R.id.user_password);
        loginBt = findViewById(R.id.user_login);
        registerTv = findViewById(R.id.user_register);
        progressBar = findViewById(R.id.progress_bar);
    }

    private void showMessage(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    private void showProgressBar() {
        progressBar.setIndeterminate(true);
        progressBar.setVisibility(View.VISIBLE);
    }

    private void hideProgressBar() {
        progressBar.setVisibility(View.GONE);
    }

    private void UpdateUI() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            return;
        } else {
            String user_logged = user.getEmail();
            showMessage("Hi!" + user_logged);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        UpdateUI();
        if (mAuth.getCurrentUser() != null) {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            LoginActivity.this.finish();
        }
    }
}
