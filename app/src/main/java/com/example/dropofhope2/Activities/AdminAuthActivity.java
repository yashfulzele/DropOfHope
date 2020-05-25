package com.example.dropofhope2.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.dropofhope2.R;

public class AdminAuthActivity extends AppCompatActivity {
    private EditText adminUsernameEt, adminPasswordEt;
    private Button loginBt;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_auth);
        initViews();
        hideProgressBar();
        loginBt.setOnClickListener(v -> {
            String username, password;
            username = adminUsernameEt.getText().toString();
            password = adminPasswordEt.getText().toString();
            if (isValid(username, password)) {
                showProgressBar();
                startActivity(new Intent(getApplicationContext(), AdminMainActivity.class));
                AdminAuthActivity.this.finish();
            }
        });
    }

    private void initViews() {
        adminUsernameEt = findViewById(R.id.admin_username);
        adminPasswordEt = findViewById(R.id.admin_password);
        loginBt = findViewById(R.id.admin_login);
        progressBar = findViewById(R.id.progress_bar_admin);
    }

    private boolean isValid(String username, String password) {
        if (username.equals("yashfulzele")) {
            adminUsernameEt.setError("Username is wrong!");
            return false;
        } else if (password.equals("password003")) {
            adminPasswordEt.setError("Password is wrong!");
            return false;
        }
        return true;
    }

    private void showProgressBar() {
        progressBar.setIndeterminate(true);
        progressBar.setVisibility(View.VISIBLE);
    }

    private void hideProgressBar() {
        progressBar.setVisibility(View.GONE);
    }
}
