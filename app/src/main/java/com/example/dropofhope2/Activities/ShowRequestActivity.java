package com.example.dropofhope2.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.dropofhope2.Adapter.showRequestAdapter;
import com.example.dropofhope2.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ShowRequestActivity extends AppCompatActivity implements showRequestAdapter.OnItemClickListener {
    private RecyclerView recyclerView;
    private showRequestAdapter mAdapter;
    private ProgressBar progressBar;
    private FirebaseStorage mStorage;
    private DatabaseReference db_ref;
    private ValueEventListener mDBListener;
    private List<Map<String, String>> mUploads;
    private static final String TAG = "myTag";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_request);
        initViews();
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mUploads = new ArrayList<>();
        mAdapter = new showRequestAdapter(ShowRequestActivity.this, mUploads);
        recyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(ShowRequestActivity.this);
        mStorage = FirebaseStorage.getInstance();
        Log.d(TAG, "You are in ShowRequestActivity");
        db_ref = FirebaseDatabase.getInstance().getReference("Requests");
        mDBListener = db_ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUploads.clear();
                Map<String, String> upload = new HashMap<>();
                for (DataSnapshot post : dataSnapshot.getChildren()) {
                    upload.put("Message", post.child("Message").getValue(String.class));
                    upload.put("Name", post.child("Name").getValue(String.class));
                    upload.put("Image uri", post.child("Image uri").getValue(String.class));
                    upload.put("Image Id", post.child("Image Id").getValue(String.class));
                    upload.put("Email", post.child("Email").getValue(String.class));
                    upload.put("Type", post.child("Type").getValue(String.class));
                    upload.put("Id", post.child("Id").getValue(String.class));
                    mUploads.add(upload);
                }
                mAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "onCancelled error : " + databaseError.getMessage());
                showMessage(databaseError.getMessage());
                progressBar.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recycler_view);
        progressBar = findViewById(R.id.progress_circular);
    }

    private void showMessage(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void OnItemClick(int position) {
        Map<String, String> selectedItem = mUploads.get(position);
        String selectedKey = selectedItem.get("Id");
        Intent intent = new Intent(ShowRequestActivity.this, DetailsActivity.class);
        intent.putExtra("Id", selectedKey);
        intent.putExtra("Image uri", selectedItem.get("Image uri"));
        intent.putExtra("Name", selectedItem.get("Name"));
        intent.putExtra("Type", selectedItem.get("Type"));
        intent.putExtra("Message", selectedItem.get("Message"));
        startActivity(intent);
    }

    @Override
    public void OnDeleteClick(int position) {
        Log.d(TAG, "OnDeleteClick came");
        Map<String, String> selectedItem = mUploads.get(position);
        String selectedKey = selectedItem.get("Id");
        String uri_string = selectedItem.get("Image uri");
        if (Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid().equals(selectedKey)) {
            assert uri_string != null;
            StorageReference img_ref = mStorage.getReferenceFromUrl(uri_string);
            img_ref.delete()
                    .addOnSuccessListener(aVoid -> {
                        db_ref.child(selectedKey).removeValue();
                        showMessage("Item deleted successfully!");
                    });
        } else {
            showMessage("You are not authorized to delete this post!");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        db_ref.removeEventListener(mDBListener);
    }
}
