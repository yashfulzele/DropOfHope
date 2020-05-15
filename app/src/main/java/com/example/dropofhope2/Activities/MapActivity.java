package com.example.dropofhope2.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.widget.Toast;

import com.example.dropofhope2.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {
    private boolean mLocationPermissionGranted;
    private static final String TAG = "myTag";
    private static final int PERMISSION_REQUEST_CODE = 9001;
    private static final int PLAY_SERVICES_ERROR_CODE = 9002;

    private MapView mapView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        initGoogleMap();
        mapView = findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG, "Map is showing");
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

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState, @NonNull PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        mapView.onSaveInstanceState(outState);
    }
}