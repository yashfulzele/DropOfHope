package com.example.dropofhope2.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import com.example.dropofhope2.R;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    private Button makeRequestBt, showRequestBt, reportBt, feedbackBt, logOutBt;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        initViews();
        if (mAuth.getCurrentUser() != null) {
            makeRequestBt.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), MakeRequestActivity.class)));
            showRequestBt.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), ShowRequestActivity.class)));
            reportBt.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), ReportActivity.class)));
            feedbackBt.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), FeedBackActivity.class)));
            logOutBt.setOnClickListener(v -> {
                mAuth.signOut();
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                MainActivity.this.finish();
            });
        } else {
            showMessage("Something wrong going on!");
        }
    }

    private void initViews() {
        makeRequestBt = findViewById(R.id.make_request);
        showRequestBt = findViewById(R.id.show_requests);
        reportBt = findViewById(R.id.report);
        feedbackBt = findViewById(R.id.feedback);
        logOutBt = findViewById(R.id.logOut);
    }

    private void showMessage(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}
