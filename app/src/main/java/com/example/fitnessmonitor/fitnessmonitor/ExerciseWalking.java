package com.example.fitnessmonitor.fitnessmonitor;

import android.graphics.Point;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.GridLayout;

public class ExerciseWalking extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_walking);

        //get current screen dimensions
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;
        System.out.println("width is "+width);
        //set flBack height
        int flBackHeight = (int)(0.05 * height);
        int flBackMarginTop = 10;
        FrameLayout flBack = (FrameLayout) findViewById(R.id.flBack);
        ViewGroup.LayoutParams flBackLayoutParams = flBack.getLayoutParams();
        flBackLayoutParams.height = flBackHeight;//(int)(grdMainHeight * 0.9);
        setMargins(flBack, 0,flBackMarginTop,0,0);
        flBack.setLayoutParams(flBackLayoutParams);
        //set flWalking height
        int flWalkingHeight = (int)(0.08 * height);
        int flWalkingMarginTop = 0;
        FrameLayout flWalking = (FrameLayout) findViewById(R.id.flWalking);
        ViewGroup.LayoutParams flWalkingLayoutParams = flWalking.getLayoutParams();
        flWalkingLayoutParams.height = flWalkingHeight;//(int)(grdMainHeight * 0.9);
        setMargins(flWalking, 0,flWalkingMarginTop,0,0);
        flWalking.setLayoutParams(flWalkingLayoutParams);
        //set flTarget height
        int flTargetHeight = (int)(0.07 * height);
        int flTargetMarginTop = 0;
        FrameLayout flTarget = (FrameLayout) findViewById(R.id.flTarget);
        ViewGroup.LayoutParams flTargetLayoutParams = flTarget.getLayoutParams();
        flTargetLayoutParams.height = flTargetHeight;//(int)(grdMainHeight * 0.9);
        setMargins(flTarget, 0,flTargetMarginTop,0,0);
        flTarget.setLayoutParams(flTargetLayoutParams);
        //set flCount height
        int flCountHeight = (int)(0.08 * height);
        int flCountMarginTop = 0;
        FrameLayout flCount = (FrameLayout) findViewById(R.id.flCount);
        ViewGroup.LayoutParams flCountLayoutParams = flCount.getLayoutParams();
        flCountLayoutParams.height = flCountHeight;//(int)(grdMainHeight * 0.9);
        setMargins(flCount, 0,flCountMarginTop,0,0);
        flCount.setLayoutParams(flCountLayoutParams);
        //set flCounting height
        int flCountingHeight = (int)(0.08 * height);
        int flCountingMarginTop = 0;
        FrameLayout flCounting = (FrameLayout) findViewById(R.id.flCounting);
        ViewGroup.LayoutParams flCountingLayoutParams = flCounting.getLayoutParams();
        flCountingLayoutParams.height = flCountingHeight;//(int)(grdMainHeight * 0.9);
        setMargins(flCounting, 0,flCountingMarginTop,0,0);
        flCounting.setLayoutParams(flCountingLayoutParams);
        //set flMap height
        int flMapHeight = (int)(0.6 * height);
        int flMapMarginTop = 0;
        FrameLayout flMap = (FrameLayout) findViewById(R.id.flMap);
        ViewGroup.LayoutParams flMapLayoutParams = flMap.getLayoutParams();
        flMapLayoutParams.height = flMapHeight;//(int)(grdMainHeight * 0.9);
        setMargins(flMap, 0,flMapMarginTop,0,0);
        flMap.setLayoutParams(flMapLayoutParams);
        //set flStart height
        int flStartHeight = (int)(0.1 * height);
        int flStartMarginTop = 0;
        FrameLayout flStart = (FrameLayout) findViewById(R.id.flStart);
        ViewGroup.LayoutParams flStartLayoutParams = flStart.getLayoutParams();
        flStartLayoutParams.height = flStartHeight;//(int)(grdMainHeight * 0.9);
        setMargins(flStart, 0,flStartMarginTop,0,0);
        flStart.setLayoutParams(flStartLayoutParams);
    }

    public void goBackHome(View view){
        finish();
    }

    public void setMargins (View v, int l, int t, int r, int b) {
        if (v.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            p.setMargins(l, t, r, b);
            v.requestLayout();
        }
    }
}
