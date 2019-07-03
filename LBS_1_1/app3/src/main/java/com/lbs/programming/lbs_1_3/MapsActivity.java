package com.lbs.programming.lbs_1_3;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Camera;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

// TODO: MapsReadyCallback 구현.
public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 0;

    // TODO: googlemaps 객체 생성.
    // private GoogleMap googleMap;
    private LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            onLocationChanged(locationResult.getLastLocation());
        }
    };
    private Location lastLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        // TODO: mapsAsync 실행.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_LOCATION);
        }
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    // TODO: onMapsReady 구현.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MY_PERMISSIONS_REQUEST_LOCATION && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            startLocationService();
        }
    }


    private void startLocationService() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationRequest locationRequest = getLocationRequest();

        // Create LocationSettingsRequest object using location request
        LocationSettingsRequest locationSettingsRequest = getLocationSettingsRequest(locationRequest);

        // Check whether location settings are satisfied
        // https://developers.google.com/android/reference/com/google/android/gms/location/SettingsClient
        SettingsClient settingsClient = LocationServices.getSettingsClient(this);
        settingsClient.checkLocationSettings(locationSettingsRequest);

        getLastLocation();

        // new Google API SDK v11 uses getFusedLocationProviderClient(this)
        LocationServices.getFusedLocationProviderClient(this).requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
    }

    private void getLastLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        Task<Location> result = LocationServices.getFusedLocationProviderClient(this).getLastLocation();
        result.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                onLocationChanged(location);
            }
        });
    }

    @NonNull
    private LocationSettingsRequest getLocationSettingsRequest(LocationRequest locationRequest) {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(locationRequest);
        return builder.build();
    }

    @NonNull
    private LocationRequest getLocationRequest() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(1000);
        return locationRequest;
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        startLocationService();
    }

    @Override
    protected void onPause() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        LocationServices.getFusedLocationProviderClient(this).removeLocationUpdates(locationCallback);
        super.onPause();
    }

    public void onLocationChanged(Location location) {
        if (location != null) {
            lastLocation = location;
        }
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    Activity#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for Activity#requestPermissions for more details.
            return;
        }

        setMyLocationButton(googleMap);

        setPOIMarker(googleMap);

        setMapStyle(googleMap);

        setPolygon(googleMap);
    }

    private void setPolygon(GoogleMap googleMap) {
        Polygon polygon1 = googleMap.addPolygon(new PolygonOptions()
                .clickable(true)
                .add(
                        new LatLng(37.534491, 126.975930),
                        new LatLng(37.529340, 126.974874),
                        new LatLng(37.529349, 126.969751),
                        new LatLng(37.526405, 126.969468),
                        new LatLng(37.522454, 126.974521),
                        new LatLng(37.520226, 126.989609),
                        new LatLng(37.529851, 126.991286),
                        new LatLng(37.535112, 126.986233),
                        new LatLng(37.534569, 126.975796)));
        // Store a data object with the polygon, used here to indicate an arbitrary type.
        polygon1.setTag("용산공원");
        polygon1.setFillColor(0x7fFF0000);
        googleMap.setOnPolygonClickListener(new GoogleMap.OnPolygonClickListener() {
            @Override
            public void onPolygonClick(Polygon polygon) {
                Toast.makeText(getBaseContext(), polygon.getTag().toString(), Toast.LENGTH_LONG).show();
            }
        });

    }

    private void setMapStyle(GoogleMap googleMap) {
        googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        googleMap.setIndoorEnabled(true);
        googleMap.getUiSettings().setMapToolbarEnabled(true);
        googleMap.getUiSettings().setAllGesturesEnabled(true);
        googleMap.getUiSettings().setCompassEnabled(true);
        googleMap.getUiSettings().setIndoorLevelPickerEnabled(true);
        googleMap.getUiSettings().setZoomControlsEnabled(true);
    }

    private void setPOIMarker(GoogleMap googleMap) {
        LatLng seoul = new LatLng(37.553192, 126.972579);
        googleMap.addMarker(new MarkerOptions().position(seoul).title("Marker in Seoul"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(seoul));
    }

    @SuppressLint("MissingPermission")
    private void setMyLocationButton(final GoogleMap googleMap) {
        googleMap.setMyLocationEnabled(true);
        googleMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                if (lastLocation != null) {
                    googleMap.moveCamera(
                            CameraUpdateFactory.newLatLng(
                                    new LatLng(lastLocation.getLatitude(),
                                            lastLocation.getLongitude())));
                }
                return false;
            }
        });
    }
}
