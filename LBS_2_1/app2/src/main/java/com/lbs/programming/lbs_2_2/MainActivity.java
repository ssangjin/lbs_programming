package com.lbs.programming.lbs_2_2;

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

    private SensorEventListener sensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            switch (event.sensor.getType()) {
                case Sensor.TYPE_ACCELEROMETER:
                    if (System.currentTimeMillis() - lastAccelerationEventTime > 100) {
                        lastAccelerationEventTime = System.currentTimeMillis();
                        // TODO: Acceleration Sensor 값 표출
                    }
                    break;
                case Sensor.TYPE_ROTATION_VECTOR:
                    if (System.currentTimeMillis() - lastRotationEventTime > 100) {
                        lastRotationEventTime = System.currentTimeMillis();

                        // TODO: Rotation vector 표출
                    }
                    break;
                case Sensor.TYPE_STEP_COUNTER:
                    // TODO: Step Counter 표출
                    break;
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // TODO: SensorManager 가져오기

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
        // TODO: Sensor list 확인
    }

    @Override
    protected void onResume() {
        super.onResume();

        // TODO: Acceleration 등록

        // TODO: Rotation vector 등록

        // TODO: Step Counter 등록
    }

    @Override
    protected void onPause() {
        super.onPause();
        // TODO: Sensor 해제
    }
}
