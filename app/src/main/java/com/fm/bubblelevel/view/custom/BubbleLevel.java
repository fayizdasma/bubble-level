package com.fm.bubblelevel.view.custom;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.fm.bubblelevel.model.SensorData;

public class BubbleLevel extends View {

    private static final float BUBBLE_RADIUS = 55f;
    private static final float MAX_RANGE = 10;
    private static final float MIN_RANGE = -10;
    private Paint paint;
    private String TAG = "lvl";
    private SensorData sensorData;
    private int screenOrientation;


    public BubbleLevel(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    //initialize variables
    private void init() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.d(TAG, "onDraw: width: " + getWidth() + " height: " + getHeight());
        if (sensorData != null) {

            //if device portrait or landscape, draw 1D bubble; else draw 2D bubble
            if ((screenOrientation == 0 | screenOrientation == 180) && ((sensorData.getRoll() >= 45 && sensorData.getRoll() < 135) || (sensorData.getRoll() <= -45 && sensorData.getRoll() > -135))) {
                Log.d(TAG, "type: 1D Bubble portrait");
                paint.setStyle(Paint.Style.FILL);

                if (sensorData.getPitch() > MAX_RANGE) {
                    paint.setColor(Color.RED);
                    canvas.drawCircle((float) (getWidth() - (MAX_RANGE * 10 + getWidth() / 2)), (float) getHeight() / 2, BUBBLE_RADIUS, paint);
                } else if (sensorData.getPitch() < MIN_RANGE) {
                    paint.setColor(Color.RED);
                    canvas.drawCircle((float) (getWidth() - (MIN_RANGE * 10 + getWidth() / 2)), (float) getHeight() / 2, BUBBLE_RADIUS, paint);
                } else {
                    paint.setColor(Color.GREEN);
                    canvas.drawCircle((float) (getWidth() - (sensorData.getPitch() * 10 + getWidth() / 2)), (float) getHeight() / 2, BUBBLE_RADIUS, paint);
                }
                //draw center dot
                paint.setColor(Color.BLACK);
                canvas.drawCircle(getWidth() / 2, getHeight() / 2, 5f, paint);

                //outer border circle
                paint.setStyle(Paint.Style.STROKE);
                canvas.drawRect(0, (getHeight() / 2) - 100, getWidth(), (getHeight() / 2) + 100, paint);
                invalidate();

            } else if (screenOrientation == 90 | screenOrientation == 270) {
                Log.d(TAG, "type: 1D Bubble landscape");
                paint.setStyle(Paint.Style.FILL);
                paint.setColor(Color.RED);

                if (sensorData.getRoll() > MAX_RANGE) {
                    paint.setColor(Color.RED);
                    canvas.drawCircle((float) getWidth() / 2, (float) (getHeight() - (MAX_RANGE * 10 + getHeight() / 2)), BUBBLE_RADIUS, paint);
                } else if (sensorData.getRoll() < MIN_RANGE) {
                    paint.setColor(Color.RED);
                    canvas.drawCircle((float) getWidth() / 2, (float) (getHeight() - (MIN_RANGE * 10 + getHeight() / 2)), BUBBLE_RADIUS, paint);
                } else {
                    paint.setColor(Color.GREEN);
                    canvas.drawCircle((float) getWidth() / 2, (float) (getHeight() - (sensorData.getRoll() * 10 + getHeight() / 2)), BUBBLE_RADIUS, paint);
                }
                //draw center dot
                paint.setColor(Color.BLACK);
                canvas.drawCircle(getWidth() / 2, getHeight() / 2, 5f, paint);

                //outer border circle
                paint.setStyle(Paint.Style.STROKE);
                canvas.drawRect((getWidth() / 2) - 100, 0, (getWidth() / 2) + 100, getHeight(), paint);
                invalidate();
            } else {
                Log.d(TAG, "type: 2D Bubble flat");
                paint.setStyle(Paint.Style.FILL);
                paint.setColor(Color.RED);
                canvas.drawCircle((float) sensorData.getPitch() * 10 + getWidth() / 2, (float) (getHeight() - (sensorData.getRoll() * 10 + getHeight() / 2)), BUBBLE_RADIUS, paint);

                //draw center dot
                paint.setColor(Color.BLACK);
                canvas.drawCircle(getWidth() / 2, getHeight() / 2, 5f, paint);

                //outer border circle
                paint.setStyle(Paint.Style.STROKE);
                canvas.drawCircle(getWidth() / 2, getHeight() / 2, ((float) 10 + getHeight() / 2), paint);
                invalidate();
            }
        }
    }

    //method to call the draw function
    public void drawBubbleView(SensorData sensorData, int screenOrientation) {
        this.sensorData = sensorData;
        this.screenOrientation = screenOrientation;
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(widthSize, (heightSize / 2));
    }
}