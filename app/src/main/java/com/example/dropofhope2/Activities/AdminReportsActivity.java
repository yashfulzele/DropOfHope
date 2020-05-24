package com.example.dropofhope2.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.example.dropofhope2.Adapter.adminReportsAdapter;
import com.example.dropofhope2.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class AdminReportsActivity extends AppCompatActivity implements adminReportsAdapter.OnItemClickListener {
    private RecyclerView recyclerView;
    private EditText searchUsersEt;
    private ProgressBar progressBar;
    private ImageView searchIconIv;

    private adminReportsAdapter mAdapter;
    private List<Map<String, String>> mReports;

    private DatabaseReference db_ref;
    private ValueEventListener mDBListener;

    private static final String TAG = "myTag";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_reports);
        initViews();
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mReports = new ArrayList<>();
        mAdapter = new adminReportsAdapter(AdminReportsActivity.this, mReports);
        recyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(AdminReportsActivity.this);
        Log.d(TAG, "You are in AdminReportsActivity !");
        db_ref = FirebaseDatabase.getInstance().getReference("Report");
        mDBListener = db_ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mReports.clear();
                Map<String, String> upload = new HashMap<>();
                for (DataSnapshot post : dataSnapshot.getChildren()) {
                    upload.put("Message", post.child("Message").getValue(String.class));
                    upload.put("Name", post.child("Name").getValue(String.class));
                    upload.put("Image uri", post.child("Image uri").getValue(String.class));
                    upload.put("Email", post.child("Email").getValue(String.class));
                    upload.put("Id", post.child("Id").getValue(String.class));
                    mReports.add(upload);
                }
                mAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "onCancelled error : " + databaseError.getMessage());
                progressBar.setVisibility(View.INVISIBLE);
            }
        });
        init();
    }

    private void init() {
        searchUsersEt.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE || event.getAction() == KeyEvent.ACTION_DOWN || event.getAction() == KeyEvent.KEYCODE_ENTER) {
                searchUsersEt.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        if (s.toString().length() > 0) {
                            searchIconIv.setVisibility(View.GONE);
                            filter(s.toString());
                        }
                    }
                });
            }
            return false;
        });
        hideSoftKeyboard();
    }

    private void filter(String text) {
        List<Map<String, String>> filteredList = new ArrayList<>();
        for (Map<String, String> map : mReports) {
            if (Objects.requireNonNull(map.get("Name")).toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(map);
            }
        }
        mAdapter.filterList(filteredList);
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recycler_view);
        searchUsersEt = findViewById(R.id.search_users);
        searchIconIv = findViewById(R.id.search_icon);
        progressBar = findViewById(R.id.progress_circular_users);
    }

    @Override
    public void OnItemClick(int position) {
        Map<String, String> selectedItem = mReports.get(position);
        String selectedKey = selectedItem.get("Id");
        Intent intent = new Intent(AdminReportsActivity.this, ReportsDetailActivity.class);
        intent.putExtra("Id", selectedKey);
        intent.putExtra("Image uri", selectedItem.get("Image uri"));
        intent.putExtra("Name", selectedItem.get("Name"));
        intent.putExtra("Email", selectedItem.get("Email"));
        intent.putExtra("Type", selectedItem.get("Type"));
        intent.putExtra("Message", selectedItem.get("Message"));
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        db_ref.removeEventListener(mDBListener);
    }

    private void hideSoftKeyboard() {
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }
}
