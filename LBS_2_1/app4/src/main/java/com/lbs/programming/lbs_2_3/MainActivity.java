package com.lbs.programming.lbs_2_3;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.graphics.PointF;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.lemmingapex.trilateration.NonLinearLeastSquaresSolver;
import com.lemmingapex.trilateration.TrilaterationFunction;

import org.apache.commons.math3.fitting.leastsquares.LeastSquaresOptimizer;
import org.apache.commons.math3.fitting.leastsquares.LevenbergMarquardtOptimizer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_ENABLE_BT = 23;

    // Stops scanning after 10 seconds.
    private static final long SCAN_PERIOD = 10000;

    private BluetoothAdapter bluetoothAdapter;
    private BluetoothLeScanner bluetoothLeScanner;
    private Handler handler;
    private ListView listview;
    private ScanResultAdapter scanResultAdapter;

    // Device scan callback.
    private ScanCallback scanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            scanResultAdapter.add(result);
            listview.setAdapter(scanResultAdapter);
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            super.onBatchScanResults(results);
            listview.setAdapter(new ScanResultAdapter(getBaseContext(), results));
        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        handler = new Handler();
        listview = (ListView) findViewById(R.id.listview);
        scanResultAdapter = new ScanResultAdapter(getBaseContext(), new ArrayList<ScanResult>());

        // Use this check to determine whether BLE is supported on the device. Then
        // you can selectively disable BLE-related features.
        if (!getPackageManager().
                hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, "BLE is not supported.", Toast.LENGTH_SHORT).show();
            finish();
        }

        // TODO: GET Bluetooth Adaptor.
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_ENABLE_BT) {
            scanLeDevice(true);
        }
    }

    private void scanLeDevice(final boolean scan) {
        if (scan) {
            scanResultAdapter.clear();

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
        scanLeDevice(true);
    }

    private class ScanResultAdapter extends ArrayAdapter<ScanResult> {
        double d1 = Double.MAX_VALUE;
        double d2 = Double.MAX_VALUE;
        double d3 = Double.MAX_VALUE;

        List<ScanResult> scanResultArrayList = new ArrayList<>();

        public ScanResultAdapter(Context context, List<ScanResult> scanResults) {
            super(context, R.layout.scan_result_item, scanResults);
            scanResultArrayList = scanResults;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public int getCount() {
            return scanResultArrayList.size();
        }

        @Override
        public void add(@Nullable ScanResult object) {
            super.add(object);
            Iterator<ScanResult> iterator = scanResultArrayList.iterator();
            while (iterator.hasNext()) {
                ScanResult scanResult = iterator.next();
                if (scanResult.getDevice().getAddress().equals(object.getDevice().getAddress())) {
                    iterator.remove();
                }
            }
            scanResultArrayList.add(object);

            Log.e("ScanResult", object.toString());

            // TODO: AP 이름 변경
            if (object != null && object.getDevice() != null && object.getDevice().getAddress() != null) {
                if (object.getDevice().getAddress().equals("51:FC:61:90:E8:2C")) {
                    d1 = calculateDistance(object.getRssi());
                } else if (object.getDevice().getAddress().equals("70:8C:32:EE:93:AB")) {
                    d2 = calculateDistance(object.getRssi());
                } else if (object.getDevice().getAddress().equals("D8:D0:87:03:EA:81")) {
                    d3 = calculateDistance(object.getRssi());
                }

                if (d1 < 1000 && d2 < 1000 && d3 < 1000) {
                    PointF currentPoint = calculatePosition(
                            new Point(20, 20),
                            new Point(10, -10),
                            new Point(-20, 0), d1, d2, d3);
                    Toast.makeText(getBaseContext(), currentPoint.toString(), Toast.LENGTH_LONG).show();
                    Log.e("ScanResultPoint", "x:" + currentPoint.x + ", y:" + currentPoint.y);
                }
            }
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            ScanResult scanResult = getItem(position);
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.scan_result_item, parent, false);
            }

            // Lookup view for data population
            TextView name = (TextView) convertView.findViewById(R.id.name);
            TextView address = (TextView) convertView.findViewById(R.id.address);
            TextView level = (TextView) convertView.findViewById(R.id.level);
            TextView distance = (TextView) convertView.findViewById(R.id.distance);

            name.setText(scanResult.getDevice().getName() + "   |");
            distance.setText(Double.toString(calculateDistance(scanResult.getRssi())) + "   |");
            address.setText(scanResult.getDevice().getAddress() + "   |");
            level.setText(Integer.toString(scanResult.getRssi()) + "  |");

            return convertView;
        }
    }

    private double calculateDistance(int rssi) {
        int txPower = -50; //hard coded power value. Usually ranges between -59 to -65
        return Math.pow(10, ((double)txPower - rssi) / (10 * 2));
    }

    private PointF calculatePosition(Point p1, Point p2, Point p3, double d1, double d2, double d3) {
        // TODO: 삼각측량 구현
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