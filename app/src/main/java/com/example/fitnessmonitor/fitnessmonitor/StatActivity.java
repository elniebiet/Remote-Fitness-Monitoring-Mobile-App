package com.example.fitnessmonitor.fitnessmonitor;

/**
 * Created by Aniebiet Akpan.
 */
import android.content.Context;
import android.content.pm.ActivityInfo;
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
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.fitnessmonitor.fitnessmonitor.views.PlotView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class StatActivity extends AppCompatActivity {
    private TextView txtStepsToday;
    private TextView txtDistance;
    private TextView txtCalories;
    private int currentNumberOfSteps = 0;
    private int currentDistanceCovered = 0;
    private int currentCaloriesBurnt = 0;
    private SQLiteDatabase sqLiteDatabase = null;
    private int userWeight = 70; //70kg as default weight
    private int userHeight = 170; //170cm as default height in cm
    private int latestNumSteps = 0;
    private String latestTimeStamp = "";
    private String latestHour = "";
    private String latestMinute = "";
    private String latestDay = "";
    private ArrayList<Integer> lstHour = new ArrayList<>();
    private ArrayList<Integer> lstNumSteps = new ArrayList<>();
    private ArrayList<Integer> tempList = new ArrayList<>();
    private static HashMap<Integer,Integer> mpStepsPerHour = new HashMap<Integer, Integer>();//hashmap to hold hour and steps
    private Canvas cvDisplaySteps;
    private ImageView imgCanvas;
    public Bitmap mybitmap,newbmp,bitmap,bmp;
    private static int flCanvasHeight = 0;
    private static int flCanvasWidth = 0;
    Paint paint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stat);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        //get weight and height if supplied
        try {
            sqLiteDatabase = this.openOrCreateDatabase("FitnessMonitorDB", MODE_PRIVATE, null);
        } catch (Exception ex){
            Log.i("EXRUN: ERROR OPENG DB: ", "couldn't open database"+ex.getMessage());
        }

        //check if profile details have been supplied
        getWeightAndHeight();

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
        flCanvasHeight = (int)(0.25 * height);
        flCanvasWidth = (int)(0.95 * width);

        int flCanvasMarginTop = frmDailyStepsMarginTop + frmDailyStepsHeight + 10;
        FrameLayout flCanvas = (FrameLayout) findViewById(R.id.frmCanvas);
        ViewGroup.LayoutParams flCanvasLayoutParams = flCanvas.getLayoutParams();
        flCanvasLayoutParams.height = flCanvasHeight;//(int)(grdMainHeight * 0.9);
        flCanvasLayoutParams.width = flCanvasWidth;
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

        txtStepsToday = (TextView) findViewById(R.id.txtStepsToday);
        txtDistance = (TextView) findViewById(R.id.txtDistance);
        txtCalories = (TextView) findViewById(R.id.txtCalories);


        currentNumberOfSteps = MainActivity.getCurrentNumSteps();
        currentDistanceCovered = (int)(userHeight / 100 * 0.45 * currentNumberOfSteps); //in meters
        currentCaloriesBurnt = (int) (userWeight * currentDistanceCovered * 1.036); //in calories

        txtStepsToday.setText(Integer.toString(currentNumberOfSteps));
        txtDistance.setText(String.format("%.2f km", currentDistanceCovered/1000.0f));
        txtCalories.setText(String.format("%.2f kcal", currentCaloriesBurnt/1000.0f)); //output in kcal

        //get readings for today
        getTodaysReadings();

        //analyse readings, get steps per hour
        getStepsPerHour();

        //display plot
        displayPlot();

        PlotView plotView = new PlotView(this);


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

    private void getWeightAndHeight(){
        if(sqLiteDatabase != null){
            try {
                Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM tblUserProfile", null);
                int heightIndex = cursor.getColumnIndex("height");
                int weightIndex = cursor.getColumnIndex("weight");

                boolean cursorResponse = cursor.moveToFirst();

                if (cursorResponse) { //profile details supplied
                    //get the user details
                    userHeight = cursor.getInt(heightIndex);
                    userWeight = cursor.getInt(weightIndex);
                    Log.i("EX RUNNING", "profile details supplied");


                } else {
                    Log.i("EX RUNNING", "profile details not supplied");
                    //user profile details not specified
                }
            } catch (Exception ex){
                Log.i("ERROR FETCHN USER DET", ex.getMessage());
            }
        }
    }

    private void getTodaysReadings(){
        //clear the lists
        lstHour.clear();
        lstNumSteps.clear();
        String currentTimeStamp = MainActivity.getCurrentTimeStamp();
        Log.i("STAT ACT CTS: ", currentTimeStamp);
        try {
            String query = "SELECT  * FROM tblReadings WHERE timeRecorded >= date('now', '-0 day', 'localtime')";

            Cursor cursor = sqLiteDatabase.rawQuery(query, null);
            int dateIndex = cursor.getColumnIndex("timeRecorded");
            int numStepsIndex = cursor.getColumnIndex("numOfSteps");
            boolean cursorResponse = cursor.moveToFirst();

            if (cursorResponse) {
                //ignore first 3 readings
                int cnt = 0;
                while(cursor.moveToNext() && cnt < 3){
                    ; cnt++;
                }
                String datetimeParts[];
                String dateHourParts[];
                String hourParts[];
//
                while(cursor.moveToNext()){
                    //get first number of steps for the day
                    latestTimeStamp = cursor.getString(dateIndex);
                    latestNumSteps = cursor.getInt(numStepsIndex);
                    datetimeParts = latestTimeStamp.split(":");
                    dateHourParts = datetimeParts[0].split("-");
                    hourParts = dateHourParts[2].split(" ");
                    latestDay = hourParts[0];
                    latestHour = hourParts[1];
                    latestMinute = datetimeParts[1];

                    System.out.println("LATEST READINGS: " + cursor.getString(dateIndex) + " " + latestNumSteps + " " + " hour: " + hourParts[1] + " Minute: "+ latestMinute);
                    lstHour.add(Integer.parseInt(latestHour));
                    lstNumSteps.add(latestNumSteps);
                }


            } else {
                latestNumSteps = 0;
//                System.out.println("LATEST READINGS: " + latestNumSteps);
            }
        } catch (Exception ex){
            Log.i("ERR GETNG LATST READGS ", ex.getMessage());
        }
    }

    private void getStepsPerHour(){


        for(int i=0; i<24; i++) {
            tempList.clear();
            for(int j=0; j<lstHour.size(); j++){
                if(lstHour.get(j) == i){
                    tempList.add(lstNumSteps.get(j));
                }
            }
            int lowestPerHr = 0;
            int highestPerHr = 0;
            if(tempList.size() != 0) {
                lowestPerHr = Collections.min(tempList);
                highestPerHr = Collections.max(tempList);
            }
            mpStepsPerHour.put(i, highestPerHr - lowestPerHr);
            System.out.println("LOWEST - HEIGHEST: " + lowestPerHr + " " + highestPerHr);
        }

        for(Map.Entry m: mpStepsPerHour.entrySet()){
            System.out.println(m.getKey() + " " + m.getValue());
        }
    }

    private void displayPlot(){

    }

    public static int getCanvasHeight(){
        return flCanvasHeight;
    }

    public static int getCanvasWidth(){
        return flCanvasWidth;
    }

    public static HashMap<Integer, Integer> returnStepsPerHour(){
        return mpStepsPerHour;
    }
}
