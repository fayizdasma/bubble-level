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

    private Paint paint;
    private String TAG = "bubble";
    private SensorData sensorData;


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
    }

    //method to call the draw function
    public void drawBubbleView(SensorData sensorData) {
        this.sensorData = sensorData;
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(widthSize, (heightSize / 2));
    }

}