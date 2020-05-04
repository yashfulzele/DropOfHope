package com.example.dropofhope2.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.example.dropofhope2.R;

public class OrganizationOrPersonActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organization_or_person);
        LinearLayout personLy = findViewById(R.id.person);
        LinearLayout organizationLy = findViewById(R.id.organization);
        personLy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(OrganizationOrPersonActivity.this, RegisterPersonActivity.class));
                OrganizationOrPersonActivity.this.finish();
            }
        });
        organizationLy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(OrganizationOrPersonActivity.this, RegisterOrganizationActivity.class));
                OrganizationOrPersonActivity.this.finish();
            }
        });
    }
}
