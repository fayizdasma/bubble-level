package com.fm.bubblelevel.view.custom;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.fm.bubblelevel.model.SensorData;
import com.fm.bubblelevel.utils.AppConstants;

import java.util.ArrayList;
import java.util.Collections;

import static com.fm.bubblelevel.utils.AppConstants.MAX_RANGE;
import static com.fm.bubblelevel.utils.AppConstants.MIN_RANGE;
import static com.fm.bubblelevel.utils.AppConstants.TYPE_LANDSCAPE;
import static com.fm.bubblelevel.utils.AppConstants.TYPE_PORTRAIT;

public class LevelGraph extends View {

    private Paint paint;
    private String TAG = "graph";
    private ArrayList<Integer> getYAxisIntervals;
    private ArrayList<Integer> getXAxisIntervals;
    private int screenOrientation;
    private float deviceWidth;
    private RectF recBar;
    private float onePointY;
    private int MAX_NO_SAMPLES = 50;
    private ArrayList<SensorData> sensorDataArrayList;
    private SensorData currentSensorData;
    private SharedPreferences preferences;
    float graphCenter;
    float gap;
    float gapStarting;
    float widthGraph;
    private float toleranceLevel;
    private float onePointX;
    private Context context;

    public LevelGraph(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    //initialize variables
    private void init() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        recBar = new RectF();
        sensorDataArrayList = new ArrayList<>();
        getYAxisIntervals = getYAxisIntervals();
        getXAxisIntervals = getXAxisIntervals();
        preferences = context.getSharedPreferences(AppConstants.APP_SHARED_PREF, Context.MODE_PRIVATE);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        deviceWidth = getWidth();
        graphCenter = deviceWidth / 2;
        gap = 35;
        gapStarting = gap * 2;
        widthGraph = deviceWidth - gapStarting;

        //get tolerance value from shared preference
        toleranceLevel = preferences.getInt(AppConstants.SHARED_PREF_KEY_TOLERENCE_LEVEL, 0);

        if (sensorDataArrayList.size() > 0) {
            //if device portrait or landscape, draw 1D graph; else draw 2D graph
            if ((screenOrientation == 0 | screenOrientation == 180) && ((currentSensorData.getRoll() >= 45 && currentSensorData.getRoll() < 135) || (currentSensorData.getRoll() <= -45 && currentSensorData.getRoll() > -135))) {
                Log.d(TAG, "type: 1D graph portrait");
                draw1DGraph(canvas, TYPE_PORTRAIT);
            } else if (screenOrientation == 90 | screenOrientation == 270) {
                Log.d(TAG, "type: 1D graph landscape");
                draw1DGraph(canvas, TYPE_LANDSCAPE);
            } else {
                Log.d(TAG, "type: 2D graph flat");
                draw2DGraph(canvas);
            }
        }
    }

