package com.example.fitnessmonitor.fitnessmonitor;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fitnessmonitor.fitnessmonitor.views.PlotView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class InterUserAccess extends AppCompatActivity {

    private TextView txtStepsToday;
    private TextView txtDistance;
    private TextView txtCalories;

    private int currentNumberOfSteps = 0;
    private int currentDistanceCovered = 0;
    private int currentCaloriesBurnt = 0;

    private int userWeight = 70; //70kg as default weight
    private int userHeight = 170; //170cm as default height in cm

    private FrameLayout frmBack;
    private FrameLayout frmSearch;
    private FrameLayout frmBodyTemp;
    private FrameLayout frmHeartRate;
    private FrameLayout frmDailySteps;
    private FrameLayout frmDistanceCovered;
    private FrameLayout frmCalories;
    private EditText edtSearch;
    private Button btnDiscoverability;
    private int discoverable = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inter_user_access);


        //get current screen dimensions
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;

        //set frmBack height
        int frmBackHeight = (int)(0.05 * height);
        int frmBackMarginTop = 10;
        frmBack = (FrameLayout) findViewById(R.id.frmBackButton);
        ViewGroup.LayoutParams frmBackLayoutParams = frmBack.getLayoutParams();
        frmBackLayoutParams.height = frmBackHeight;//(int)(grdMainHeight * 0.9);
        setMargins(frmBack, 0,frmBackMarginTop,0,0);
        frmBack.setLayoutParams(frmBackLayoutParams);

        int otherFramesHeight = (int)(0.9 * height) / 7;

        //set frmSearch height
        int frmSearchMarginTop = frmBackMarginTop + frmBackHeight + 10;
        frmSearch = (FrameLayout) findViewById(R.id.frmSearch);
        ViewGroup.LayoutParams frmSearchLayoutParams = frmSearch.getLayoutParams();
        frmSearchLayoutParams.height = otherFramesHeight;//(int)(grdMainHeight * 0.9);
        setMargins(frmSearch, 0,frmSearchMarginTop,0,0);
        frmSearch.setLayoutParams(frmSearchLayoutParams);

        //set frmBodyTemp height
        int frmBodyTempMarginTop = frmSearchMarginTop + otherFramesHeight + 10;
        frmBodyTemp = (FrameLayout) findViewById(R.id.frmBodyTemp);
        ViewGroup.LayoutParams frmBodyTempLayoutParams = frmBodyTemp.getLayoutParams();
        frmBodyTempLayoutParams.height = otherFramesHeight;//(int)(grdMainHeight * 0.9);
        setMargins(frmBodyTemp, 0,frmBodyTempMarginTop,0,0);
        frmBodyTemp.setLayoutParams(frmBodyTempLayoutParams);

        //set frmHeartRate height
        int frmHeartRateMarginTop = frmBodyTempMarginTop + otherFramesHeight + 10;
        frmHeartRate = (FrameLayout) findViewById(R.id.frmHeartRate);
        ViewGroup.LayoutParams frmHeartRateLayoutParams = frmHeartRate.getLayoutParams();
        frmHeartRateLayoutParams.height = otherFramesHeight;//(int)(grdMainHeight * 0.9);
        setMargins(frmHeartRate, 0,frmHeartRateMarginTop,0,0);
        frmHeartRate.setLayoutParams(frmHeartRateLayoutParams);

        //set frmDailySteps height
        int frmDailyStepsMarginTop = frmHeartRateMarginTop + otherFramesHeight + 10;
        frmDailySteps = (FrameLayout) findViewById(R.id.frmDailySteps);
        ViewGroup.LayoutParams frmDailyStepsLayoutParams = frmDailySteps.getLayoutParams();
        frmDailyStepsLayoutParams.height = otherFramesHeight;//(int)(grdMainHeight * 0.9);
        setMargins(frmDailySteps, 0,frmDailyStepsMarginTop,0,0);
        frmDailySteps.setLayoutParams(frmDailyStepsLayoutParams);

        //set frmDistanceCovered height
        int frmDistanceCoveredMarginTop = frmDailyStepsMarginTop + otherFramesHeight + 10;
        frmDistanceCovered = (FrameLayout) findViewById(R.id.frmDistanceCovered);
        ViewGroup.LayoutParams frmDistanceCoveredLayoutParams = frmDistanceCovered.getLayoutParams();
        frmDistanceCoveredLayoutParams.height = otherFramesHeight;//(int)(grdMainHeight * 0.9);
        setMargins(frmDistanceCovered, 0,frmDistanceCoveredMarginTop,0,0);
        frmDistanceCovered.setLayoutParams(frmDistanceCoveredLayoutParams);

        //set frmCalories height
        int frmCaloriesMarginTop = frmDistanceCoveredMarginTop + otherFramesHeight + 10;
        frmCalories = (FrameLayout) findViewById(R.id.frmCalories);
        ViewGroup.LayoutParams frmCaloriesLayoutParams = frmCalories.getLayoutParams();
        frmCaloriesLayoutParams.height = otherFramesHeight;//(int)(grdMainHeight * 0.9);
        setMargins(frmCalories, 0,frmCaloriesMarginTop,0,0);
        frmCalories.setLayoutParams(frmCaloriesLayoutParams);

        frmBodyTemp.setVisibility(View.INVISIBLE);
        frmHeartRate.setVisibility(View.INVISIBLE);
        frmDailySteps.setVisibility(View.INVISIBLE);
        frmDistanceCovered.setVisibility(View.INVISIBLE);
        frmCalories.setVisibility(View.INVISIBLE);

        edtSearch = (EditText) findViewById(R.id.edtSearch);
        btnDiscoverability = (Button) findViewById(R.id.btnDiscoverability);

        //calculate distance covered and calories burnt
