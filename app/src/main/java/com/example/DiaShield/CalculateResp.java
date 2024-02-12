package com.example.DiaShield;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.widget.TextView;
import android.widget.Toast;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.util.ArrayList;
import java.util.List;

public class CalculateResp extends Service implements SensorEventListener {
    final static String CALC_RESP_RATE = "CALC_RESP_RATE";
    private SensorManager accelerateManage;
    TextView viewResp;
    private Sensor senseAccelerate;
    float MValues[] = new float[450];
    float NValues[] = new float[450];
    float OValues[] = new float[450];
    int index = 0;
    int respiratoryVal=0;
    public CalculateResp() {
    }

    @Override
    public void onCreate() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(getApplicationContext(),"Measuring Respiratory Rate...", Toast.LENGTH_LONG).show();
        accelerateManage = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        senseAccelerate = accelerateManage.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        accelerateManage.registerListener(this, senseAccelerate, SensorManager.SENSOR_DELAY_NORMAL);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Sensor mySensor = event.sensor;
        if (mySensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            index++;
            MValues[index] = event.values[0];
            NValues[index] = event.values[1];
            OValues[index] = event.values[2];
            if(index >= 449){
                index = 0;
                accelerateManage.unregisterListener(this);
                Toast.makeText(CalculateResp.this, "Stopped Accelerometer Recording", Toast.LENGTH_LONG).show();
                calculateRespirationRate();
            }
        }
    }

    private void calculateRespirationRate() {
        int windowStart = 0;
        int windowEnd = 20;

        // Calculate moving averages for NValues
        while (windowEnd < 450) {
            float sum = 0;

            // Calculate the sum of values within the window
            for (int i = windowStart; i < windowEnd; i++) {
                sum += NValues[i];
            }

            // Update NValues with the moving average
            NValues[windowStart] = sum / 20;
            windowEnd++;
            windowStart++;
        }

        // Find extrema points in NValues
        List<Integer> extremaIndices = new ArrayList<>();
        for (int i = 0; i < NValues.length - 20; i++) {
            if ((NValues[i + 1] - NValues[i]) * (NValues[i + 2] - NValues[i + 1]) <= 0) {
                extremaIndices.add(i + 1);
            }
        }

        // Calculate respiratory rate based on extrema points
        for (int i = 0; i < extremaIndices.size() - 1; i++) {
            if (extremaIndices.get(i) / 10 != extremaIndices.get(i + 1) / 10) {
                respiratoryVal++;
            }
        }

        // Divide by 2 to get the final respiratory rate
        respiratoryVal /= 2;

        // Display the calculated respiratory rate as a toast message
        Toast.makeText(CalculateResp.this, "Respiratory Rate is : " + String.valueOf(respiratoryVal), Toast.LENGTH_LONG).show();

        // Send a broadcast (assuming sendBroadcast() is defined elsewhere in your code)
        sendBroadcast();
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
    private void sendBroadcast (){
        Intent intent = new Intent ("message");
        intent.putExtra("success", respiratoryVal);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }


}