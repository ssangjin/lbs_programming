package com.lbs.programming.lbs_2_2;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.lang.reflect.Field;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 21;
    private WifiManager wifiManager;
    private WifiScanReceiver wifiScanReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        wifiManager = (WifiManager)getApplicationContext().getSystemService(WIFI_SERVICE);
    }

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
        }
    }

    private double calculateDistance(int rssi) {

        int txPower = -59; //hard coded power value. Usually ranges between -59 to -65
        return Math.pow(10, ((double)txPower - rssi) / (10 * 2));
    }
}
