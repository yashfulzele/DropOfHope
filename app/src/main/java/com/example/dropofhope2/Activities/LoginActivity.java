package com.example.dropofhope2.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dropofhope2.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {
    private EditText userEmailEt, userPasswordEt;
    private TextView registerTv, adminTv;
    private RadioButton orgRb, personRb;
    private Button loginBt;
    private ProgressBar progressBar;

    private FirebaseAuth mAuth;
    private DatabaseReference dbRef;

    private SharedPreferences sharedPreferences;
    private static final String MyPREFERENCES = "MyPrefs";
    private static final String TAG = "MyTag";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initViews();
        dbRef = FirebaseDatabase.getInstance().getReference("Users");
        sharedPreferences = getApplicationContext().getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        mAuth = FirebaseAuth.getInstance();
        loginBt.setOnClickListener(this::LoginUser);
        registerTv.setOnClickListener(this::RegisterUser);
        adminTv.setOnClickListener(this::AdminAuth);
        hideProgressBar();
    }

    private void AdminAuth(View view) {
        startActivity(new Intent(LoginActivity.this, AdminAuthActivity.class));
    }

    private void LoginUser(View view) {
        if ((!validateEmailAddress() | !validatePassword()) && !radioBtClicked()) {
            return;
        }
        String email = userEmailEt.getText().toString();
        String password = userPasswordEt.getText().toString();
        String type = "";
        if (orgRb.isChecked()) {
            type = "Organization";
        } else if (personRb.isChecked()) {
            type = "Person";
        }
        dbRef = dbRef.child(type);
        showProgressBar();
        String finalType = type;
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        hideProgressBar();
                        showMessage("Logged in...");
                        FirebaseUser user = mAuth.getCurrentUser();
                        assert user != null;
                        String uid = user.getUid();
                        dbRef = dbRef.child(uid);
                        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                String name = dataSnapshot.child("Name").getValue(String.class);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("Type", finalType);
                                editor.putString("Name", name);
                                editor.apply();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Log.d(TAG, "dbRef.addListenerForSingleValueEvent: error" + databaseError.getMessage());
                            }
                        });
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
                });
    }

    private void RegisterUser(View view) {
        startActivity(new Intent(LoginActivity.this, OrganizationOrPersonActivity.class));
    }

    private boolean radioBtClicked() {
        if(!orgRb.isChecked() && !personRb.isChecked()){
            showMessage("Click the user type!");
            return false;
        }
        return true;
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
        orgRb = findViewById(R.id.type_organization);
        personRb = findViewById(R.id.type_person);
        loginBt = findViewById(R.id.user_login);
        registerTv = findViewById(R.id.user_register);
        adminTv = findViewById(R.id.admin);
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
