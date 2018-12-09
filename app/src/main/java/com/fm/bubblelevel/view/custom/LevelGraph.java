package com.fm.bubblelevel.view.custom;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.fm.bubblelevel.R;
import com.fm.bubblelevel.model.SensorData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Queue;

import static com.fm.bubblelevel.utils.AppConstants.MAX_RANGE;
import static com.fm.bubblelevel.utils.AppConstants.MIN_RANGE;

public class LevelGraph extends View {

    private Paint paint;
    private String TAG = "graph";
    private ArrayList<Integer> getYAxisIntervals;
    private ArrayList<Integer> getXAxisIntervals;
    private int screenOrientation;
    private float deviceWidth;
    private double maxInterval;
    private RectF recBar;
    private float onePointY;
    private int MAX_NO_SAMPLES = 50;
    private ArrayList<SensorData> sensorDataArrayList;

    public LevelGraph(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    //initialize variables
    private void init() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        recBar = new RectF();
        sensorDataArrayList = new ArrayList<>();
        getYAxisIntervals = getYAxisIntervals();
        getXAxisIntervals = getXAxisIntervals();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        deviceWidth = getWidth();
        float graphCenter = deviceWidth / 2;
        float gap = 35;
        float gapStarting = gap * 2;
        float widthGraph = deviceWidth - gapStarting;
        float barWidth = (deviceWidth - (2 * gap)) / sensorDataArrayList.size();

        //draw y-axis intervals and lines
        paint.setTextAlign(Paint.Align.LEFT);
        for (int i = 0; i < getYAxisIntervals.size(); i++) {
            paint.setColor(Color.DKGRAY);
            float pointY = ((widthGraph / getYAxisIntervals.size()) * i) + gap;
            Log.d(TAG, "draw yaxis val: " + i + " dvc " + Math.ceil(getYAxisIntervals.size()));
            if (i == (int) Math.ceil(getYAxisIntervals.size())) {

                paint.setColor(Color.RED);
                canvas.drawLine(gapStarting, pointY, deviceWidth, pointY, paint);
            } else {
                paint.setColor(Color.DKGRAY);
                canvas.drawLine(gapStarting, pointY, deviceWidth, pointY, paint);
            }
            paint.setColor(Color.DKGRAY);
            paint.setTextSize(20);
            // Log.d(TAG, "draw yaxis val: " + getYAxisIntervals.get(i));
            canvas.drawText(String.valueOf(getYAxisIntervals.get(i)), gap, pointY, paint);
        }


        //draw x-axis intervals and lines
        for (int i = 0; i < getXAxisIntervals.size(); i++) {
            paint.setColor(Color.DKGRAY);
            float pointX = ((widthGraph / getXAxisIntervals.size()) * i) + gapStarting;
            canvas.drawLine(pointX, deviceWidth - gap, pointX, gap, paint);
            paint.setTextAlign(Paint.Align.RIGHT);
            paint.setColor(Color.DKGRAY);
            paint.setTextSize(20);
            //   Log.d(TAG, "draw xaxis val: " + getXAxisIntervals.get(i));
            //for drawing 0
            if (i == 0)
                canvas.drawText("0", pointX, deviceWidth, paint);
            canvas.drawText(String.valueOf(getXAxisIntervals.get(i)), pointX + barWidth, (deviceWidth / 2) - gapStarting, paint);
            paint.setTextAlign(Paint.Align.LEFT);
        }



    }

    //method to call the draw function
    public void drawLevelGraph(SensorData sensorData, int screenOrientation) {
        this.screenOrientation = screenOrientation;
        if (sensorData != null) {
            this.sensorDataArrayList.add(sensorData);
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