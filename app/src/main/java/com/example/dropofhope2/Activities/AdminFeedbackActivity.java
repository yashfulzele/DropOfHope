package com.example.dropofhope2.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.example.dropofhope2.Adapter.adminFeedbackAdapter;
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

public class AdminFeedbackActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private EditText searchUsersEt;
    private ProgressBar progressBar;

    private adminFeedbackAdapter mAdapter;
    private List<Map<String, String>> mFeedback;

    private DatabaseReference db_ref;
    private ValueEventListener mDBListener;

    private static final String TAG = "myTag";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_feedback);
        initViews();
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mFeedback = new ArrayList<>();
        mAdapter = new adminFeedbackAdapter(AdminFeedbackActivity.this, mFeedback);
        recyclerView.setAdapter(mAdapter);
        db_ref = FirebaseDatabase.getInstance().getReference("Feedback");
        mDBListener = db_ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mFeedback.clear();
                Map<String, String> feedback = new HashMap<>();
                for (DataSnapshot post : dataSnapshot.getChildren()) {
                    feedback.put("Name", post.child("Name").getValue(String.class));
                    feedback.put("Feedback", post.child("Feedback").getValue(String.class));
                    feedback.put("Email", post.child("Email").getValue(String.class));
                    mFeedback.add(feedback);
                }
                mAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "Show Person : onCancelled error : " + databaseError.getMessage());
                progressBar.setVisibility(View.INVISIBLE);
            }
        });
        init();
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recycler_view);
        searchUsersEt = findViewById(R.id.search_users);
        progressBar = findViewById(R.id.progress_circular_users);
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
        for (Map<String, String> map : mFeedback) {
            if (Objects.requireNonNull(map.get("Name")).toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(map);
            }
        }
        mAdapter.filterList(filteredList);
    }

    private void hideSoftKeyboard() {
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        db_ref.removeEventListener(mDBListener);
    }
}
