package com.lbs.programming.lbs_2_1;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private SensorManager sensorManager;
    private Sensor accelerationSensor;
    private Sensor rotationVectorSensor;
    private Sensor stepCounterSensor;

    private TextView textViewValueX;
    private TextView textViewValueY;
    private TextView textViewValueZ;

    private TextView textViewRotationValueX;
    private TextView textViewRotationValueY;
    private TextView textViewRotationValueZ;

    private TextView textViewStepCountValue;

    private long lastAccelerationEventTime = 0;
    private long lastRotationEventTime = 0;

    // TODO: Sensor Event Listener 구현.
    private SensorEventListener sensorEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // TODO: SensorManager 가져오기.

        showSensorList();

        textViewValueX = findViewById(R.id.textViewValueX);
        textViewValueY = findViewById(R.id.textViewValueY);
        textViewValueZ = findViewById(R.id.textViewValueZ);

        textViewRotationValueX = findViewById(R.id.textViewRotationValueX);
        textViewRotationValueY = findViewById(R.id.textViewRotationValueY);
        textViewRotationValueZ = findViewById(R.id.textViewRotationValueZ);

        textViewStepCountValue = findViewById(R.id.textViewStepCountValue);
    }

    private void showSensorList() {
        // TODO: Sensor List 출력
    }

    @Override
    protected void onResume() {
        super.onResume();

        // TODO: Accelerometer 등록

        // TODO: Rotation vector 등록

        // TODO: step counter 등록
    }

    @Override
    protected void onPause() {
        // TODO: unregister sensor listener
        super.onPause();
    }
}
