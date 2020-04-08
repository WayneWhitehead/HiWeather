package com.hidesign.hiweather;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class SplashActivity extends AppCompatActivity {

    public static Location uLocation;
    public static String uAddress;
    private GoogleApiClient googleApiClient;
    private ArrayList<String> permissionsToRequest;
    private final ArrayList<String> permissionsRejected = new ArrayList<>();
    private final ArrayList<String> permissions = new ArrayList<>();
    private FusedLocationProviderClient mFusedLocationClient;
    public ImageView logo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_activity);

        if (googleApiClient != null) { googleApiClient.connect(); }

        logo = findViewById(R.id.splash_logo);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        permissionsToRequest = permissionsToRequest(permissions);
        if (permissionsToRequest.size() > 0) {
            requestPermissions(permissionsToRequest.toArray(new String[0]), 1011);
        }
        else {
            getLocation();
        }
        googleApiClient = new GoogleApiClient.Builder(this).addApi(LocationServices.API).build();
    }

    private void showMain() {
        Intent i = new Intent(SplashActivity.this, ScrollingActivity.class);
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, logo, "logoAnim");
        startActivity(i);
        overridePendingTransition(R.anim.slide_up, R.anim.slide_up_out);
        finish(); // kill current activity
    }

    public void getLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                &&  ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        mFusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
            if (location != null) {
                uLocation = location;
                showMain();
                uAddress = getAddress(uLocation.getLatitude(), uLocation.getLongitude());
                Log.e("Getting Location.....", uLocation.getLatitude()+ "," + uLocation.getLongitude() + " .... Address: " + uAddress);
            }
        });
        startLocationUpdates();


    }
    private ArrayList<String> permissionsToRequest(ArrayList<String> wantedPermissions) {
        ArrayList<String> result = new ArrayList<>();

        for (String perm : wantedPermissions) {
            if (hasPermission(perm)) {
                result.add(perm);
            }
        }
        return result;
    }

    private boolean hasPermission(String permission) {
        return checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED;
    }
    private void startLocationUpdates() {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(5000);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                &&  ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "You need to enable permissions to display location !", Toast.LENGTH_SHORT).show();
        }
        mFusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
            if (location != null) {
                uLocation = location;
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1011) {
            for (String perm : permissionsToRequest) {
                if (hasPermission(perm)) {
                    permissionsRejected.add(perm);
                }
            }
            if (permissionsRejected.size() > 0) {
                if (shouldShowRequestPermissionRationale(permissionsRejected.get(0))) {
                    new AlertDialog.Builder(this).setMessage("These permissions are mandatory to get your location.").setPositiveButton("OK", (dialogInterface, i) -> requestPermissions(permissionsRejected.toArray(new String[0]), 1011)).setNegativeButton("Cancel", null).create().show();
                    getLocation();
                    if (uLocation != null) {
                        showMain();
                    }
                }
            } else {
                if (googleApiClient != null) {
                    googleApiClient.connect();
                    getLocation();
                    showMain();
                }
                return;
            }
            getLocation();
        }
    }
    private String getAddress(double latitude, double longitude){
        Geocoder geocoder;
        List<Address> addresses = null;
        geocoder = new Geocoder(this, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("Tag", "Error getting Street Address: ");
        }

        assert addresses != null;
        return addresses.get(0).getLocality()+ "," + addresses.get(0).getCountryCode();
    }
}
