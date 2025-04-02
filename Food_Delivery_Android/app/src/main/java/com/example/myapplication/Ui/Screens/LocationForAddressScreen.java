package com.example.myapplication.Ui.Screens;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.Utility.Constant;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.example.myapplication.databinding.ActivityLocationForAddressScreenBinding;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LocationForAddressScreen extends FragmentActivity implements OnMapReadyCallback {
    private String latitude,longitude;
    private GoogleMap mMap;
    private LatLng latlng;
    private ActivityLocationForAddressScreenBinding binding;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private Button addLocationButton;
    private String address,subLocality,refreshToken;
    private TextView localityTextView, addressTextView;
    private Marker selectedMarker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLocationForAddressScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        SharedPreferences sharedPreferences = getSharedPreferences("Login", MODE_PRIVATE);
        refreshToken = sharedPreferences.getString("refreshToken", "0");

        checkPermissions();

        addLocationButton = findViewById(R.id.saveAddress_Button);
        addLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addLocation(address,latitude,longitude);
            }
        });
    }



    private void checkPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            getUserLocation();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 123);
        }
    }

    private void getUserLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        // Use getLastLocation or create a location request for more accurate updates
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    double lat = location.getLatitude();
                    double lang = location.getLongitude();
                    latitude = String.valueOf(lat);
                    longitude = String.valueOf(lang);
                    latlng = new LatLng(lat, lang);
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, 15));
                    mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                    mMap.getUiSettings().setMyLocationButtonEnabled(true);
                    if (ActivityCompat.checkSelfPermission(LocationForAddressScreen.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(LocationForAddressScreen.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    mMap.setMyLocationEnabled(true);
                    setupMarker();
                } else {
                    Toast.makeText(LocationForAddressScreen.this, "Unable to get current location.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setupMarker() {
        selectedMarker = mMap.addMarker(new MarkerOptions().title("Selected Location").position(mMap.getCameraPosition().target));
        mMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
            @Override
            public void onCameraMove() {
                LatLng currentPosition = mMap.getCameraPosition().target;
//                selectedMarker.setPosition(currentPosition);
                double lat = currentPosition.latitude;
                double lang = currentPosition.longitude;
                if (System.currentTimeMillis() % 100 < 50) {
                    selectedMarker.setPosition(currentPosition);
                }
                updateAddressInfo(lat, lang);
            }
        });
    }

    private void updateAddressInfo(double lat, double lang) {
        Geocoder geocoder = new Geocoder(this);
        try {
            List<Address> addressList = geocoder.getFromLocation(lat, lang, 1);
            if (addressList.size() > 0) {
                address = addressList.get(0).getAddressLine(0);
                String subLocality = addressList.get(0).getSubLocality();
                LinearLayout bottomSheetLayout = findViewById(R.id.bottom_sheet_layout);
                localityTextView = bottomSheetLayout.findViewById(R.id.localityAddressShowcase_BottomSheet);
                addressTextView = bottomSheetLayout.findViewById(R.id.AddressShowcase_BottomSheet);
                localityTextView.setText(subLocality);
                addressTextView.setText(address);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 123) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
                getUserLocation(); // Try to get user location again
            } else {
                Toast.makeText(this, "Permission Declined", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

    }

    private void addLocation(String address,String latitude,String longitude) {
        RequestQueue requestQueue = Volley.newRequestQueue(LocationForAddressScreen.this);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("address",address);
            jsonObject.put("lat",latitude);
            jsonObject.put("lang",longitude);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Constant.base_IP+"/location//getLocation", jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Toast.makeText(LocationForAddressScreen.this, "Submitted Successfully", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(LocationForAddressScreen.this, MainActivity.class);
                startActivity(intent);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(LocationForAddressScreen.this, "Error While Submitting Location", Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                super.getHeaders();
                Map<String,String> params = new HashMap<>();
                params.put("Authorization",refreshToken);
                return params;
            }
        };

        requestQueue.add(jsonObjectRequest);
    }
}
