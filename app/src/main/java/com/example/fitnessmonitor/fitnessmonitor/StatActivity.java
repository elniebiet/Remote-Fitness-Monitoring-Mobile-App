package com.example.fitnessmonitor.fitnessmonitor;

import android.graphics.Point;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

public class StatActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stat);

        //get current screen dimensions
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;
        //set frmBack height
        int frmBackHeight = (int)(0.07 * height);
        int frmBackMarginTop = 10;
        FrameLayout frmBack = (FrameLayout) findViewById(R.id.frmBackButton);
        ViewGroup.LayoutParams frmBackLayoutParams = frmBack.getLayoutParams();
        frmBackLayoutParams.height = frmBackHeight;//(int)(grdMainHeight * 0.9);
        setMargins(frmBack, 0,frmBackMarginTop,0,0);
        frmBack.setLayoutParams(frmBackLayoutParams);
        //set frmDailySteps height
        int frmDailyStepsHeight = (int)(0.25 * height);
        int frmDailyStepsMarginTop = frmBackMarginTop + frmBackHeight + 10;
        FrameLayout flDailySteps = (FrameLayout) findViewById(R.id.frmDailySteps);
        ViewGroup.LayoutParams flDailyStepsLayoutParams = flDailySteps.getLayoutParams();
        flDailyStepsLayoutParams.height = frmDailyStepsHeight;//(int)(grdMainHeight * 0.9);
        setMargins(flDailySteps, 0,frmDailyStepsMarginTop,0,0);
        flDailySteps.setLayoutParams(flDailyStepsLayoutParams);
        //set frmCanvas height
        int flCanvasHeight = (int)(0.25 * height);
        int flCanvasMarginTop = frmDailyStepsMarginTop + frmDailyStepsHeight + 10;
        FrameLayout flCanvas = (FrameLayout) findViewById(R.id.frmCanvas);
        ViewGroup.LayoutParams flCanvasLayoutParams = flCanvas.getLayoutParams();
        flCanvasLayoutParams.height = flCanvasHeight;//(int)(grdMainHeight * 0.9);
        setMargins(flCanvas, 0,flCanvasMarginTop,0,0);
        flCanvas.setLayoutParams(flCanvasLayoutParams);
        //set frmDistanceCovered height
        int flDistanceHeight = (int)(0.15 * height);
        int flDistanceMarginTop = flCanvasMarginTop + flCanvasHeight + 10;
        FrameLayout flDistance = (FrameLayout) findViewById(R.id.frmDistanceCovered);
        ViewGroup.LayoutParams flDistanceParamsLayout = flDistance.getLayoutParams();
        flDistanceParamsLayout.height = flDistanceHeight;//(int)(grdMainHeight * 0.9);
        setMargins(flDistance, 0,flDistanceMarginTop,0,0);
        flDistance.setLayoutParams(flDistanceParamsLayout);
        //set frmCalories height
        int flCaloriesMarginTop = flDistanceMarginTop + flDistanceHeight + 10;
        FrameLayout flCalories = (FrameLayout) findViewById(R.id.frmCalories);
        ViewGroup.LayoutParams flCaloriesLayoutParams = flCalories.getLayoutParams();
        flCaloriesLayoutParams.height = flDistanceHeight;//(int)(grdMainHeight * 0.9);
        setMargins(flCalories, 0,flCaloriesMarginTop,0,0);
        flCalories.setLayoutParams(flCaloriesLayoutParams);
    }

    public void setMargins (View v, int l, int t, int r, int b) {
        if (v.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            p.setMargins(l, t, r, b);
            v.requestLayout();
        }
    }

    public void goBackHome(View view){
        finish();
    }
}