    private void draw1DGraph(Canvas canvas, int type) {

        if (type == TYPE_PORTRAIT) {
            //draw y-axis intervals and lines
            paint.setTextAlign(Paint.Align.LEFT);
            for (int i = 0; i < getYAxisIntervals.size(); i++) {
                float pointY = ((widthGraph / getYAxisIntervals.size()) * i) + gap;
                //draw black color for y center line
                if (i == (int) Math.ceil(getYAxisIntervals.size() / 2)) {
                    paint.setColor(Color.BLACK);
                    canvas.drawLine(gapStarting, pointY, deviceWidth, pointY, paint);
                } else {
                    paint.setColor(Color.LTGRAY);
                    canvas.drawLine(gapStarting, pointY, deviceWidth, pointY, paint);
                }
                paint.setColor(Color.DKGRAY);
                paint.setTextSize(20);
                canvas.drawText(String.valueOf(getYAxisIntervals.get(i)), gap, pointY, paint);
            }

            //draw x-axis intervals and lines
            for (int i = 0; i < getXAxisIntervals.size(); i++) {
                paint.setColor(Color.LTGRAY);
                float pointX = ((widthGraph / getXAxisIntervals.size()) * i) + gapStarting;
                canvas.drawLine(pointX, deviceWidth - gap, pointX, gap, paint);
                paint.setTextAlign(Paint.Align.RIGHT);
                paint.setColor(Color.DKGRAY);
                paint.setTextSize(20);
                //for drawing 0
                if (i == 0) {
                    // canvas.drawText("0", pointX, deviceWidth, paint);
                } else
                    canvas.drawText(String.valueOf(getXAxisIntervals.get(i)), pointX, (deviceWidth / 2) - gapStarting, paint);
                //small hack to show label or 50
                if (i == getXAxisIntervals.size() - 1)
                    canvas.drawText("50", deviceWidth, (deviceWidth / 2) - gapStarting, paint);
                paint.setTextAlign(Paint.Align.LEFT);
            }

            //draw line graph
            float previousStopX = 0;
            float previousStopY = 0;
            float stopY;
            float stopX;
            float startY;
            float startX;
            onePointX = ((deviceWidth) / MAX_NO_SAMPLES) / 5;
            onePointY = ((widthGraph / getYAxisIntervals.size()) / 5);

            for (int i = 0; i < sensorDataArrayList.size(); i++) {
                Log.d(TAG, " size():  " + sensorDataArrayList.size() + " i " + i);
                //set color based on tolerance
                if (sensorDataArrayList.get(i).getPitch() > (0 + toleranceLevel) || sensorDataArrayList.get(i).getPitch() < (0 - toleranceLevel)) {
                    paint.setColor(Color.RED);
                } else
                    paint.setColor(Color.GREEN);

                if ((i + 1) < sensorDataArrayList.size()) {
                    double pitch = sensorDataArrayList.get(i).getPitch();
                    //limit pitch to range [-10,10]
                    if (pitch < MIN_RANGE) {
                        pitch = MIN_RANGE;
                    } else if (pitch > MAX_RANGE) {
                        pitch = MAX_RANGE;
                    }
                    //if first data
                    if (i == 0) {
                        startX = gapStarting + previousStopX;
                        startY = (float) ((graphCenter - gapStarting - gap) - (pitch * onePointY));
                        stopX = gapStarting + (onePointX * (i + 1));
                    } else {
                        startX = previousStopX;
                        startY = previousStopY;
                        stopX = startX + (onePointX * (i + 1));
                    }
                    stopY = (float) ((graphCenter - gapStarting - gap) - (pitch * onePointY));
                    canvas.drawLine(startX, startY, stopX, stopY, paint);
                    //  Log.d(TAG, "i:" + i + " pitch:  " + sensorDataArrayList.get(i).getPitch() + " sX " + startX + " sY " + startY + " eX " + stopX + " eY " + stopY);
                    previousStopX = stopX;
                    previousStopY = stopY;
                    // Log.d(TAG, "previousStopX " + previousStopX);
                    // Log.d(TAG, "previousStopY " + previousStopY);
                }
            }
        }
    }

    private void draw2DGraph(Canvas canvas) {
    }

    //method to call the draw function
    public void drawLevelGraph(SensorData sensorData, int screenOrientation) {
        this.screenOrientation = screenOrientation;
        if (sensorData != null) {
            this.currentSensorData = sensorData;
            this.sensorDataArrayList.add(sensorData);
            //if more than 50 samples in list, remove first element to maintain the 50 limit
            if (sensorDataArrayList.size() > MAX_NO_SAMPLES) {
                sensorDataArrayList.remove(0);
            }
            invalidate();
        }
    }

    //get y-axis intervals
    private ArrayList<Integer> getYAxisIntervals() {
        ArrayList<Integer> labelList = new ArrayList<>();
        for (int i = MIN_RANGE; i <= MAX_RANGE; i = i + 5) {
            labelList.add(i);
        }
        //to set label values in descending order
        Collections.reverse(labelList);
        return labelList;
    }

    //get x-axis intervals
    private ArrayList<Integer> getXAxisIntervals() {
        ArrayList<Integer> labelList = new ArrayList<>();
        for (int i = 0; i < MAX_NO_SAMPLES; i = i + 5) {
            labelList.add(i);
        }
        return labelList;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(widthSize, (heightSize));
    }
}