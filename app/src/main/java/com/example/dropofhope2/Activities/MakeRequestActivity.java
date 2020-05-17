package com.example.dropofhope2.Activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
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

public class MakeRequestActivity extends AppCompatActivity {
    private ImageView displayImageIv;
    private TextView chooseImageTv;
    private EditText messageEt;
    private Button submitRequestBt;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;
    private StorageReference mStorageRef;
    private StorageTask uploadTask;
    private DatabaseReference db_ref;
    private Uri imageUri;
    private static final String TAG = "myTag";
    private static final String MyPREFERENCES = "MyPrefs";
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_request);
        mAuth = FirebaseAuth.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference("Requests/Images");
        db_ref = FirebaseDatabase.getInstance().getReference("Requests");
        initViews();
        sharedPreferences = getApplicationContext().getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        progressBar.setVisibility(View.GONE);
        chooseImageTv.setOnClickListener(v -> chooseImage());
        submitRequestBt.setOnClickListener(v -> {
            String message = messageEt.getText().toString();
            if (uploadTask != null && uploadTask.isInProgress()) {
                showMessage("Upload in progress...");
            } else {
                FileUploader(message);
            }
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            MakeRequestActivity.this.finish();
        });
    }

    private void FileUploader(String message) {
        FirebaseUser user = mAuth.getCurrentUser();
        assert user != null;
        String imageId = (user.getUid() + "." + getExtension(imageUri));
        if (isValid(message, imageId)) {
            String uid = Objects.requireNonNull(user).getUid();
            String type = sharedPreferences.getString("Type", null);
            String name =sharedPreferences.getString("Name", null);
            StorageReference mRef = mStorageRef.child(imageId);
            uploadTask = mRef.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        progressBar.setProgress(0);
                        showMessage("Request sent successfully!");
                        mRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            String url = uri.toString();
                            Map<String, String> map = new HashMap<>();
                            map.put("Message", message);
                            map.put("Email", user.getEmail());
                            map.put("Image Id", imageId);
                            map.put("Type", type);
                            map.put("Name", name);
                            map.put("Image uri", url);
                            map.put("Id", uid);
                            db_ref.child(uid).setValue(map);
                        });
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        MakeRequestActivity.this.finish();
                    })
                    .addOnFailureListener(exception -> showMessage("Something went wrong :("))
                    .addOnProgressListener(taskSnapshot -> {
                        double progress = (taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount()) * 100;
                        progressBar.setProgress((int) progress);
                        Log.d(TAG, "Progressing...");
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


    private void showMessage(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            Picasso.get()
                    .load(imageUri)
                    .fit().centerCrop()
                    .into(displayImageIv);
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

    private void initViews() {
        displayImageIv = findViewById(R.id.display_image);
        chooseImageTv = findViewById(R.id.choose_image);
        messageEt = findViewById(R.id.message);
        submitRequestBt = findViewById(R.id.submit_request);
        progressBar = findViewById(R.id.progress_bar);
    }

}
