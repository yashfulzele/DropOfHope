package com.example.dropofhope2.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.dropofhope2.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Objects;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback, PopupMenu.OnMenuItemClickListener {
    private boolean mLocationPermissionGranted;
    private static final String TAG = "myTag";
    private static final int PERMISSION_REQUEST_CODE = 9001;
    private static final int PLAY_SERVICES_ERROR_CODE = 9002;

    private GoogleMap mGoogleMap;
    private SupportMapFragment supportMapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        initGoogleMap();
        supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        assert supportMapFragment != null;
        supportMapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG, "Map is showing");
        mGoogleMap = googleMap;
        mGoogleMap.getUiSettings().setMyLocationButtonEnabled(true);
        mGoogleMap.getUiSettings().setMapToolbarEnabled(false);
        try {
            final ViewGroup parent = (ViewGroup) Objects.requireNonNull(supportMapFragment.getView()).findViewWithTag("GoogleMapMyLocationButton").getParent();
            parent.post(new Runnable() {
                @Override
                public void run() {
                    try {
                        Resources r = getResources();
                        //convert our dp margin into pixels
                        int marginPixels = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, r.getDisplayMetrics());
                        // Get the map compass view
                        View mapCompass = parent.getChildAt(4);

                        // create layoutParams, giving it our wanted width and height(important, by default the width is "match parent")
                        RelativeLayout.LayoutParams rlp = new RelativeLayout.LayoutParams(mapCompass.getHeight(),mapCompass.getHeight());
                        // position on top right
                        rlp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                        rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
                        rlp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0);
                        rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                        //give compass margin
                        rlp.setMargins(marginPixels, marginPixels, marginPixels, marginPixels);
                        mapCompass.setLayoutParams(rlp);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        try {
            boolean success = googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style));
            if (!success) {
                Log.e(TAG, "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "Can't find style. Error: ", e);
        }
        /*MarkerOptions markerOptions = new MarkerOptions()
                .title("Center of the world!")
                .position(new LatLng(0,0));
        mGoogleMap.addMarker(markerOptions);*/
    }

    private void initGoogleMap() {
        if (isServicesOK()) {
            if (checkLocationPermission()) {
                Log.d(TAG, "Ready to map!");
            } else {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE);
                }
            }
        }
    }

    private boolean checkLocationPermission() {
        return (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED);
    }

    private boolean isServicesOK() {
        GoogleApiAvailability googleApi = GoogleApiAvailability.getInstance();
        int result = googleApi.isGooglePlayServicesAvailable(MapActivity.this);
        if (result == ConnectionResult.SUCCESS) {
            return true;
        } else if (googleApi.isUserResolvableError(result)) {
            Dialog dialog = googleApi.getErrorDialog(MapActivity.this, result, PLAY_SERVICES_ERROR_CODE, task -> {
                Toast.makeText(this, "Dialog box cancelled by user", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Dialog box cancelled by user");
            });
            dialog.show();
        } else {
            Toast.makeText(this, "Play services are required by the application.", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "Play services are required by the application.");
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
            Log.d(TAG, "Permission granted!");
        } else {
            Log.d(TAG, "Permission not granted!");
        }
    }

    public void showMapType(View v) {
        popUp(v, true, R.style.MyPopupStyle);
    }

    public void popUp(View v, boolean isWithIcons, int style) {
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
        popup.inflate(R.menu.popup_menu);
        popup.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_1:
                mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                return true;
            case R.id.item_2:
                mGoogleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                return true;
            default:
                return false;
        }
    }
}