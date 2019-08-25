package com.example.busstationonmap;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;

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
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import net.osmand.osm.GeoPoint;
import net.osmand.osm.GeoTrans;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 0;

    private GoogleMap mMap;
    private List<BusStation> busStationList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // TODO: add permissions.
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_LOCATION);
        } else {
            // TODO: add location request.
            startLocationService();
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
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MY_PERMISSIONS_REQUEST_LOCATION && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            startLocationService();
        }
    }

    private LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            onLocationChanged(locationResult.getLastLocation());
        }
    };
    private Location lastLocation;

    public void onLocationChanged(Location location) {
        if (location != null) {
            if (lastLocation == null || location.distanceTo(lastLocation) > 1000) {
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                lastLocation = location;

                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (mMap != null) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mMap.clear();
                                }
                            });
                        }

                        // requestBusStation();
                        requestBusStationInSeoul();
                    }
                });
                thread.start();
            }
        }
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

        LatLng latLng = new LatLng(37.484802, 127.035275);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    Activity#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for Activity#requestPermissions for more details.
                return;
            }
        }
        mMap.setMyLocationEnabled(true);
        mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                return false;
            }
        });
    }

    private void requestBusStationInSeoul() {
        // TODO: 현위치로 주변 정류장 요청
        URL url = null;
        HttpURLConnection urlConnection = null;
        try {
            StringBuffer sb = new StringBuffer();
            sb.append("http://ws.bus.go.kr/api/rest/stationinfo/getStationByPos?");
            sb.append("serviceKey=");
            sb.append("P7vj%2B%2FvV5UhtGKHkQldZU%2BrP5hdXIhGsPzz4ujuTdXMY5wzbN6DkQuU5fSos15SpHROJjAZz8M8gQZyhgBJzsA%3D%3D");
            sb.append("&tmY=");
            sb.append(lastLocation.getLatitude());
            sb.append("&tmX=");
            sb.append(lastLocation.getLongitude());
            sb.append("&radius=500");

            Log.d("Result", "url:" + sb.toString());

            url = new URL(sb.toString());
            urlConnection = (HttpURLConnection) url.openConnection();
            parseBusStationList(urlConnection.getInputStream());

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    updateMarkers();
                }
            });
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            urlConnection.disconnect();
        }
    }

    private void requestBusStation() {
        URL url = null;
        HttpURLConnection urlConnection = null;
        try {
            StringBuffer sb = new StringBuffer();
            sb.append("http://openapi.tago.go.kr/openapi/service/BusSttnInfoInqireService/getCrdntPrxmtSttnList?");
            sb.append("serviceKey=");
            sb.append("P7vj%2B%2FvV5UhtGKHkQldZU%2BrP5hdXIhGsPzz4ujuTdXMY5wzbN6DkQuU5fSos15SpHROJjAZz8M8gQZyhgBJzsA%3D%3D");
            sb.append("&gpsLati=");
            sb.append(lastLocation.getLatitude());
            sb.append("&gpsLong=");
            sb.append(lastLocation.getLongitude());

            Log.d("Result", "url:" + sb.toString());

            url = new URL(sb.toString());
            urlConnection = (HttpURLConnection) url.openConnection();
            parseBusStationList(urlConnection.getInputStream());

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    updateMarkers();
                }
            });
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            urlConnection.disconnect();
        }
    }

    private void updateMarkers() {
        // 화면에 마커 표출
        for (BusStation busStation : busStationList) {
            MarkerOptions marker = new MarkerOptions()
                    .position(new LatLng(busStation.getLatitude(), busStation.getLongitude()))
                    .title(busStation.name)
                    .snippet(busStation.getNodeId());
            mMap.addMarker(marker);
        }
    }

    private void parseBusStationList(InputStream xmlString) {
        // TODO: 응답값 분석
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(xmlString);
            doc.getDocumentElement().normalize();
            NodeList nodeList = doc.getDocumentElement().getElementsByTagName("item");
            if (nodeList == null || nodeList.getLength() == 0) {
                nodeList = doc.getDocumentElement().getElementsByTagName("itemList");
            }
            if (nodeList != null && nodeList.getLength() > 0) {
                for (int i = 0; i < nodeList.getLength(); i++) {
                    Node node = nodeList.item(i);
                    NodeList stationInfo = node.getChildNodes();
                    BusStation busStation = new BusStation();

                    for (int j = 0; j < stationInfo.getLength(); j++) {
                        Node item = stationInfo.item(j);
                        switch (item.getNodeName()) {
                            case "citycode":
                                busStation.setCityCode(Integer.parseInt(item.getFirstChild().getNodeValue()));
                                break;
                            case "gpslati":
                            case "gpsY":
                                busStation.setLatitude(Double.parseDouble(item.getFirstChild().getNodeValue()));
                                break;
                            case "gpslong":
                            case "gpsX":
                                busStation.setLongitude(Double.parseDouble(item.getFirstChild().getNodeValue()));
                                break;
                            case "nodeid":
                            case "stationId":
                                busStation.setNodeId(item.getFirstChild().getNodeValue());
                                break;
                            case "nodenm":
                            case "stationNm":
                                busStation.setName(item.getFirstChild().getNodeValue());
                                break;
                        }
                    }

                    if (nodeList.item(0).getNodeName().equals("item")) {
                        busStation.setLatitude(busStation.getLatitude() - 0.0026);
                        busStation.setLongitude(busStation.getLongitude() - 0.000745);
                    }

                    Log.d("Result", i + ": " + busStation.toString());
                    busStationList.add(busStation);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
    }

    static class BusStation {
        private int cityCode;
        private double latitude;
        private double longitude;
        private String nodeId;
        private String name;

        public BusStation() {
        }

        public int getCityCode() {
            return cityCode;
        }

        public double getLatitude() {
            return latitude;
        }

        public double getLongitude() {
            return longitude;
        }

        public String getNodeId() {
            return nodeId;
        }

        public String getName() {
            return name;
        }

        public void setCityCode(int cityCode) {
            this.cityCode = cityCode;
        }

        public void setLatitude(double latitude) {
            this.latitude = latitude;
        }

        public void setLongitude(double longitude) {
            this.longitude = longitude;
        }

        public void setNodeId(String nodeId) {
            this.nodeId = nodeId;
        }

        public void setName(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return "city: " + cityCode + " (" + latitude + ", " + longitude + ") " + name + " [" + nodeId + "]";
        }
    }
}
