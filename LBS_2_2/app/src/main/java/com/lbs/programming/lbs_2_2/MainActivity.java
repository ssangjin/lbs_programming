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

        // TODO: WIFI Manger 객체 생성.

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_LOCATION);
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MY_PERMISSIONS_REQUEST_LOCATION && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            onScan(null);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // TODO: WIFI Scan receiver 생성 / 등록

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            onScan(null);
        }
    }

    @Override
    protected void onPause() {
        // TODO: WIFI Scan receiver 해제
        super.onPause();
    }

    public void onScan(View view) {
        // TODO: WIFI SCAN
    }

    private class WifiScanReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            List<ScanResult> scanResultList = wifiManager.getScanResults();
            TableLayout table = (TableLayout) findViewById(R.id.tableLayout);
            table.removeAllViews();

            TableRow row = new TableRow(getBaseContext());
            Field[] fields = ScanResult.class.getDeclaredFields();
            for (Field field : fields) {
                TextView textView = new TextView(getBaseContext());
                textView.setText("  "+ field.getName() + "  ");
                textView.setBackgroundColor(Color.GRAY);
                row.addView(textView);
            }
            table.addView(row);

            for(ScanResult scanResult : scanResultList) {
                // create a new TableRow
                row = new TableRow(getBaseContext());

                for (Field field : fields) {
                    TextView textView = new TextView(getBaseContext());
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
}
