package com.example.dropofhope2.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.PermissionChecker;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.HandlerThread;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.dropofhope2.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback, PopupMenu.OnMenuItemClickListener {
    private boolean mLocationPermissionGranted;
    private static final String TAG = "myTag";
    private static final int PERMISSION_REQUEST_CODE = 9001;
    private static final int PLAY_SERVICES_ERROR_CODE = 9002;
    private static final int GPS_REQUEST_CODE = 9003;
    private static final float DEFAULT_ZOOM = 17f;

    private GoogleMap mGoogleMap;
    private SupportMapFragment supportMapFragment;
    private FusedLocationProviderClient mLocationClient;
    private LocationCallback mLocationCallback;
    private HandlerThread mHandlerThread;

    private EditText searchEt;
    private ImageView getLocationIv;
    private String userAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        initGoogleMap();
        searchEt = findViewById(R.id.search_location);
        getLocationIv = findViewById(R.id.get_location);
        userAddress = Objects.requireNonNull(getIntent().getExtras()).getString("Address");

        mLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                Location location = locationResult.getLastLocation();
                runOnUiThread(() -> {
                    gotoLocation(location.getLatitude(), location.getLongitude());
                    Log.d(TAG, "runOnUiThread : Thread name : " + Thread.currentThread().getName());
                });
                //showMarker(location.getLatitude(), location.getLongitude());
                Log.d(TAG, location.getLatitude() + ", " + location.getLongitude());
                Log.d(TAG, "Thread name : " + Thread.currentThread().getName());
            }
        };
        getLocationIv.setOnClickListener(v -> getLocationUpdates());
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG, "Map is showing");
        mGoogleMap = googleMap;
        //mGoogleMap.setMyLocationEnabled(true);
        mGoogleMap.getUiSettings().setMyLocationButtonEnabled(true);
        mGoogleMap.getUiSettings().setMapToolbarEnabled(true);
        getUserLocation(userAddress);
        try {
            final ViewGroup parent = (ViewGroup) Objects.requireNonNull(supportMapFragment.getView()).findViewWithTag("GoogleMapMyLocationButton").getParent();
            parent.post(() -> {
                try {
                    Resources r = getResources();
                    int marginPixels = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, r.getDisplayMetrics());
                    View mapCompass = parent.getChildAt(4);
                    RelativeLayout.LayoutParams rlp = new RelativeLayout.LayoutParams(mapCompass.getHeight(), mapCompass.getHeight());
                    rlp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                    rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
                    rlp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0);
                    rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                    rlp.setMargins(marginPixels, marginPixels, marginPixels, marginPixels);
                    mapCompass.setLayoutParams(rlp);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        init();
    }

    private void init() {
        searchEt.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE || event.getAction() == KeyEvent.ACTION_DOWN || event.getAction() == KeyEvent.KEYCODE_ENTER) {
                geoLocate();
            }
            return false;
        });
        hideSoftKeyboard();
    }

    private void geoLocate() {
        String searchString = searchEt.getText().toString();
        Geocoder geocoder = new Geocoder(MapActivity.this);
        List<Address> list = new ArrayList<>();
        try {
            list = geocoder.getFromLocationName(searchString, 1);
        } catch (IOException e) {
            Log.d(TAG, "geoLocate, error : " + e.getMessage());
        }
        if (list.size() > 0) {
            Address address = list.get(0);
            mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(address.getLatitude(), address.getLongitude()), DEFAULT_ZOOM));
            MarkerOptions markerOptions = new MarkerOptions()
                    .title(address.getAddressLine(0))
                    .position(new LatLng(address.getLatitude(), address.getLongitude()));
            mGoogleMap.addMarker(markerOptions);
        }
    }

    private void getUserLocation(String userAddress) {
        Geocoder geocoder = new Geocoder(MapActivity.this);
        List<Address> list = new ArrayList<>();
        try {
            list = geocoder.getFromLocationName(userAddress, 1);
        } catch (IOException e) {
            Log.d(TAG, "getUserLocation, error : " + e.getMessage());
        }
        if (list.size() > 0) {
            Address address = list.get(0);
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(address.getLatitude(), address.getLongitude()), DEFAULT_ZOOM));
            MarkerOptions markerOptions = new MarkerOptions()
                    .title(userAddress)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                    .position(new LatLng(address.getLatitude(), address.getLongitude()));
            mGoogleMap.addMarker(markerOptions);
        }
    }

    private void initGoogleMap() {
        if (isServicesOK()) {
            if (isGPSEnabled()) {
                if (checkLocationPermission()) {
                    Log.d(TAG, "Ready to map!");
                    supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
                    assert supportMapFragment != null;
                    supportMapFragment.getMapAsync(MapActivity.this);
                } else {
                    requestLocationPermission();
                }
            }
        }
    }

    private boolean isGPSEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        assert locationManager != null;
        boolean providerEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (providerEnabled) {
            return true;
        } else {
            AlertDialog alertDialog = new AlertDialog.Builder(this)
                    .setTitle("GPS Permission")
                    .setMessage("GPS is required for this app to work. Please enable GPS!")
                    .setPositiveButton("Yes", ((dialogInterface, i) -> {
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(intent, GPS_REQUEST_CODE);
                    }))
                    .setCancelable(false)
                    .show();
        }
        return false;
    }

    private boolean checkLocationPermission() {
        return (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED);
    }

    private void requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE);
        }
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
        mLocationPermissionGranted = false;
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
                mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                try {
                    boolean success = mGoogleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.dark_map));
                    if (!success) {
                        Log.e(TAG, "Style parsing failed.");
                    }
                } catch (Resources.NotFoundException e) {
                    Log.e(TAG, "Can't find style. Error: ", e);
                }
                return true;
            case R.id.item_3:
                mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                try {
                    boolean success = mGoogleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.night_map));
                    if (!success) {
                        Log.e(TAG, "Style parsing failed.");
                    }
                } catch (Resources.NotFoundException e) {
                    Log.e(TAG, "Can't find style. Error: ", e);
                }
                return true;
            case R.id.item_4:
                mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                try {
                    boolean success = mGoogleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.aubergine_map));
                    if (!success) {
                        Log.e(TAG, "Style parsing failed.");
                    }
                } catch (Resources.NotFoundException e) {
                    Log.e(TAG, "Can't find style. Error: ", e);
                }
                return true;
            case R.id.item_5:
                mGoogleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                return true;
            default:
                return false;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GPS_REQUEST_CODE) {
            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            assert locationManager != null;
            boolean providerEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            if (providerEnabled) {
                Log.d(TAG, "GPS is enabled!");
            } else {
                Log.d(TAG, "GPS is not enabled!");
            }
        }
    }

    private void getCurrentLocation() {
        mLocationClient.getLastLocation().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Location location = task.getResult();
                gotoLocation(location.getLatitude(), location.getLongitude());
            } else {
                Log.d(TAG, "getCurrentLocation: Error: " + Objects.requireNonNull(task.getException()).getMessage());
            }
        });
    }

    private void gotoLocation(double lat, double lng) {
        LatLng latLng = new LatLng(lat, lng);
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM);
        mGoogleMap.animateCamera(cameraUpdate);
    }

    private void getLocationUpdates() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        locationRequest.setInterval(10 * 1000);
        locationRequest.setFastestInterval(5000);
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            return ;
        }
        mHandlerThread = new HandlerThread("LocationCallbackThread");
        mHandlerThread.start();
        mLocationClient.requestLocationUpdates(locationRequest, mLocationCallback, mHandlerThread.getLooper());
    }

    private void showMarker(double lat, double lng) {
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(new LatLng(lat, lng));
        mGoogleMap.addMarker(markerOptions);
    }

    private void hideSoftKeyboard() {
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mLocationCallback != null) {
            mLocationClient.removeLocationUpdates(mLocationCallback);
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mHandlerThread != null) {
            mHandlerThread.quit();
        }
    }
}