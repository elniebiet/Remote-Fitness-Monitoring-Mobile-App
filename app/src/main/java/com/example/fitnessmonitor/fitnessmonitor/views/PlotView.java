package com.example.fitnessmonitor.fitnessmonitor.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.ColorRes;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.example.fitnessmonitor.fitnessmonitor.StatActivity;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Aniebiet Akpan on 22/06/20.
 */

public class PlotView extends View {

    private float gridHeight = 0.f;
    private float gridWidth = 0.f;
    private Paint paint;
    private Rect rect;
    private HashMap<Integer,Integer> stepsPerHour = new HashMap<Integer, Integer>();

    public PlotView(Context context) {
        super(context);

        init(null);
    }

    public PlotView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        init(attrs);
    }

    public PlotView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    public PlotView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    private void init(@Nullable AttributeSet set){
        rect = new Rect();
        paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setTextSize(30.f);
        paint.setStrokeWidth(3.f);
        paint.setStyle(Paint.Style.STROKE);
    }

    @Override
    public void onDraw(Canvas canvas){
//        canvas.drawColor(Color.GRAY);
        gridHeight = (float)(StatActivity.getCanvasHeight());
        gridWidth = (float)(StatActivity.getCanvasWidth());

        stepsPerHour = StatActivity.returnStepsPerHour();

        float maxBinHeight = (float)(0.8 * gridHeight);
        float binWidth = gridWidth/24.0f;

//        canvas.drawRect(0, 0, gridWidth, gridHeight, paint);
//        canvas.drawLine();
        //draw grid lines
        //for x-axis
        float marginBottom = (float) (0.10 * gridHeight);
        float marginLeftRight = (float) (0.02 * gridWidth);
        float startX = marginLeftRight;
        float startY = gridHeight - marginBottom;
        float stopX = gridWidth - marginLeftRight;
        float stopY = gridHeight - marginBottom;

        canvas.drawLine( startX, startY, stopX, stopY, paint);
        float marginBottomText = (float)(0.05 * gridHeight);
        float startPointX = marginLeftRight;
        float lineWidth = stopX - startX;
        float gapX = lineWidth/24;
        //WRITE POINTS
        //divide width to 24 bins
        float st = startPointX;
        for(int i=0; i <= 23; i++){
            if(i%6 != 0 && i != 23)
                ;
            else {
                if(i == 6 || i == 12 || i == 18)
                    canvas.drawText(Integer.toString(i), st - gapX, gridHeight - marginBottomText, paint);
                else
                    canvas.drawText(Integer.toString(i), st, gridHeight - marginBottomText, paint);
            }
            st += gapX;

        }
        //DRAW BINS
        float maxStepsForMaxBin = 5000.f / 9.f;
        ArrayList<Float> binHeights = new ArrayList<Float>();
        float tempHeights = 0.f;
        for(int i=0; i<stepsPerHour.size(); i++){
            tempHeights = ((float)(stepsPerHour.get(i)) / 5000.f * maxStepsForMaxBin);
            tempHeights = (tempHeights > maxStepsForMaxBin) ? maxStepsForMaxBin : tempHeights;
            binHeights.add(tempHeights);
        }
        System.out.println("BIN HEIGHTS: " + binHeights);

        float mainBinWidth  = (float)0.7 * binWidth; //use 80% of space for bin width

        //draw first bin
        float startXTop = (float)0.15 * binWidth;
        float startYTop = gridHeight - marginBottom - binHeights.get(0);
        float stopXBottom = startXTop + mainBinWidth;
        float stopYBottom =  gridHeight - marginBottom;
        canvas.drawRect(startXTop, startYTop, stopXBottom, stopYBottom, paint);

        float startXPoint = startXTop;
        //
        for(int i=1; i < 24; i++){

            startXPoint += mainBinWidth + startXTop;

            float staY = gridHeight - marginBottom - binHeights.get(i);
            float stoX = startXPoint + mainBinWidth;
            float stoY = stopYBottom;

//            canvas.drawRect(startXPoint, staY, stoX, stoY, paint);
            canvas.drawRect(startXPoint + binWidth, staY, stoX + binWidth, stoY, paint);
        }
    }
}
