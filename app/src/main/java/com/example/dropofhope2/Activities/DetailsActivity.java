package com.example.dropofhope2.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dropofhope2.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class DetailsActivity extends AppCompatActivity {
    private ImageView profileImageIv;
    private TextView profileNameTv, profileContactTv, profileMessageTv, profileEmailTv, profileBloodGroupTv;
    private Button getLocationBt;
    private DatabaseReference db_ref2;
    private ValueEventListener mDBListener2;
    private String Address;
    private static final String TAG = "myTag";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        initViews();
        Log.d(TAG, "You are in Details Activity");
        String type = Objects.requireNonNull(getIntent().getExtras()).getString("Type");
        Log.d(TAG, "The type value is : " + type);
        assert type != null;
        db_ref2 = FirebaseDatabase.getInstance().getReference("Users").child(type);
        String uid = Objects.requireNonNull(getIntent().getExtras()).getString("Id");
        Log.d(TAG, "The uid from last intent is : " + uid);
        Picasso.get()
                .load(getIntent().getExtras().getString("Image uri"))
                .fit()
                .centerCrop()
                .into(profileImageIv);
        profileNameTv.setText(getIntent().getExtras().getString("Name"));
        profileMessageTv.setText(getIntent().getExtras().getString("Message"));
        assert uid != null;
        mDBListener2 = db_ref2.child(uid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Map<String, String> upload = new HashMap<>();
                        upload.put("Contact", dataSnapshot.child("Contact").getValue(String.class));
                        upload.put("Address", dataSnapshot.child("Address").getValue(String.class));
                        if (type.equals("Person")) {
                            upload.put("Blood group", dataSnapshot.child("Blood group").getValue(String.class));
                            upload.put("isDonor", dataSnapshot.child("isDonor").getValue(String.class));
                        } else {
                            upload.put("Blood group", "-----");
                            upload.put("isDonor", "-----");
                        }
                        upload.put("Email", dataSnapshot.child("Email").getValue(String.class));
                        profileContactTv.setText(upload.get("Contact"));
                        profileBloodGroupTv.setText(upload.get("Blood group"));
                        profileEmailTv.setText(upload.get("Email"));
                        Address = upload.get("Address");
                        Log.d(TAG, "Address : " + Address);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.d(TAG, "onCancelled error : " + databaseError.getMessage());
                        showMessage(databaseError.getMessage());
                    }
                });
        getLocationBt.setOnClickListener(v -> {
            Intent intent = new Intent(DetailsActivity.this, MapActivity.class);
            intent.putExtra("Address", Address);
            startActivity(intent);
        });
        profileImageIv.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), FullImageActivity.class);
            intent.putExtra("Image uri", Objects.requireNonNull(getIntent().getExtras()).getString("Image uri", null));
            startActivity(intent);
        });
    }

    private void initViews() {
        profileImageIv = findViewById(R.id.profile_image);
        profileNameTv = findViewById(R.id.profile_name);
        profileEmailTv = findViewById(R.id.profile_email);
        profileContactTv = findViewById(R.id.profile_contact);
        profileMessageTv = findViewById(R.id.profile_message);
        profileBloodGroupTv = findViewById(R.id.profile_blood_group);
        getLocationBt = findViewById(R.id.get_location);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        db_ref2.removeEventListener(mDBListener2);
    }

    private void showMessage(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}
