package com.fm.bubblelevel.view.custom;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.fm.bubblelevel.model.SensorData;
import com.fm.bubblelevel.utils.AppConstants;
import com.fm.bubblelevel.utils.AppUtils;

import static com.fm.bubblelevel.utils.AppConstants.MAX_RANGE;
import static com.fm.bubblelevel.utils.AppConstants.MIN_RANGE;
import static com.fm.bubblelevel.utils.AppConstants.TYPE_LANDSCAPE;
import static com.fm.bubblelevel.utils.AppConstants.TYPE_PORTRAIT;

public class BubbleLevel extends View {

    private static final float BUBBLE_RADIUS = 55f;

    private Paint paint;
    private String TAG = "lvl";
    private SensorData sensorData;
    private int screenOrientation;
    private int centerWidth;
    private int centerHeight;
    private int viewWidth;
    private int viewHeight;
    private SharedPreferences preferences;
    private int outerBorderWidth = 100;
    private float toleranceLevel;
    private Context context;
    private boolean isVibration;

    public BubbleLevel(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    //initialize variables
    private void init() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        preferences = context.getSharedPreferences(AppConstants.APP_SHARED_PREF, Context.MODE_PRIVATE);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        viewWidth = getWidth();
        viewHeight = getHeight();
        centerWidth = viewWidth / 2;
        centerHeight = viewHeight / 2;

        //get tolerance value from shared preference
        toleranceLevel = preferences.getInt(AppConstants.SHARED_PREF_KEY_TOLERENCE_LEVEL, 5);
        isVibration = preferences.getBoolean(AppConstants.SHARED_PREF_KEY_IS_VIBRATION, true);

        Log.d(TAG, "onDraw: width: " + viewWidth + " height: " + viewHeight);
        if (sensorData != null) {
            //if device portrait or landscape, draw 1D bubble; else draw 2D bubble
            if ((screenOrientation == 0 | screenOrientation == 180) && ((sensorData.getRoll() >= 45 && sensorData.getRoll() < 135) || (sensorData.getRoll() <= -45 && sensorData.getRoll() > -135))) {
                Log.d(TAG, "type: 1D Bubble portrait");
                draw1DBubble(canvas, TYPE_PORTRAIT);
                // invalidate();
            } else if (screenOrientation == 90 | screenOrientation == 270) {
                Log.d(TAG, "type: 1D Bubble landscape");
                draw1DBubble(canvas, TYPE_LANDSCAPE);
                //invalidate();
            } else {
                Log.d(TAG, "type: 2D Bubble flat");
                draw2DBubble(canvas);
                //invalidate();
            }
        }
    }

    private void draw2DBubble(Canvas canvas) {
        float cx = 0;
        float cy = 0;
        float gap = 35;
        paint.setStyle(Paint.Style.FILL);

        //check max/min range
        if (sensorData.getRoll() > MAX_RANGE || sensorData.getPitch() > MAX_RANGE) {
            if (sensorData.getRoll() > MAX_RANGE) {
                cx = (float) (sensorData.getPitch() * 10 + centerWidth);
                cy = (float) (viewHeight - (MAX_RANGE * 10 + centerHeight));
            } else if (sensorData.getPitch() > MAX_RANGE) {
                cx = (float) (MAX_RANGE * 10 + centerWidth);
                cy = (float) (viewHeight - (sensorData.getRoll() * 10 + centerHeight));
            }
        } else if (sensorData.getRoll() < MIN_RANGE || sensorData.getPitch() < MIN_RANGE) {
            if (sensorData.getRoll() < MIN_RANGE) {
                cx = (float) (sensorData.getPitch() * 10 + centerWidth);
                cy = (float) (viewHeight - (MIN_RANGE * 10 + centerHeight));
            } else if (sensorData.getPitch() < MIN_RANGE) {
                cx = (float) (MIN_RANGE * 10 + centerWidth);
                cy = (float) (viewHeight - (sensorData.getRoll() * 10 + centerHeight));
            }
        } else {
            cx = (float) sensorData.getPitch() * 10 + centerWidth;
            cy = (float) (viewHeight - (sensorData.getRoll() * 10 + centerHeight));
        }

        //set color based on tolerance
        if ((sensorData.getRoll() > (0 + toleranceLevel) || sensorData.getRoll() < (0 - toleranceLevel)) || (sensorData.getPitch() > (0 + toleranceLevel) || sensorData.getPitch() < (0 - toleranceLevel))) {
            paint.setColor(Color.RED);
            if (isVibration)
                AppUtils.shortVibrate(context);
        } else
            paint.setColor(Color.GREEN);
        //draw circle bubble
        canvas.drawCircle(cx, cy, BUBBLE_RADIUS, paint);

        //draw center dot
        paint.setColor(Color.BLACK);
        canvas.drawCircle(centerWidth, centerHeight, 5f, paint);

        //outer border circle
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawCircle(centerWidth, centerHeight, ((float) 10 + centerHeight - gap), paint);
    }

    private void draw1DBubble(Canvas canvas, int type) {
        float cx;
        float cy;
        int borderLeft;
        int borderTop;
        int borderRight;
        int borderBottom;
        paint.setStyle(Paint.Style.FILL);

        if (type == TYPE_PORTRAIT) {
            //check max/min range
            if (sensorData.getPitch() > MAX_RANGE) {
                cx = viewWidth - (MAX_RANGE * 10 + centerWidth);
                cy = centerHeight;
            } else if (sensorData.getPitch() < MIN_RANGE) {
                cx = viewWidth - (MIN_RANGE * 10 + centerWidth);
                cy = centerHeight;
            } else {
                cx = (float) (viewWidth - (sensorData.getPitch() * 10 + centerWidth));
                cy = centerHeight;
            }
            //outer border params
            borderLeft = 0;
            borderTop = (centerHeight) - outerBorderWidth;
            borderRight = viewWidth;
            borderBottom = (centerHeight) + outerBorderWidth;

            //tolerance color
            if (sensorData.getPitch() > (0 + toleranceLevel) || sensorData.getPitch() < (0 - toleranceLevel)) {
                paint.setColor(Color.RED);
                if (isVibration)
                    AppUtils.shortVibrate(context);
            } else
                paint.setColor(Color.GREEN);
        } else {
            if (sensorData.getRoll() > MAX_RANGE) {
                cx = viewWidth - (MAX_RANGE * 10 + centerWidth);
                cy = centerHeight;
            } else if (sensorData.getRoll() < MIN_RANGE) {
                cx = viewWidth - (MIN_RANGE * 10 + centerWidth);
                cy = centerHeight;
            } else {
                cx = (float) (viewWidth - (sensorData.getRoll() * 10 + centerWidth));
                cy = centerHeight;
            }
            //outer border params
            borderLeft = 0;
            borderTop = (centerHeight) - outerBorderWidth;
            borderRight = viewWidth;
            borderBottom = (centerHeight) + outerBorderWidth;

            //tolerance color
            if (sensorData.getRoll() > (0 + toleranceLevel) || sensorData.getRoll() < (0 - toleranceLevel)) {
                paint.setColor(Color.RED);
                if (isVibration)
                    AppUtils.shortVibrate(context);
            } else
                paint.setColor(Color.GREEN);
        }
        //draw circle bubble
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