package com.example.storeinfo;

import android.graphics.Color;
import android.util.Log;
import android.widget.Toast;
import androidx.fragment.app.FragmentActivity;
import android.os.Bundle;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;
import org.json.JSONArray;
import org.json.JSONException;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        queue = Volley.newRequestQueue(this);
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
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng gangnam = new LatLng(37.498187, 127.027817);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(gangnam, 14.0f));

        mMap.setOnCameraMoveStartedListener(new GoogleMap.OnCameraMoveStartedListener() {
            @Override
            public void onCameraMoveStarted(int reason) {
                if (reason == GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE) {
                    Toast.makeText(MapsActivity.this, "The user gestured on the map.",
                            Toast.LENGTH_SHORT).show();
                }

            }
        });

        mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                Toast.makeText(MapsActivity.this, "The camera has stopped moving.",
                        Toast.LENGTH_SHORT).show();
                requestStoreInformation(mMap.getCameraPosition().target);
            }
        });
    }

    private void requestStoreInformation(LatLng latLng) {
        String url = null;

        StringBuffer sb = new StringBuffer();
        sb.append("http://192.168.1.187:8080/request_store_data?");
        sb.append("code=");
        sb.append("Q");
        sb.append("&lat=");
        sb.append(latLng.latitude);
        sb.append("&lon=");
        sb.append(latLng.longitude);

        Log.d("Result", "url:" + sb.toString());
        url = sb.toString();

        // Request a string response from the provided URL.
        JsonArrayRequest jsonObjectRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                getResponseListener(), new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Result", "on Error:" + error.toString());
            }
        });

        // Add the request to the RequestQueue.
        queue.add(jsonObjectRequest);

    }

    private Response.Listener<JSONArray> getResponseListener() {
        return new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Log.d("Result", response.toString());

                Gson gson = new Gson();

                StoreInfo locationData = null;

                for (int i = 0; i < response.length(); i++) {
                    try {
                        locationData = gson.fromJson(response.get(i).toString(), StoreInfo.class);

                        double latitude = locationData.latitude;
                        double longitude = locationData.longitude;
                        LatLng latLng = new LatLng(latitude, longitude);

                        MarkerOptions markerOptions = new MarkerOptions();
                        markerOptions.title(locationData.name);
                        markerOptions.position(latLng);
                        mMap.addMarker(markerOptions);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
    }

    static class StoreInfo {
        String name;
        String className;
        String businessName;
        String industrialName;
        Double latitude;
        Double longitude;
    }
}
