package com.example.fitnessmonitor.fitnessmonitor;

import android.graphics.Point;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

public class SleepActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sleep);

        //get current screen dimensions
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;

        //set flBack height
        int frmBackHeight = (int)(0.07 * height);
        FrameLayout frmBack = (FrameLayout) findViewById(R.id.frmBack);
        ViewGroup.LayoutParams frmBackLayoutParams = frmBack.getLayoutParams();
        frmBackLayoutParams.height = frmBackHeight;//(int)(grdMainHeight * 0.9);
        setMargins(frmBack, 0,10,0,0);
        frmBack.setLayoutParams(frmBackLayoutParams);
        //set frmDate height
        int frmDateHeight = (int)(0.1 * height);
        FrameLayout frmDate = (FrameLayout) findViewById(R.id.frmDate);
        ViewGroup.LayoutParams frmDateLayoutParams = frmDate.getLayoutParams();
        frmDateLayoutParams.height = frmDateHeight;//(int)(grdMainHeight * 0.9);
        setMargins(frmDate, 0,0,0,0);
        frmDate.setLayoutParams(frmDateLayoutParams);
        //set frmClock height
        int frmClockHeight = (int)(0.6 * height);
        FrameLayout frmClock = (FrameLayout) findViewById(R.id.frmClock);
        ViewGroup.LayoutParams frmClockLayoutParams = frmClock.getLayoutParams();
        frmClockLayoutParams.height = frmClockHeight;//(int)(grdMainHeight * 0.9);
        setMargins(frmClock, 0,0,0,0);
        frmClock.setLayoutParams(frmClockLayoutParams);
        //set frmDuration height
        int frmDurationHeight = (int)(0.25 * height);
        FrameLayout frmDuration = (FrameLayout) findViewById(R.id.frmDuration);
        ViewGroup.LayoutParams frmDurationLayoutParams = frmDuration.getLayoutParams();
        frmDurationLayoutParams.height = frmDurationHeight;//(int)(grdMainHeight * 0.9);
        setMargins(frmDuration, 0,0,0,0);
        frmDuration.setLayoutParams(frmDurationLayoutParams);

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
