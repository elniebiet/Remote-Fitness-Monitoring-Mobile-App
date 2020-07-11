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
    private String deviceID = "";
    private int discoverable = 0;
    private SQLiteDatabase sqLiteDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inter_user_access);

        //check device discoverability
        sqLiteDatabase = this.openOrCreateDatabase("FitnessMonitorDB", MODE_PRIVATE, null);
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM tblDeviceID", null);
        int idIndex = cursor.getColumnIndex("deviceID");
        int discoverableIndex = cursor.getColumnIndex("discoverable");

        boolean cursorResponse = cursor.moveToFirst();

        try {
            if (cursorResponse) {
                //get the device id and device discoverability
                deviceID = cursor.getString(idIndex);
                discoverable = cursor.getInt(discoverableIndex);
                Log.i("DEVICE ID IS: ", deviceID);

            } else {

            }
        } catch(Exception ex){
            System.out.println("INTER-USER: ERROR GETTING DEVICE ID");
        }

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
        //Check initial state of discoverability
        if(discoverable == 1){
            //device discoverable
            btnDiscoverability.setText("DISABLE DISCOVERABILITY");
        } else {
            btnDiscoverability.setText("ENABLE DISCOVERABILITY");
        }

        //calculate distance covered and calories burnt
//        currentDistanceCovered = (int)(userHeight / 100 * 0.45 * currentNumberOfSteps); //in meters
//        currentCaloriesBurnt = (int) (userWeight * currentDistanceCovered * 1.036); //in calories



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
        //check discoverabiltiy
        if(discoverable == 0) {
            discoverable = 1; //toggle disvoverability
            btnDiscoverability.setText("DISABLE DISCOVERABILITY");
            //update database
            try {
                String query = "UPDATE tblDeviceID SET discoverable = 1 WHERE deviceID = '" + deviceID + "';";
                sqLiteDatabase.execSQL(query);
                Log.i("SUCCESS UPDATING DIS", "set discoverability to 1");

            } catch(Exception e){
                Log.i("ERROR UPDATING DISC", "Couldnt Update discoverability");
                e.printStackTrace();
            }
            Toast.makeText(getApplicationContext(), "Device is now visible", Toast.LENGTH_SHORT).show();
        } else {
            discoverable = 0; //toggle disvoverability
            btnDiscoverability.setText("ENABLE DISCOVERABILITY");
            //update database
            try {
                String query = "UPDATE tblDeviceID SET discoverable = 0 WHERE deviceID = '" + deviceID + "';";
                sqLiteDatabase.execSQL(query);
                Log.i("SUCCESS UPDATING DIS", "set discoverability to 0");

            } catch(Exception e){
                Log.i("ERROR UPDATING DISC", "Couldnt update discoverability");
                e.printStackTrace();
            }
            Toast.makeText(getApplicationContext(), "Device is no longer visible", Toast.LENGTH_SHORT).show();
        }

    }

}
