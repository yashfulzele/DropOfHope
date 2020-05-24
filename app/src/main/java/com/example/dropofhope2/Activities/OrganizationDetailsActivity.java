package com.example.dropofhope2.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.example.dropofhope2.R;

import java.util.Objects;

public class OrganizationDetailsActivity extends AppCompatActivity {
    private TextView nameTv, contactTv, membersTv, emailTv, addressTv, organizationTypeTv;
    private String name, contact, members, email, address, organizationType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organization_details);
        initViews();
        getValues();
        setValues();
    }

    private void initViews(){
        nameTv = findViewById(R.id.profile_name);
        contactTv = findViewById(R.id.profile_contact);
        membersTv = findViewById(R.id.profile_members);
        emailTv = findViewById(R.id.profile_email);
        addressTv = findViewById(R.id.profile_address);
        organizationTypeTv = findViewById(R.id.profile_organization_type);
    }

    private void getValues(){
        name = Objects.requireNonNull(getIntent().getExtras()).getString("Name");
        contact = getIntent().getExtras().getString("Contact");
        members = getIntent().getExtras().getString("Members");
        email = getIntent().getExtras().getString("Email");
        address = getIntent().getExtras().getString("Address");
        organizationType = getIntent().getExtras().getString("Organization type");
    }

    private void setValues(){
        nameTv.setText(name);
        contactTv.setText(contact);
        membersTv.setText(members);
        emailTv.setText(email);
        addressTv.setText(address);
        organizationTypeTv.setText(organizationType);
    }
}
