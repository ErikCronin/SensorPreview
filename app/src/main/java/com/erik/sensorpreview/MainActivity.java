package com.erik.sensorpreview;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private TextView xTextview, yTextview, zTextview, vibrateCheck;
    private SensorManager sensorManager;
    private Sensor accSensor;
    private boolean isAccAvail, isNotFirstTime;
    private float currentX, currentY, currentZ, lastX, lastY, lastZ;
    private float xDifference, yDifference, zDifference;
    private float shakeThreshold = 5f;
    private Vibrator vibrator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        xTextview = findViewById(R.id.Xvalue);
        yTextview = findViewById(R.id.Yvalue);
        zTextview = findViewById(R.id.Zvalue);
        vibrateCheck = findViewById(R.id.scream);

        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        //Check if sensor is available
        if(sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null){
            accSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            isAccAvail = true;
        } else {
            xTextview.setText("Accelerometer sensor is not available.");
            isAccAvail = false;
        }
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        xTextview.setText(sensorEvent.values[0]+"m/s2");
        yTextview.setText(sensorEvent.values[1]+"m/s2");
        zTextview.setText(sensorEvent.values[2]+"m/s2");


        currentX = sensorEvent.values[0];
        currentY = sensorEvent.values[1];
        currentZ = sensorEvent.values[2];

        if(isNotFirstTime){
            xDifference = Math.abs(lastX - currentX);
            yDifference = Math.abs(lastY - currentY);
            zDifference = Math.abs(lastZ - currentZ);

            if((xDifference > shakeThreshold && yDifference > shakeThreshold)
                    || (xDifference > shakeThreshold && zDifference > shakeThreshold)
                    || (yDifference > shakeThreshold && zDifference > shakeThreshold)){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    //when phone is shaken it will do this
                    vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
                    vibrateCheck.setText("Yes Vibrate");
                } else {
                    vibrator.vibrate(500);
                    //depreciated in API 26
                }
            }
        }

        lastX = currentX;
        lastY = currentY;
        lastZ = currentZ;
        isNotFirstTime = true;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    protected void onResume() {
        super.onResume();

        if (isAccAvail){
            sensorManager.registerListener(this, accSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (isAccAvail){
            sensorManager.unregisterListener(this);
        }
    }
}
