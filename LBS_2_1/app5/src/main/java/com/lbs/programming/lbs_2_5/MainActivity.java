package com.lbs.programming.lbs_2_5;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.graphics.PointF;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.lemmingapex.trilateration.NonLinearLeastSquaresSolver;
import com.lemmingapex.trilateration.TrilaterationFunction;

import org.apache.commons.math3.fitting.leastsquares.LeastSquaresOptimizer;
import org.apache.commons.math3.fitting.leastsquares.LevenbergMarquardtOptimizer;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 21;

    private static final int REQUEST_ENABLE_BT = 23;

    // Stops scanning after 10 seconds.
    private static final long SCAN_PERIOD = 10000;

    private WifiManager wifiManager;
    private WifiScanReceiver wifiScanReceiver;

    private BluetoothAdapter bluetoothAdapter;
    private BluetoothLeScanner bluetoothLeScanner;
    private Handler handler;

    // Device scan callback.
    private ScanCallback scanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            calculateBleScanResult(result);
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            super.onBatchScanResults(results);

            for (ScanResult scanResult : results) {
                calculateBleScanResult(scanResult);
            }
        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
        }
    };

    private CustomGeofence geofence;

    private void calculateBleScanResult(ScanResult result) {
        Log.e("ScanResultBLE", result.toString());

        double distance = Double.MAX_VALUE;
        if (result != null && result.getDevice() != null && result.getDevice().getAddress() != null) {
            if (result.getDevice().getAddress().equals("51:FC:61:90:E8:2C")) {
                // Geofence에 위치 업데이트
                geofence.onLocationChanged(CustomGeofence.PROVIDER_BLE, 10, 20, 10);
            } else if (result.getDevice().getAddress().equals("70:8C:32:EE:93:AB")) {
                // Geofence에 위치 업데이트
                geofence.onLocationChanged(CustomGeofence.PROVIDER_BLE,9, 10, 10);
            } else if (result.getDevice().getAddress().equals("D8:D0:87:03:EA:81")) {
                // Geofence에 위치 업데이트
                geofence.onLocationChanged(CustomGeofence.PROVIDER_BLE,40, 50, 10);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
        }

        wifiManager = (WifiManager)getApplicationContext().getSystemService(WIFI_SERVICE);

        handler = new Handler();

        geofence = new CustomGeofence(new CustomGeofence.OnGeofenceTransition() {
            @Override
            public void onTransition(CustomGeofence.Status geofenceTransition) {
                if (geofenceTransition == CustomGeofence.Status.Enter) {
                    Toast.makeText(getBaseContext(), "Entering", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(MainActivity.this, MenuActivity.class);
                    startActivity(intent);

                } else if (geofenceTransition == CustomGeofence.Status.Exit) {
                    Toast.makeText(getBaseContext(), "Exiting", Toast.LENGTH_LONG).show();
                }
            }
        });

        geofence.addFence(14, 15, 10);

        // Use this check to determine whether BLE is supported on the device. Then
        // you can selectively disable BLE-related features.
        if (!getPackageManager().
                hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, "BLE is not supported.", Toast.LENGTH_SHORT).show();
            finish();
        }

        BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();

        bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();

        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            return;
        }

        scanLeDevice(true);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onResume() {
        super.onResume();

        // TODO: WIFI Scan receiver 생성 / 등록
        wifiScanReceiver = new WifiScanReceiver();
        registerReceiver(wifiScanReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            onScan(null);
        }
    }

    @Override
    protected void onPause() {
        unregisterReceiver(wifiScanReceiver);
        super.onPause();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_ENABLE_BT || requestCode == MY_PERMISSIONS_REQUEST_LOCATION) {
            onScan(null);
        }
    }

    private void scanLeDevice(final boolean scan) {
        if (scan) {
            // Stops scanning after a pre-defined scan period.
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    bluetoothLeScanner.stopScan(scanCallback);
                }
            }, SCAN_PERIOD);

            bluetoothLeScanner.startScan(scanCallback);
        } else {
            bluetoothLeScanner.stopScan(scanCallback);
        }

    }

    public void onScan(View view) {
        wifiManager.startScan();
        scanLeDevice(true);
    }

    private class WifiScanReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            List<android.net.wifi.ScanResult> scanResultList = wifiManager.getScanResults();

            if (scanResultList.size() >= 8) {

                double d1 = 0;
                double d2 = 0;
                double d3 = 0;

                for (android.net.wifi.ScanResult scanResult : scanResultList) {
                    Log.e("ScanResultWIFI", scanResult.toString());
                    // TODO: AP 이름 변경
                    if (scanResult.SSID.equals("sj")) {
                        d1 = calculateDistance(scanResult.level);
                    } else if (scanResult.SSID.equals("iptime-SoJu")) {
                        d2 = calculateDistance(scanResult.level);
                    } else if (scanResult.SSID.equals("MK")) {
                        d3 = calculateDistance(scanResult.level);
                    }
                }

                PointF currentPoint = calculatePosition(
                        new Point(20, 20),
                        new Point(10, -10),
                        new Point(-20, 0), d1, d2, d3);
                Toast.makeText(getBaseContext(), currentPoint.toString(), Toast.LENGTH_LONG).show();

                // Geofence에 위치 업데이트
                geofence.onLocationChanged(CustomGeofence.PROVIDER_WIFI, currentPoint.x, currentPoint.y, 10);
            }
        }
    }

    private double calculateDistance(int rssi) {
        int txPower = -50; //hard coded power value. Usually ranges between -59 to -65
        return Math.pow(10, ((double)txPower - rssi) / (10 * 2));
    }

    private PointF calculatePosition(Point p1, Point p2, Point p3, double d1, double d2, double d3) {
        double[][] positions = new double[][] { { p1.x, p1.y }, { p2.x, p2.y }, { p3.x, p3.y } };
        double[] distances = new double[] { d1, d2, d3 };

        NonLinearLeastSquaresSolver solver = new NonLinearLeastSquaresSolver(
                new TrilaterationFunction(positions, distances),
                new LevenbergMarquardtOptimizer());
        LeastSquaresOptimizer.Optimum optimum = solver.solve();

        // the answer
        double[] centroid = optimum.getPoint().toArray();

        return new PointF((float)centroid[0], (float)centroid[1]);
    }
}