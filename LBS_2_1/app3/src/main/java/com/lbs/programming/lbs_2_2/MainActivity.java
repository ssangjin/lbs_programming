package com.lbs.programming.lbs_2_2;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PointF;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 21;
    private WifiManager wifiManager;
    private WifiScanReceiver wifiScanReceiver;


    private static final int REQUEST_ENABLE_BT = 23;

    // Stops scanning after 10 seconds.
    private static final long SCAN_PERIOD = 10000;

    private BluetoothAdapter bluetoothAdapter;
    private BluetoothLeScanner bluetoothLeScanner;
    private Handler handler;
//    private ListView listview;
//    private ScanResultAdapter scanResultAdapter;

    boolean state = false;

    // Device scan callback.
    private ScanCallback scanCallback = new ScanCallback() {
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onScanResult(int callbackType, final android.bluetooth.le.ScanResult result) {
            super.onScanResult(callbackType, result);
            Log.e("BLE RESULT", result.getDevice().getName() + " "
                    + result.getDevice().getAddress() + " "
                    + (int)calculateDistance(result.getRssi()));

            if ("47:BF:8A:CC:15:FB".equals(result.getDevice().getAddress())) {
                final int distance = (int) calculateDistance(result.getRssi());

                if (state == false && distance < 5) {
                    Toast.makeText(getBaseContext(), "IN", Toast.LENGTH_LONG);
                    state = true;
                } else if (state && distance > 10) {
                    Toast.makeText(getBaseContext(), "out", Toast.LENGTH_LONG);
                    state = false;
                }

                final TextView textView = findViewById(R.id.textViewDistance);
                textView.post(new Runnable() {
                    @Override
                    public void run() {
                        textView.setText(Integer.toString(distance));
                    }
                });
            }
        }

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onBatchScanResults(List<android.bluetooth.le.ScanResult> results) {
            super.onBatchScanResults(results);
            //listview.setAdapter(new ScanResultAdapter(getBaseContext(), results));
            for (android.bluetooth.le.ScanResult scanResult: results) {
                Log.e("BLE RESULT", scanResult.getDevice().getAddress() + " " + calculateDistance(scanResult.getRssi()));
            }
        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        wifiManager = (WifiManager)getApplicationContext().getSystemService(WIFI_SERVICE);

        handler = new Handler();
        // listview = (ListView) findViewById(R.id.listview);
        //scanResultAdapter = new ScanResultAdapter(getBaseContext(), new ArrayList<android.bluetooth.le.ScanResult>());

        // Use this check to determine whether BLE is supported on the device. Then
        // you can selectively disable BLE-related features.
        if (!getPackageManager().
                hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, "BLE is not supported.", Toast.LENGTH_SHORT).show();
            finish();
        }

        // TODO: GET Bluetooth Adaptor.
        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();

        if (bluetoothAdapter != null) {
            bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();

            if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                return;
            }
            scanLeDevice(true);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onResume() {
        super.onResume();
        wifiScanReceiver = new WifiScanReceiver();
        registerReceiver(wifiScanReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

        // TODO: WIFI Scan receiver 생성 / 등록

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            onScan(null);
        }
    }

    @Override
    protected void onPause() {
        unregisterReceiver(wifiScanReceiver);
        super.onPause();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void onScan(View view) {
        wifiManager.startScan();
        scanLeDevice(true);
    }

    private class WifiScanReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            List<ScanResult> scanResultList = wifiManager.getScanResults();
            TableLayout table = (TableLayout) findViewById(R.id.tableLayout);
            table.removeAllViews();

            TableRow row = new TableRow(getBaseContext());

            TextView textView = new TextView(getBaseContext());
            textView.setText("Est distance  ");
            textView.setBackgroundColor(Color.GRAY);
            row.addView(textView);

            Field[] fields = ScanResult.class.getDeclaredFields();
            for (Field field : fields) {
                textView = new TextView(getBaseContext());
                textView.setText("  "+ field.getName() + "  ");
                textView.setBackgroundColor(Color.GRAY);
                row.addView(textView);
            }
            table.addView(row);

            for(ScanResult scanResult : scanResultList) {
                // create a new TableRow
                row = new TableRow(getBaseContext());

                double distance = calculateDistance(scanResult.level);
                textView = new TextView(getBaseContext());
                textView.setText(String.format("%.2f", distance));
                row.addView(textView);

                for (Field field : fields) {
                    textView = new TextView(getBaseContext());
                    try {
                        Object value = field.get(scanResult);

                        if (value == null) {
                            textView.setText("");
                        } else {
                            textView.setText(value.toString());
                        }
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                    row.addView(textView);
                }

                table.addView(row);
            }

            if (scanResultList.size() >= 8) {

                double d1 = 0;
                double d2 = 0;
                double d3 = 0;

                for (ScanResult scanResult: scanResultList) {
                   if (scanResult.SSID.equals("KT_GiGA_2G_BA01")) {
                       d1 = calculateDistance(scanResult.level);
                   }
                   else if (scanResult.SSID.equals("도매꾹세미나룸")) {
                       d2 = calculateDistance(scanResult.level);
                   }
                   else if (scanResult.SSID.equals("RoJ")) {
                       d3 = calculateDistance(scanResult.level);
                   }
                }

                PointF currentPoint = calculatePosition(
                        new Point(20, 20),
                        new Point(10, -10),
                        new Point(-20, 0), d1, d2, d3);
                Toast.makeText(getBaseContext(), currentPoint.toString(), Toast.LENGTH_LONG).show();
            }
        }
    }

    private double calculateDistance(int rssi) {
        int txPower = -50; //hard coded power value. Usually ranges between -59 to -65
        return Math.pow(10, ((double)txPower - rssi) / (10 * 2));
    }

    private PointF calculatePosition(Point p1, Point p2, Point p3, double d1, double d2, double d3) {
        double A = Math.pow(p1.x, 2) + Math.pow(p1.y, 2) - Math.pow(d1, 2);
        double B = Math.pow(p2.x, 2) + Math.pow(p2.y, 2) - Math.pow(d2, 2);
        double C = Math.pow(p3.x, 2) + Math.pow(p3.y, 2) - Math.pow(d3, 2);

        double X32 = p3.x - p2.x;
        double X13 = p1.x - p3.x;
        double X21 = p2.x - p1.x;

        double Y32 = p3.y - p2.y;
        double Y13 = p1.y - p3.y;
        double Y21 = p2.y - p1.y;

        double x = (A * Y32 + B * Y13 + C * Y21) / ( 2 * (p1.x * Y32 + p2.x * Y13 + p3.x * Y21));
        double y = (A * X32 + B * X13 + C * X21) / ( 2 * (p1.y * X32 + p2.y * X13 + p3.y * X21));
        return new PointF((float)x, (float)y);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_ENABLE_BT) {
            scanLeDevice(true);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void scanLeDevice(final boolean scan) {
        if (scan) {
//            scanResultAdapter.clear();

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
}
