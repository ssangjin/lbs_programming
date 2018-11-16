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
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // TODO: WIFI Manager 생성
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
        // TODO: WIFI Scan receiver 해제​
        super.onPause();
    }

    public void onScan(View view) {
        // TODO: WIFI Scan​
    }

    private class WifiScanReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            List<ScanResult> scanResultList = wifiManager.getScanResults();
            TableLayout table = (TableLayout) findViewById(R.id.tableLayout);
            table.removeAllViews();

            TableRow row = new TableRow(getBaseContext());

            // TODO : TableView에 컬럼 추가​
            TextView textView = new TextView(getBaseContext());

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

                // TODO : Distance column에 값 추가​

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
                // TODO: 원하는 AP를 찾아 아래의 index를 변경할 것.
                double d1 = calculateDistance(scanResultList.get(5).level);
                double d2 = calculateDistance(scanResultList.get(6).level);
                double d3 = calculateDistance(scanResultList.get(7).level);

                PointF currentPoint = calculatePosition(new Point(20, 20), new Point(10, 10), new Point(-10, -10), d1, d2, d3);
                Toast.makeText(getBaseContext(), currentPoint.toString(), Toast.LENGTH_LONG).show();
            }
        }
    }

    private double calculateDistance(int rssi) {
        int txPower = -59; //hard coded power value. Usually ranges between -59 to -65
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
}
