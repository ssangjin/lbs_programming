package com.example.myapplication;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import androidx.fragment.app.FragmentActivity;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;
import org.json.JSONArray;
import org.json.JSONException;

public class HistoryActivity extends FragmentActivity implements OnMapReadyCallback {
    private String deviceId = "";
    public static final String LATITUDE = "latitude";
    public static final String LONGITUDE = "longitude";

    private GoogleMap mMap;
    RequestQueue queue;
    private Polyline line;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        queue = Volley.newRequestQueue(this);
    }

    private void requestHistory() {
        String url = null;

        StringBuffer sb = new StringBuffer();
        sb.append("http://192.168.1.187:8080/request_data?");
        sb.append("userId=");
        sb.append(MapsActivity.getDeviceId(this));

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

                // maps 경로를 만든다.
                PolylineOptions polylineOptions = new PolylineOptions();
                polylineOptions.width(5).color(Color.RED);

                LocationData locationData = null;

                for (int i = 0; i < response.length(); i++) {
                    try {
                        locationData = gson.fromJson(response.get(i).toString(), LocationData.class);
                        double latitude = locationData.getLatitude();
                        double longitude = locationData.getLongitude();
                        polylineOptions.add(new LatLng(latitude, longitude));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                line = mMap.addPolyline(polylineOptions);

                if (locationData != null) {
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(locationData.getLatitude(), locationData.getLongitude()), 14));
                }
            }
        };
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
        requestHistory();
    }
}
