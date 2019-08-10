package com.lbs.programming.lbs_2_3;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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

            name.setText(scanResult.getDevice().getName());
            address.setText(scanResult.getDevice().getAddress());
            level.setText(Integer.toString(scanResult.getRssi()));

            return convertView;
        }
    }

}