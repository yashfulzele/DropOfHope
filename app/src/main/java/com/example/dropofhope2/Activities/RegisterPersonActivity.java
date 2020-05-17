package com.example.dropofhope2.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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

public class RegisterPersonActivity extends AppCompatActivity {
    private EditText personNameEt, personContactEt, personAddressEt, personAgeEt, emailEt, passwordEt;
    private RadioButton maleRd, femaleRd;
    private RadioGroup sexRg;
    private Spinner bloodGroupSp, isDonorSp;
    private Button registerBt;
    private SharedPreferences sharedPreferences;
    private static final String MyPREFERENCES = "MyPrefs";
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_person);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = database.getReference("Users/Person");
        mAuth = FirebaseAuth.getInstance();
        initViews();
        sharedPreferences = getApplicationContext().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        registerBt.setOnClickListener(v -> {
            String sex = "";
            final String name, age, contact_no, address, blood_group, isDonor, email, password;
            name = personNameEt.getText().toString();
            age = personAgeEt.getText().toString();
            contact_no = personContactEt.getText().toString();
            address = personAddressEt.getText().toString();
            blood_group = bloodGroupSp.getSelectedItem().toString();
            isDonor = isDonorSp.getSelectedItem().toString();
            email = emailEt.getText().toString();
            password = passwordEt.getText().toString();
            if (maleRd.isChecked()) {
                sex = "Male";
            } else if (femaleRd.isChecked()) {
                sex = "Female";
            }
            if (isValid(name, age, sex, address, contact_no, blood_group, isDonor)) {
                String finalSex = sex;
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                String uid = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("Type", "Person");
                                editor.putString("Name", name);
                                editor.apply();
                                databaseReference.child(uid).child("Name").setValue(name);
                                databaseReference.child(uid).child("Age").setValue(age);
                                databaseReference.child(uid).child("Sex").setValue(finalSex);
                                databaseReference.child(uid).child("Address").setValue(address);
                                databaseReference.child(uid).child("Contact").setValue(contact_no);
                                databaseReference.child(uid).child("Blood group").setValue(blood_group);
                                databaseReference.child(uid).child("isDonor").setValue(isDonor);
                                databaseReference.child(uid).child("Id").setValue(uid);
                                UpdateUI();
                                showMessage("Registered user...");
                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                RegisterPersonActivity.this.finish();
                            } else {
                                if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                    emailEt.setError("Email already in use");
                                    showMessage("Email already in use");
                                } else {
                                    showMessage("Something went wrong :(");
                                }
                            }
                        });
            }
        });
    }

    private boolean isValid(String name, String age, String sex, String address, String contact_no, String blood_group, String isDonor) {
        List<String> valid_blood_groups = new ArrayList<>();
        valid_blood_groups.add("O+");
        valid_blood_groups.add("O-");
        valid_blood_groups.add("A+");
        valid_blood_groups.add("A-");
        valid_blood_groups.add("B+");
        valid_blood_groups.add("B-");
        valid_blood_groups.add("AB+");
        valid_blood_groups.add("AB-");
        List<String> yes_no = new ArrayList<>();
        yes_no.add("Yes");
        yes_no.add("No");
        if (name.isEmpty()) {
            personNameEt.setError("Name field is required.");
            return false;
        } else if (age.isEmpty()) {
            personAgeEt.setError("Age field is empty.");
            return false;
        } else if (sex.isEmpty()) {
            maleRd.setError("Not selected anything");
            femaleRd.setError("Not selected anything");
            return false;
        } else if (address.isEmpty()) {
            personAddressEt.setError("Address field is empty.");
            return false;
        } else if (contact_no.length() != 10) {
            personContactEt.setError("Contact number is required.");
            return false;
        } else if (!valid_blood_groups.contains(blood_group)) {
            showMessage("Select your blood group!");
            return false;
        } else if (!yes_no.contains(isDonor)) {
            showMessage("Are you a donor?");
            return false;
        } else if (!validateEmailAddress() | !validatePassword()) {
            return false;
        }
        return true;
    }

    private boolean validateEmailAddress() {
        String email = emailEt.getText().toString().trim();
        if (email.isEmpty()) {
            emailEt.setError("Email is required. Can't be empty.");
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEt.setError("Invalid Email. Enter valid email address.");
            return false;
        } else {
            emailEt.setError(null);
            return true;
        }
    }

    private boolean validatePassword() {

        String password = passwordEt.getText().toString().trim();

        if (password.isEmpty()) {
            passwordEt.setError("Password is required. Can't be empty.");
            return false;
        } else if (password.length() < 6) {
            passwordEt.setError("Password length short. Minimum 6 characters required.");
            return false;
        } else {
            passwordEt.setError(null);
            return true;
        }
    }

    private void initViews() {
        personNameEt = findViewById(R.id.person_name);
        personContactEt = findViewById(R.id.person_contact);
        personAddressEt = findViewById(R.id.person_address);
        personAgeEt = findViewById(R.id.person_age);
        sexRg = findViewById(R.id.person_sex);
        maleRd = findViewById(R.id.radio_male);
        femaleRd = findViewById(R.id.radio_female);
        emailEt = findViewById(R.id.person_email);
        passwordEt = findViewById(R.id.person_password);
        bloodGroupSp = findViewById(R.id.person_blood_group);
        isDonorSp = findViewById(R.id.person_isDonor);
        registerBt = findViewById(R.id.person_register);
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