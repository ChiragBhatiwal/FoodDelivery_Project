package com.example.vendorapp.UI.Screens;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest.permission;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vendorapp.MainActivity;
import com.example.vendorapp.R;
import com.example.vendorapp.databinding.ActivityLocationScreenBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.List;

public class LocationScreen extends FragmentActivity implements OnMapReadyCallback {

    TextView locationTextView;
    Button saveAddress_Button;
    private static final long THROTTLE_DELAY = 1000; // 1 second delay
    private long lastGeocodeTime = 0;
    private GoogleMap mMap;
    private ActivityLocationScreenBinding binding;
    private final int Permission_Code = 1234567890;
    private LatLng currentLatLng;
    private Marker currentMarker;
    private FusedLocationProviderClient fusedLocationProviderClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityLocationScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        locationTextView = findViewById(R.id.location_text);
        saveAddress_Button = findViewById(R.id.saveAddress_Button);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        checkLocationPermissions();

         saveAddress_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                   Intent intent = new Intent(LocationScreen.this, MainActivity.class);
                   startActivity(intent);
            }
        });
    }

    private void checkLocationPermissions() {
        if (ActivityCompat.checkSelfPermission(this, permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            getCurrentLocation();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{permission.ACCESS_COARSE_LOCATION, permission.ACCESS_FINE_LOCATION}, Permission_Code);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == Permission_Code)
        {
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(this, "Permission Declined", Toast.LENGTH_SHORT).show();
            }
        }
    }

    void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                currentLatLng = new LatLng(latitude,longitude);
                setLocationOnMap(currentLatLng);
            }
        });
    }

    void setLocationOnMap(LatLng latLng)
    {
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.moveCamera(CameraUpdateFactory.zoomTo(12.5f));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(12.5f));
        currentMarker = mMap.addMarker(new MarkerOptions().position(latLng).title("Malik"));
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.isMyLocationEnabled();

        mMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
            @Override
            public void onCameraMove() {
                LatLng newLocation = mMap.getCameraPosition().target;

                long currentTime = System.currentTimeMillis();

                if (currentTime - lastGeocodeTime > THROTTLE_DELAY) {
                    lastGeocodeTime = currentTime;
                    getAddressFromLatLng(newLocation);
                }

                if(currentMarker != null)
                {
                    currentMarker.setPosition(newLocation);
                }
            }
        });
    }

    private void getAddressFromLatLng(LatLng latLng) {
        new Thread(() -> {
            Geocoder geocoder = new Geocoder(getApplicationContext());
            try {
                List<Address> addressList = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
                runOnUiThread(() -> {
                    if (addressList != null && !addressList.isEmpty()) {
                        String address = addressList.get(0).getAddressLine(0);
                        locationTextView.setText(address);
                    } else {
                        locationTextView.setText("Address not found");
                    }
                });
            } catch (IOException e) {
                runOnUiThread(() -> {
                    locationTextView.setText("Geocoder service unavailable");
                });
            }
        }).start();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

//        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }
}