package com.lbs.programming.lbs_2_2;

import android.Manifest;
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
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 21;
    private WifiManager wifiManager;
    private WifiScanReceiver wifiScanReceiver;


    private static final int REQUEST_ENABLE_BT = 23;

    // Stops scanning after 10 seconds.
    private static final long SCAN_PERIOD = 10000;

    private Handler handler;
//    private ListView listview;
//    private ScanResultAdapter scanResultAdapter;

    boolean state = false;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        wifiManager = (WifiManager)getApplicationContext().getSystemService(WIFI_SERVICE);

        handler = new Handler();
        // listview = (ListView) findViewById(R.id.listview);
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

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void onScan(View view) {
        wifiManager.startScan();
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
                textView.setText(String.format("    %.2f", distance));
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
                   if (scanResult.SSID.equals("sj")) {
                       d1 = calculateDistance(scanResult.level);
                   }
                   else if (scanResult.SSID.equals("MK")) {
                       d2 = calculateDistance(scanResult.level);
                   }
                   else if (scanResult.SSID.equals("iptime")) {
                       d3 = calculateDistance(scanResult.level);
                   }
                }

                PointF currentPoint = calculatePosition(
                        new Point(20, 20),
                        new Point(10, -10),
                        new Point(-20, 0), d1, d2, d3);
                Toast.makeText(getBaseContext(), currentPoint.toString(), Toast.LENGTH_LONG).show();

                TextView distanceView = findViewById(R.id.textViewDistance);
                distanceView.setText("D1: " + d1 + "  \nD2: " + d2 + " \nD3: " + d3 + " \nx:" + currentPoint.x + " y: " + currentPoint.y);
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
    }
}
