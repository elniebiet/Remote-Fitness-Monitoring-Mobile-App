package com.example.fitnessmonitor.fitnessmonitor;

/**
 * Created by Aniebiet Akpan.
 */

import android.content.pm.ActivityInfo;
import android.graphics.Point;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

public class RecentWorkOuts extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recent_work_outs);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        //get current screen dimensions
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;
        //set flBack height
        int flBackHeight = (int)(0.07 * height);
        int flBackMarginTop = 10;
        FrameLayout flBack = (FrameLayout) findViewById(R.id.flBack);
        ViewGroup.LayoutParams flBackLayoutParams = flBack.getLayoutParams();
        flBackLayoutParams.height = flBackHeight;//(int)(grdMainHeight * 0.9);
        setMargins(flBack, 0,flBackMarginTop,0,0);
        flBack.setLayoutParams(flBackLayoutParams);
        //set flMessage height
        int flMessageHeight = (int)(0.12 * height);
        int flMessageMarginTop = 0;
        FrameLayout flMessage = (FrameLayout) findViewById(R.id.flMessage);
        ViewGroup.LayoutParams flMessageLayoutParams = flMessage.getLayoutParams();
        flMessageLayoutParams.height = flMessageHeight;//(int)(grdMainHeight * 0.9);
        setMargins(flMessage, 10,flMessageMarginTop,0,0);
        flMessage.setLayoutParams(flMessageLayoutParams);
        //set flList height
        int flListHeight = (int)(0.75 * height);
        int flListMarginTop = 0;
        FrameLayout flList = (FrameLayout) findViewById(R.id.flList);
        ViewGroup.LayoutParams flListLayoutParams = flList.getLayoutParams();
        flListLayoutParams.height = flListHeight;//(int)(grdMainHeight * 0.9);
        setMargins(flList, 10,flListMarginTop,0,0);
        flList.setLayoutParams(flListLayoutParams);

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
