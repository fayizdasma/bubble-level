package com.fm.bubblelevel.view;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.fm.bubblelevel.R;
import com.fm.bubblelevel.model.SensorData;
import com.fm.bubblelevel.view.custom.BubbleLevel;
import com.fm.bubblelevel.view.custom.LevelGraph;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private static final String TAG = "mnAct";
    private SensorManager sensorManager;
    private Sensor bubbleSensor;
    private TextView rot;
    private ArrayList<SensorData> sensorDataArrayList;
    private BubbleLevel bubbleLevel;
    private LevelGraph levelGraph;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        rot = (TextView) findViewById(R.id.rotation);
        bubbleLevel = (BubbleLevel) findViewById(R.id.custom_view_bubble);
        levelGraph = (LevelGraph) findViewById(R.id.custom_view_graph);

        if (sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
            bubbleSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            sensorManager.registerListener(this, bubbleSensor, SensorManager.SENSOR_DELAY_NORMAL);
            sensorDataArrayList = new ArrayList<>();
        } else {
            Toast.makeText(this, "Sensor not found", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.values != null) {
            double xAxis = event.values[0];
            double yAxis = event.values[1];
            double zAxis = event.values[2];

            //calculate pitch and roll angles
            double pitch = Math.atan(xAxis / Math.sqrt(Math.pow(yAxis, 2) + Math.pow(zAxis, 2)));
            double roll = Math.atan(yAxis / Math.sqrt(Math.pow(xAxis, 2) + Math.pow(zAxis, 2)));
            Log.d(TAG, "yes: " + " pitch " + Math.round(Math.toDegrees(pitch)) + " roll " + Math.round(Math.toDegrees(roll)));
            rot.setText("pitch " + Math.round(Math.toDegrees(pitch)) + "\n"
                    + "roll " + Math.round(Math.toDegrees(roll)) + "\n"
            );
            //store to array list
            SensorData sensorData = new SensorData();
            sensorData.setPitch(Math.round(Math.toDegrees(pitch)));
            sensorData.setRoll( Math.round(Math.toDegrees(roll)));
            sensorDataArrayList.add(sensorData);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, bubbleSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }
}