//        currentDistanceCovered = (int)(userHeight / 100 * 0.45 * currentNumberOfSteps); //in meters
//        currentCaloriesBurnt = (int) (userWeight * currentDistanceCovered * 1.036); //in calories








//        //set frmDistanceCovered height
//        int flDistanceHeight = (int)(0.15 * height);
//        int flDistanceMarginTop = frmDailyStepsMarginTop + frmDailyStepsHeight + 10;
//        FrameLayout flDistance = (FrameLayout) findViewById(R.id.frmDistanceCovered);
//        ViewGroup.LayoutParams flDistanceParamsLayout = flDistance.getLayoutParams();
//        flDistanceParamsLayout.height = flDistanceHeight;//(int)(grdMainHeight * 0.9);
//        setMargins(flDistance, 0,flDistanceMarginTop,0,0);
//        flDistance.setLayoutParams(flDistanceParamsLayout);
//        //set frmCalories height
//        int flCaloriesMarginTop = flDistanceMarginTop + flDistanceHeight + 10;
//        FrameLayout flCalories = (FrameLayout) findViewById(R.id.frmCalories);
//        ViewGroup.LayoutParams flCaloriesLayoutParams = flCalories.getLayoutParams();
//        flCaloriesLayoutParams.height = flDistanceHeight;//(int)(grdMainHeight * 0.9);
//        setMargins(flCalories, 0,flCaloriesMarginTop,0,0);
//        flCalories.setLayoutParams(flCaloriesLayoutParams);
//
//        txtStepsToday = (TextView) findViewById(R.id.txtStepsToday);
//        txtDistance = (TextView) findViewById(R.id.txtDistance);
//        txtCalories = (TextView) findViewById(R.id.txtCalories);
//
//
//        currentNumberOfSteps = MainActivity.getCurrentNumSteps();
//        currentDistanceCovered = (int)(userHeight / 100 * 0.45 * currentNumberOfSteps); //in meters
//        currentCaloriesBurnt = (int) (userWeight * currentDistanceCovered * 1.036); //in calories
//
//        txtStepsToday.setText(Integer.toString(currentNumberOfSteps));
//        txtDistance.setText(String.format("%.2f km", currentDistanceCovered/1000.0f));
//        txtCalories.setText(String.format("%.2f kcal", currentCaloriesBurnt/1000.0f)); //output in kcal




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

    public void checkFitnessClicked(View view){
        String suppliedId = edtSearch.getText().toString().trim();
        suppliedId = suppliedId.toUpperCase();
        if(suppliedId.contains("USER")){
            //valid user id format

        } else {
            Toast.makeText(getApplicationContext(), "invalid User id", Toast.LENGTH_SHORT).show();
        }
    }

    public void btnDiscoverabilityClicked(View view){
        String btnText = btnDiscoverability.getText().toString();

        Toast.makeText(getApplicationContext(), btnText, Toast.LENGTH_SHORT).show();
    }

}
