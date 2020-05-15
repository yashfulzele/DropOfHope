package com.example.dropofhope2.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.dropofhope2.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RegisterOrganizationActivity extends AppCompatActivity {
    private EditText orgNameEt, orgContactEt, orgMembersEt, orgAddressEt, orgEmailEt, orgPasswordEt;
    private Spinner orgChooseSp;
    private Button orgRegister;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_organization);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = database.getReference("Users/Organization");
        mAuth = FirebaseAuth.getInstance();
        initViews();
        hideProgressBar();
        orgRegister.setOnClickListener(v -> {
            final String orgName, orgContact, orgMembers, orgAddress, orgEmail, orgPassword, orgChoose;
            orgName = orgNameEt.getText().toString();
            orgContact = orgContactEt.getText().toString();
            orgMembers = orgMembersEt.getText().toString();
            orgAddress = orgAddressEt.getText().toString();
            orgEmail = orgEmailEt.getText().toString();
            orgPassword = orgPasswordEt.getText().toString();
            orgChoose = orgChooseSp.getSelectedItem().toString();
            showProgressBar();
            if (isValid(orgName, orgContact, orgMembers, orgAddress, orgChoose)) {
                mAuth.createUserWithEmailAndPassword(orgEmail, orgPassword)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                String uid = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
                                PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("Type", "Organization").apply();
                                PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("Name", orgName).apply();
                                databaseReference.child(uid).child("Name").setValue(orgName);
                                databaseReference.child(uid).child("Members").setValue(orgMembers);
                                databaseReference.child(uid).child("Address").setValue(orgAddress);
                                databaseReference.child(uid).child("Email").setValue(orgEmail);
                                databaseReference.child(uid).child("Organization type").setValue(orgChoose);
                                databaseReference.child(uid).child("Contact").setValue(orgContact);
                                databaseReference.child(uid).child("Id").setValue(uid);
                                UpdateUI();
                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                RegisterOrganizationActivity.this.finish();
                            } else {
                                hideProgressBar();
                                if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                    orgEmailEt.setError("Email already in use");
                                    showMessage("Email already in use");
                                } else {
                                    showMessage("Something went wrong :(");
                                }
                            }
                        });
            }
        });
    }

    private boolean isValid(String orgName, String orgContact, String orgMembers, String orgAddress, String orgChoose) {
        List<String> orgType = new ArrayList<>();
        orgType.add("Hospital");
        orgType.add("Blood Donation Camp");
        orgType.add("Any other");
        if (orgName.isEmpty()) {
            orgNameEt.setError("Name field is required.");
            return false;
        } else if (orgContact.length() != 10) {
            orgContactEt.setError("Invalid contact information.");
            return false;
        } else if (orgMembers.isEmpty()) {
            orgMembersEt.setError("Total number of members.");
            return false;
        } else if (orgAddress.isEmpty()) {
            orgAddressEt.setError("Address field is empty.");
            return false;
        } else if (!orgType.contains(orgChoose)) {
            showMessage("Choose the type of organization you are!");
            return false;
        } else if (!validateEmailAddress() | !validatePassword()) {
            return false;
        }
        return true;
    }

    private boolean validateEmailAddress() {
        String email = orgEmailEt.getText().toString().trim();
        if (email.isEmpty()) {
            orgEmailEt.setError("Email is required. Can't be empty.");
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            orgEmailEt.setError("Invalid Email. Enter valid email address.");
            return false;
        } else {
            orgEmailEt.setError(null);
            return true;
        }
    }

    private boolean validatePassword() {

        String password = orgPasswordEt.getText().toString().trim();

        if (password.isEmpty()) {
            orgPasswordEt.setError("Password is required. Can't be empty.");
            return false;
        } else if (password.length() < 6) {
            orgPasswordEt.setError("Password length short. Minimum 6 characters required.");
            return false;
        } else {
            orgPasswordEt.setError(null);
            return true;
        }
    }

    private void showProgressBar() {
        progressBar.setIndeterminate(true);
        progressBar.setVisibility(View.VISIBLE);
    }

    private void hideProgressBar() {
        progressBar.setVisibility(View.GONE);
    }

    private void initViews() {
        orgNameEt = findViewById(R.id.organization_name);
        orgContactEt = findViewById(R.id.organization_contact);
        orgMembersEt = findViewById(R.id.organization_members);
        orgAddressEt = findViewById(R.id.organization_address);
        orgEmailEt = findViewById(R.id.organization_email);
        orgPasswordEt = findViewById(R.id.organization_password);
        orgChooseSp = findViewById(R.id.organization_choose);
        orgRegister = findViewById(R.id.organization_register);
        progressBar = findViewById(R.id.progress_bar);
    }

    private void UpdateUI() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            return;
        } else {
            showMessage("Hi!" + user.getEmail());
        }
    }

    private void showMessage(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}