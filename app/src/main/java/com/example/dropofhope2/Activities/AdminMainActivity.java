package com.example.dropofhope2.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.dropofhope2.R;

public class AdminMainActivity extends AppCompatActivity {
    private Button usersBt, reportsBt, feedbackBt;
    private TextView adminLogOutTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_main);
        initViews();
        usersBt.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), AdminUsersActivity.class)));
        reportsBt.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), AdminReportsActivity.class)));
        feedbackBt.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), AdminFeedbackActivity.class)));
        adminLogOutTv.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            AdminMainActivity.this.finish();
        });
    }

    private void initViews() {
        usersBt = findViewById(R.id.users);
        reportsBt = findViewById(R.id.reports);
        feedbackBt = findViewById(R.id.feedback);
        adminLogOutTv = findViewById(R.id.admin_log_out);
    }
}
