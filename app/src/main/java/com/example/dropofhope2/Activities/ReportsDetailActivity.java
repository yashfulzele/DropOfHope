package com.example.dropofhope2.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.dropofhope2.R;
import com.squareup.picasso.Picasso;

import java.net.Inet4Address;
import java.util.Objects;

public class ReportsDetailActivity extends AppCompatActivity {
    private TextView nameTv, emailTv, messageTv;
    private ImageView imageView;
    private String name, email, message, imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reports_detail);
        initViews();
        getValues();
        setValues();
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), FullImageActivity.class);
                intent.putExtra("Image uri", Objects.requireNonNull(getIntent().getExtras()).getString("Image uri", null));
                startActivity(intent);
            }
        });
    }

    private void initViews() {
        nameTv = findViewById(R.id.profile_name);
        emailTv = findViewById(R.id.profile_email);
        messageTv = findViewById(R.id.profile_message);
        imageView = findViewById(R.id.profile_image);
    }

    private void getValues() {
        name = Objects.requireNonNull(getIntent().getExtras()).getString("Name", null);
        email = getIntent().getExtras().getString("Email", null);
        message = getIntent().getExtras().getString("Message");
        imageUri = getIntent().getExtras().getString("Image uri", null);
    }

    private void setValues() {
        nameTv.setText(name);
        emailTv.setText(email);
        messageTv.setText(message);
        Picasso.get()
                .load(imageUri)
                .fit()
                .centerCrop()
                .into(imageView);
    }
}
