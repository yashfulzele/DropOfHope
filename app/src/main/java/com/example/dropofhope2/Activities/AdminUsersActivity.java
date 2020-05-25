package com.example.dropofhope2.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.ProgressBar;

import com.example.dropofhope2.Adapter.adminUsersAdapter;
import com.example.dropofhope2.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class AdminUsersActivity extends AppCompatActivity implements adminUsersAdapter.OnItemClickListener, PopupMenu.OnMenuItemClickListener {
    private RecyclerView recyclerView;
    private EditText searchUsersEt;
    private ProgressBar progressBar;

    private adminUsersAdapter mAdapter;
    private List<Map<String, String>> mUsers;

    private DatabaseReference db_ref;
    private ValueEventListener mDBListener;

    private static final String TAG = "myTag";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_users);
        initViews();
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mUsers = new ArrayList<>();
        mAdapter = new adminUsersAdapter(AdminUsersActivity.this, mUsers);
        recyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(AdminUsersActivity.this);
        db_ref = FirebaseDatabase.getInstance().getReference("Users");
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
        for (Map<String, String> map : mUsers) {
            if (Objects.requireNonNull(map.get("Name")).toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(map);
            }
        }
        mAdapter.filterList(filteredList);
    }

    public void showType(View v) {
        popUp(v, true, R.style.MyUsersPopupStyle);
    }

    private void popUp(View v, boolean isWithIcons, int style) {
        Context wrapper = new ContextThemeWrapper(this, style);
        PopupMenu popup = new PopupMenu(wrapper, v);
        if (isWithIcons) {
            try {
                Field[] fields = popup.getClass().getDeclaredFields();
                for (Field field : fields) {
                    if ("mPopup".equals(field.getName())) {
                        field.setAccessible(true);
                        Object menuPopupHelper = field.get(popup);
                        assert menuPopupHelper != null;
                        Class<?> classPopupHelper = Class.forName(menuPopupHelper.getClass().getName());
                        Method setForceIcons = classPopupHelper.getMethod("setForceShowIcon", boolean.class);
                        setForceIcons.invoke(menuPopupHelper, true);
                        break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        popup.setOnMenuItemClickListener(this);
        popup.inflate(R.menu.person_or_organization);
        popup.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_1:
                showPerson();
                return true;
            case R.id.item_2:
                showOrganization();
                return true;
            default:
                return false;
        }
    }

    private void showPerson() {
        mDBListener = db_ref.child("Person").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUsers.clear();
                Map<String, String> user = new HashMap<>();
                for (DataSnapshot post : dataSnapshot.getChildren()) {
                    user.put("Name", post.child("Name").getValue(String.class));
                    user.put("Age", post.child("Age").getValue(String.class));
                    user.put("Sex", post.child("Sex").getValue(String.class));
                    user.put("Email", post.child("Email").getValue(String.class));
                    user.put("Address", post.child("Address").getValue(String.class));
                    user.put("Contact", post.child("Contact").getValue(String.class));
                    user.put("Blood group", post.child("Blood group").getValue(String.class));
                    user.put("isDonor", post.child("isDonor").getValue(String.class));
                    user.put("Id", post.child("Id").getValue(String.class));
                    user.put("Type", "Person");
                    mUsers.add(user);
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
    }

    private void showOrganization() {
        mDBListener = db_ref.child("Organization").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUsers.clear();
                Map<String, String> user = new HashMap<>();
                for (DataSnapshot post : dataSnapshot.getChildren()) {
                    user.put("Name", post.child("Name").getValue(String.class));
                    user.put("Members", post.child("Members").getValue(String.class));
                    user.put("Email", post.child("Email").getValue(String.class));
                    user.put("Organization type", post.child("Organization type").getValue(String.class));
                    user.put("Address", post.child("Address").getValue(String.class));
                    user.put("Contact", post.child("Contact").getValue(String.class));
                    user.put("Id", post.child("Id").getValue(String.class));
                    user.put("Type", "Organization");
                    mUsers.add(user);
                }
                mAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "Show Organization : onCancelled error : " + databaseError.getMessage());
                progressBar.setVisibility(View.INVISIBLE);
            }
        });
    }

    @Override
    public void OnItemClick(int position) {
        Map<String, String> selectedItem = mUsers.get(position);
        String selectedType = selectedItem.get("Type");
        assert selectedType != null;
        if (selectedType.equals("Person")) {
            Intent intent = new Intent(AdminUsersActivity.this, PersonDetailsActivity.class);
            intent.putExtra("Name", selectedItem.get("Name"));
            intent.putExtra("Age", selectedItem.get("Age"));
            intent.putExtra("Sex", selectedItem.get("Sex"));
            intent.putExtra("Email", selectedItem.get("Email"));
            intent.putExtra("Address", selectedItem.get("Address"));
            intent.putExtra("Contact", selectedItem.get("Contact"));
            intent.putExtra("Blood group", selectedItem.get("Blood group"));
            intent.putExtra("isDonor", selectedItem.get("isDonor"));
            startActivity(intent);
        } else {
            Intent intent = new Intent(AdminUsersActivity.this, OrganizationDetailsActivity.class);
            intent.putExtra("Name", selectedItem.get("Name"));
            intent.putExtra("Contact", selectedItem.get("Contact"));
            intent.putExtra("Members", selectedItem.get("Members"));
            intent.putExtra("Email", selectedItem.get("Email"));
            intent.putExtra("Address", selectedItem.get("Address"));
            intent.putExtra("Organization type", selectedItem.get("Organization type"));
            startActivity(intent);
        }
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
