package com.example.dropofhope2.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.example.dropofhope2.R;

import java.util.Objects;

public class PersonDetailsActivity extends AppCompatActivity {
    private TextView nameTv, ageTv, sexTv, emailTv, addressTv, contactTv, bloodGroupTv, isDonorTv;
    private String name, age, sex, email, address, contact, bloodGroup, isDonor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_details);
        initViews();
        getValues();
        setValues();
    }

    private void initViews() {
        nameTv = findViewById(R.id.profile_name);
        ageTv = findViewById(R.id.profile_age);
        sexTv = findViewById(R.id.profile_sex);
        emailTv = findViewById(R.id.profile_email);
        addressTv = findViewById(R.id.profile_address);
        contactTv = findViewById(R.id.profile_contact);
        bloodGroupTv = findViewById(R.id.profile_blood_group);
        isDonorTv = findViewById(R.id.profile_Donor);
    }

    private void getValues() {
        name = Objects.requireNonNull(getIntent().getExtras()).getString("Name");
        age = getIntent().getExtras().getString("Age");
        sex = getIntent().getExtras().getString("Sex");
        email = getIntent().getExtras().getString("Email");
        address = getIntent().getExtras().getString("Address");
        contact = getIntent().getExtras().getString("Contact");
        bloodGroup = getIntent().getExtras().getString("Blood group");
        isDonor = getIntent().getExtras().getString("isDonor");
    }

    private void setValues() {
        nameTv.setText(name);
        ageTv.setText(age);
        sexTv.setText(sex);
        emailTv.setText(email);
        addressTv.setText(address);
        contactTv.setText(contact);
        bloodGroupTv.setText(bloodGroup);
        isDonorTv.setText(isDonor);
    }
}
