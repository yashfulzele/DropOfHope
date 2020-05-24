package com.example.dropofhope2.Activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.dropofhope2.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ReportActivity extends AppCompatActivity {
    private ImageView img;
    private Button chooseBt, reportBt;
    private EditText messageEt;
    public Uri imageUri;
    private StorageReference mStorageRef;
    private StorageTask uploadTask;
    private DatabaseReference db_ref;
    private FirebaseAuth mAuth;
    private SharedPreferences sharedPreferences;
    private static final String MyPREFERENCES = "MyPrefs";
    private static final String TAG = "MyTag";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        mAuth = FirebaseAuth.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference("Report/Images");
        db_ref = FirebaseDatabase.getInstance().getReference("Report");
        sharedPreferences = getApplicationContext().getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        initViews();
        Log.d(TAG, "You are in ReportActivity !");
        if (mAuth.getCurrentUser() != null) {
            chooseBt.setOnClickListener(v -> chooseImage());
            reportBt.setOnClickListener(v -> {
                String message = messageEt.getText().toString();
                if (uploadTask != null && uploadTask.isInProgress()) {
                    showMessage("Upload in progress...");
                } else {
                    FileUploader(message);
                }
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                ReportActivity.this.finish();
            });
        }
    }

    private boolean isValid(String msg, String imageId) {
        if (msg.isEmpty()) {
            messageEt.setError("No issues written");
            return false;
        } else if (imageId.isEmpty()) {
            showMessage("Image is required!");
            return false;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            Picasso.get()
                    .load(imageUri)
                    .fit().centerCrop()
                    .into(img);
        }
    }

    private String getExtension(Uri uri) {
        ContentResolver cr = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(cr.getType(uri));
    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 1);
    }

    private void FileUploader(String message) {
        FirebaseUser user = mAuth.getCurrentUser();
        assert user != null;
        String imageId = (user.getUid() + "." + getExtension(imageUri));
        if (isValid(message, imageId)) {
            String name = sharedPreferences.getString("Name", null);
            String uid = Objects.requireNonNull(user).getUid();
            StorageReference mRef = mStorageRef.child(imageId);
            uploadTask = mRef.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        showMessage("Image uploaded successfully!");
                        mRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            String url = uri.toString();
                            Map<String, String> map = new HashMap<>();
                            map.put("Message", message);
                            map.put("Name", name);
                            map.put("Email", user.getEmail());
                            map.put("Image uri", url);
                            map.put("Id", uid);
                            db_ref.child(uid).setValue(map);
                        });
                    })
                    .addOnFailureListener(exception -> showMessage("Something went wrong :("));
        }
    }

    private void showMessage(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    private void initViews() {
        chooseBt = findViewById(R.id.choose_image);
        reportBt = findViewById(R.id.report);
        messageEt = findViewById(R.id.message);
        img = findViewById(R.id.display_image);
    }


}
