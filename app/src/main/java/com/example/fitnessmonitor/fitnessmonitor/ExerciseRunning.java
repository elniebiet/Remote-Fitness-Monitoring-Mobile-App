package com.example.fitnessmonitor.fitnessmonitor;

import android.app.Fragment;
import android.graphics.Point;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

//public class ExerciseRunning extends AppCompatActivity {
public class ExerciseRunning extends FragmentActivity implements OnMapReadyCallback {
    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_running);

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
        //set flRunning height
        int flRunningHeight = (int)(0.08 * height);
        int flRunningMarginTop = 0;
        FrameLayout flRunning = (FrameLayout) findViewById(R.id.flRunning);
        ViewGroup.LayoutParams flRunningLayoutParams = flRunning.getLayoutParams();
        flRunningLayoutParams.height = flRunningHeight;//(int)(grdMainHeight * 0.9);
        setMargins(flRunning, 0,flRunningMarginTop,0,0);
        flRunning.setLayoutParams(flRunningLayoutParams);
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
        int flCountingHeight = (int)(0.06 * height);
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
        setMargins(flStart, 0,15,0,0);
        flStart.setLayoutParams(flStartLayoutParams);

        FragmentManager fm = getSupportFragmentManager();
        SupportMapFragment supportMapFragment =  SupportMapFragment.newInstance();
        fm.beginTransaction().replace(flMap.getId(), supportMapFragment).commit();
        supportMapFragment.getMapAsync(this);

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

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //add marker
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }
}
