package com.example.dropofhope2.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Toast;

import com.example.dropofhope2.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class FeedBackActivity extends AppCompatActivity {
    private RadioButton bt1, bt2, bt3, bt4, bt5;
    private Button submitFeedbackBt;
    private ProgressBar progressBar;
    private DatabaseReference db_ref;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_back);
        db_ref = FirebaseDatabase.getInstance().getReference("Feedback");
        mAuth = FirebaseAuth.getInstance();
        initViews();
        String type = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("Type", "Person");
        hideProgressBar();
        if (mAuth.getCurrentUser() != null) {
            submitFeedbackBt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showProgressBar();
                    String feedback = "";
                    boolean valid = true;
                    if (bt1.isChecked()) {
                        feedback = "Very bad";
                    } else if (bt2.isChecked()) {
                        feedback = "Not bad";
                    } else if (bt3.isChecked()) {
                        feedback = "Good";
                    } else if (bt4.isChecked()) {
                        feedback = "Very bad";
                    } else if (bt5.isChecked()) {
                        feedback = "Excellent";
                    } else {
                        showMessage("Select option as per your experience");
                        valid = false;
                    }
                    if (valid) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        String uid = user.getUid();
                        Map<String, String> map = new HashMap<>();
                        map.put("Email", user.getEmail());
                        map.put("Feedback", feedback);
                        db_ref.child(uid).setValue(map);
                        hideProgressBar();
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        FeedBackActivity.this.finish();
                    }
                }
            });
        }
    }

    private void initViews() {
        bt1 = findViewById(R.id.bt_1);
        bt2 = findViewById(R.id.bt_2);
        bt3 = findViewById(R.id.bt_3);
        bt4 = findViewById(R.id.bt_4);
        bt5 = findViewById(R.id.bt_5);
        submitFeedbackBt = findViewById(R.id.submit_feedback);
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

}
