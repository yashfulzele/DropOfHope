package com.example.dropofhope2.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class DetailsActivity extends AppCompatActivity {
    private ImageView profileImageIv;
    private TextView profileNameTv, profileContactTv, profileMessageTv;
    private Button getLocationBt;
    private StorageReference mStorageRef;
    private DatabaseReference db_ref2;
    private ValueEventListener mDBListener2;
    private Map<String, String> map;
    private String Address;
    private static final String TAG = "myTag";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        initViews();
        Log.d(TAG, "You are in Details Activity");
        String type = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("Type", "Org");
        Log.d(TAG, "preference manager stored the type value as : " + type);
        db_ref2 = FirebaseDatabase.getInstance().getReference("Users").child(type);
        String uid = Objects.requireNonNull(getIntent().getExtras()).getString("Id");
        Log.d(TAG, "The uid from last intent is : "+uid);
        Picasso.get()
                .load(getIntent().getExtras().getString("Image uri"))
                .fit()
                .centerCrop()
                .into(profileImageIv);
        profileNameTv.setText(getIntent().getExtras().getString("Name"));
        profileMessageTv.setText(getIntent().getExtras().getString("Message"));
        assert uid != null;

        mDBListener2 = db_ref2.child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Map<String, String> upload = new HashMap<>();
                upload.put("Contact", dataSnapshot.child("Contact").getValue(String.class));
                upload.put("Address", dataSnapshot.child("Address").getValue(String.class));
                profileContactTv.setText(upload.get("Contact"));
                Address = upload.get("Address");
                Log.d(TAG, "Address : " + Address);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "onCancelled error : " + databaseError.getMessage());
                showMessage(databaseError.getMessage());
            }
        });
        getLocationBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetailsActivity.this, MapActivity.class);
                intent.putExtra("Address", Address);
                startActivity(intent);
            }
        });
    }
    private void initViews(){
        profileImageIv = findViewById(R.id.profile_image);
        profileNameTv = findViewById(R.id.profile_name);
        profileContactTv = findViewById(R.id.profile_contact);
        profileMessageTv = findViewById(R.id.profile_message);
        getLocationBt = findViewById(R.id.get_location);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        db_ref2.removeEventListener(mDBListener2);
    }

    private void showMessage(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}
