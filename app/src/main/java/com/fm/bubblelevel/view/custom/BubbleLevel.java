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

    private static final int TYPE_PORTRAIT = 1;
    private static final int TYPE_LANDSCAPE = 2;
    private static final int TYPE_FLAT = 3;

    private Paint paint;
    private String TAG = "lvl";
    private SensorData sensorData;
    private int screenOrientation;
    private int centerWidth;
    private int centerHeight;
    private int viewWidth;
    private int viewHeight;
    private int outerBorderWidth = 100;


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
        viewWidth = getWidth();
        viewHeight = getHeight();
        centerWidth = viewWidth / 2;
        centerHeight = viewHeight / 2;

        Log.d(TAG, "onDraw: width: " + viewWidth + " height: " + viewHeight);
        if (sensorData != null) {
            //if device portrait or landscape, draw 1D bubble; else draw 2D bubble
            if ((screenOrientation == 0 | screenOrientation == 180) && ((sensorData.getRoll() >= 45 && sensorData.getRoll() < 135) || (sensorData.getRoll() <= -45 && sensorData.getRoll() > -135))) {
                Log.d(TAG, "type: 1D Bubble portrait");
                draw1DBubble(canvas, TYPE_PORTRAIT);
                invalidate();
            } else if (screenOrientation == 90 | screenOrientation == 270) {
                Log.d(TAG, "type: 1D Bubble landscape");
                draw1DBubble(canvas, TYPE_LANDSCAPE);
                invalidate();
            } else {
                Log.d(TAG, "type: 2D Bubble flat");
                draw2DBubble(canvas);
                invalidate();
            }
        }
    }

    private void draw2DBubble(Canvas canvas) {
        float cx = 0;
        float cy = 0;
        paint.setStyle(Paint.Style.FILL);

        //check max/min range
        if (sensorData.getRoll() > MAX_RANGE || sensorData.getPitch() > MAX_RANGE) {
            paint.setColor(Color.RED);
            if (sensorData.getRoll() > MAX_RANGE) {
                cx = (float) (sensorData.getPitch() * 10 + centerWidth);
                cy = (float) (viewHeight - (MAX_RANGE * 10 + centerHeight));
            } else if (sensorData.getPitch() > MAX_RANGE) {
                cx = (float) (MAX_RANGE * 10 + centerWidth);
                cy = (float) (viewHeight - (sensorData.getRoll() * 10 + centerHeight));
            }
        } else if (sensorData.getRoll() < MIN_RANGE || sensorData.getPitch() < MIN_RANGE) {
            paint.setColor(Color.RED);
            if (sensorData.getRoll() < MIN_RANGE) {
                cx = (float) (sensorData.getPitch() * 10 + centerWidth);
                cy = (float) (viewHeight - (MIN_RANGE * 10 + centerHeight));
            } else if (sensorData.getPitch() < MIN_RANGE) {
                cx = (float) (MIN_RANGE * 10 + centerWidth);
                cy = (float) (viewHeight - (sensorData.getRoll() * 10 + centerHeight));
            }
        } else {
            paint.setColor(Color.GREEN);
            cx = (float) sensorData.getPitch() * 10 + centerWidth;
            cy = (float) (viewHeight - (sensorData.getRoll() * 10 + centerHeight));
        }
        canvas.drawCircle(cx, cy, BUBBLE_RADIUS, paint);

        //draw center dot
        paint.setColor(Color.BLACK);
        canvas.drawCircle(centerWidth, centerHeight, 5f, paint);

        //outer border circle
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawCircle(centerWidth, centerHeight, ((float) 10 + centerHeight), paint);
    }

    private void draw1DBubble(Canvas canvas, int type) {
        float cx;
        float cy;
        int borderLeft;
        int borderTop;
        int borderRight;
        int borderBottom;
        paint.setStyle(Paint.Style.FILL);

        //check max/min range
        if (type == TYPE_PORTRAIT) {
            if (sensorData.getPitch() > MAX_RANGE) {
                paint.setColor(Color.RED);
                cx = viewWidth - (MAX_RANGE * 10 + centerWidth);
                cy = centerHeight;
            } else if (sensorData.getPitch() < MIN_RANGE) {
                paint.setColor(Color.RED);
                cx = viewWidth - (MIN_RANGE * 10 + centerWidth);
                cy = centerHeight;
            } else {
                paint.setColor(Color.GREEN);
                cx = (float) (viewWidth - (sensorData.getPitch() * 10 + centerWidth));
                cy = centerHeight;
            }
            //outer border params
            borderLeft = 0;
            borderTop = (centerHeight) - outerBorderWidth;
            borderRight = viewWidth;
            borderBottom = (centerHeight) + outerBorderWidth;
        } else {
            if (sensorData.getRoll() > MAX_RANGE) {
                paint.setColor(Color.RED);
                cx = centerWidth;
                cy = (viewHeight - (MAX_RANGE * 10 + centerHeight));
            } else if (sensorData.getRoll() < MIN_RANGE) {
                paint.setColor(Color.RED);
                cx = centerWidth;
                cy = (viewHeight - (MIN_RANGE * 10 + centerHeight));
            } else {
                paint.setColor(Color.GREEN);
                cx = centerWidth;
                cy = (float) (viewHeight - (sensorData.getRoll() * 10 + centerHeight));
            }

            //outer border params
            borderLeft = (centerWidth) - outerBorderWidth;
            borderTop = 0;
            borderRight = (centerWidth) + outerBorderWidth;
            borderBottom = viewHeight;
        }
        canvas.drawCircle(cx, cy, BUBBLE_RADIUS, paint);

        //draw center dot
        paint.setColor(Color.BLACK);
        canvas.drawCircle(centerWidth, centerHeight, 5f, paint);

        //outer border
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawRect(borderLeft, borderTop, borderRight, borderBottom, paint);
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