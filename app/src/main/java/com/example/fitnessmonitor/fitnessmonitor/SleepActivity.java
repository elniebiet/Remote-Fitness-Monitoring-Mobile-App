package com.example.fitnessmonitor.fitnessmonitor;

/**
 * Created by Aniebiet Akpan.
 */
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Point;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class SleepActivity extends AppCompatActivity {

    private ArrayList<Integer> lstHour = new ArrayList<>();
    private ArrayList<Integer> lstNumSteps = new ArrayList<>();
    private ArrayList<Integer> tempList = new ArrayList<>();
    private SQLiteDatabase sqLiteDatabase = null;
    private int latestNumSteps = 0;
    private String latestTimeStamp = "";
    private String latestHour = "";
    private String latestMinute = "";
    private String latestDay = "";
    private static Map<Integer,Integer> mpStepsPerHour = new LinkedHashMap<Integer, Integer>();//hashmap to hold hour and steps
    private static Map<Integer,Integer> mpStepsPerHourArranged = new LinkedHashMap<Integer, Integer>();//hashmap to hold hour and steps
    private ImageView imgFrom;
    private ImageView imgTo;
    private TextView txtDuration;
    private TextView lblSleepTime;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sleep);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
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

        imgFrom = (ImageView)findViewById(R.id.imgFrom);
        imgTo = (ImageView)findViewById(R.id.imgTo);
        txtDuration = (TextView)findViewById(R.id.txtDuration);
        lblSleepTime = (TextView)findViewById(R.id.lblSleepTime);

        imgFrom.setVisibility(View.INVISIBLE);
        imgTo.setVisibility(View.INVISIBLE);
        txtDuration.setText("No sleep records yet");

        //open DB
        try {
            sqLiteDatabase = this.openOrCreateDatabase("FitnessMonitorDB", MODE_PRIVATE, null);
        } catch (Exception ex){
            Log.i("EXRUN: ERROR OPENG DB: ", "couldn't open database"+ex.getMessage());
        }

        //get todays readings 9pm yesterday to 7am today
        getTodaysAndYesterdaysReadings();

        //get steps per hour
        getStepsPerHour();

        String hourlySteps = "";
        for(Map.Entry me: mpStepsPerHour.entrySet()){
            if((int)me.getValue() == 0){
                hourlySteps += 0;
            } else {
                hourlySteps += 1;
            }
        }

        int startHr = 0;
        int endHr = 0;
        int realStartHr = 0;
        int realEndHr = 0;
        int sleepDetected = 0;
        int sleepLength = 0;
        if(hourlySteps.length() > 4){
            //check for 10 hrs
            if(hourlySteps.indexOf("0000000000") != -1){
                startHr = hourlySteps.indexOf("0000000000");
                endHr = startHr + 9;
                sleepDetected = 1;
                sleepLength = 10;
            }
            //check for 9 hrs
            else if(hourlySteps.indexOf("000000000") != -1){
                startHr = hourlySteps.indexOf("000000000");
                endHr = startHr + 8;
                sleepDetected = 1;
                sleepLength = 9;
            }
            //check for 8 hrs
            else if(hourlySteps.indexOf("00000000") != -1){
                startHr = hourlySteps.indexOf("00000000");
                endHr = startHr + 7;
                sleepDetected = 1;
                sleepLength = 8;
            }
            //check for 7 hrs
            else if(hourlySteps.indexOf("0000000") != -1){
                startHr = hourlySteps.indexOf("0000000");
                endHr = startHr + 6;
                sleepDetected = 1;
                sleepLength = 7;
            }
            //check for 6 hrs
            else if(hourlySteps.indexOf("000000") != -1){
                startHr = hourlySteps.indexOf("000000");
                endHr = startHr + 5;
                sleepDetected = 1;
                sleepLength = 6;
            }
            //check for 5hrs
            else if(hourlySteps.indexOf("00000") != -1){
                startHr = hourlySteps.indexOf("00000");
                endHr = startHr + 4;
                sleepDetected = 1;
                sleepLength = 5;
            }
            //check for 4hrs
            else if(hourlySteps.indexOf("0000") != -1){
                startHr = hourlySteps.indexOf("0000");
                endHr = startHr + 3;
                sleepDetected = 1;
                sleepLength = 4;
            }
            //get the sleep time
            if(sleepDetected == 1){
                int i=0;
                for(Map.Entry me: mpStepsPerHour.entrySet()){
                    if(i == startHr){
                        realStartHr = (int)(me.getKey());
                    }
                    if(i == endHr){
                        realEndHr = (int)(me.getKey());
                    }
                    i++;
                }

            }
        }

        if(sleepDetected == 1) {
            System.out.println("SLEEP DETECTED: " + Integer.toString(realStartHr) + " to " + Integer.toString(realEndHr) + " SLEEP LENGTH: " + Integer.toString(sleepLength));
            //get current hour
            String currentTimeSt = MainActivity.getCurrentTimeStamp();
            String datetimeParts[] = currentTimeSt.split(":");
            String dateHourParts[] = datetimeParts[0].split("-");
            String hourParts[] = dateHourParts[2].split(" ");
            int todayDay = Integer.parseInt(hourParts[0]);
            String currentHr = hourParts[1];

            //check if current hour is greater than 7am
            if(Integer.parseInt(currentHr) > 7){
                //display detected sleep
                txtDuration.setText("Were you asleep ?");
                imgFrom.setVisibility(View.VISIBLE);
                imgTo.setVisibility(View.VISIBLE);
                float rotationStart = (float)(realStartHr % 12) / 12.f * 360.f;
                imgFrom.setRotation(rotationStart);
                float rotationEnd = (float)(realEndHr % 12) / 12.f * 360.f;
                imgTo.setRotation(rotationEnd);
                Log.i("ROTATION ANGLE", Float.toString(rotationStart) + " " + Float.toString(rotationEnd));
                lblSleepTime.setText("Sleep Time: " + Integer.toString(realStartHr) + ":00" + " - " + Integer.toString(realEndHr) + ":00 "+ "(" + Integer.toString(sleepLength) + " hrs )");

                System.out.println("SLEEP DETECTED: " + Integer.toString(realStartHr) + " to " + Integer.toString(realEndHr) + " SLEEP LENGTH: " + Integer.toString(sleepLength) + " CURRENT HR: "+ currentHr);
            } else {
                //do not display detected sleep
                txtDuration.setText("No sleep records yet");
                imgFrom.setVisibility(View.INVISIBLE);
                imgTo.setVisibility(View.INVISIBLE);
            }
        } else {
            //no sleep detected
            txtDuration.setText("No sleep records yet");
            imgFrom.setVisibility(View.INVISIBLE);
            imgTo.setVisibility(View.INVISIBLE);
        }
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

    private void getTodaysAndYesterdaysReadings(){
        //clear the lists
        lstHour.clear();
        lstNumSteps.clear();
        String currentTimeStamp = MainActivity.getCurrentTimeStamp();
        Log.i("STAT ACT CTS: ", currentTimeStamp);
        try {
            String query = "SELECT  * FROM tblReadings WHERE timeRecorded >= date('now', '-1 day', 'localtime')";

            Cursor cursor = sqLiteDatabase.rawQuery(query, null);
            int dateIndex = cursor.getColumnIndex("timeRecorded");
            int numStepsIndex = cursor.getColumnIndex("numOfSteps");
            boolean cursorResponse = cursor.moveToFirst();

            if (cursorResponse) {
                //get first number of steps for the day
                latestTimeStamp = cursor.getString(dateIndex);
                latestNumSteps = cursor.getInt(numStepsIndex);
                String datetimeParts[] = latestTimeStamp.split(":");
                String dateHourParts[] = datetimeParts[0].split("-");
                String hourParts[] = dateHourParts[2].split(" ");
                latestDay = hourParts[0];
                latestHour = hourParts[1];
                latestMinute = datetimeParts[1];

//                System.out.println("LATEST READINGS: " + cursor.getString(dateIndex) + " " + latestNumSteps + " " + " hour: " + hourParts[1] + " Minute: "+ latestMinute);
                lstHour.add(Integer.parseInt(latestHour));
                lstNumSteps.add(latestNumSteps);
                //get today
                String currentTimeSt = MainActivity.getCurrentTimeStamp();
                String datetimePrts[] = currentTimeSt.split(":");
                String dateHourPrts[] = datetimePrts[0].split("-");
                String hourPrts[] = dateHourPrts[2].split(" ");
                int todayDay = Integer.parseInt(hourPrts[0]);

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

                    int hr = Integer.parseInt(latestHour);
                    int mn = Integer.parseInt(latestMinute);
                    int dy = Integer.parseInt(latestDay);

                    //check that hour gotten is between 9pm and 7am, previous day - today
                    //also ignore readings before 00:05
                    if(((hr >= 21 && hr <= 23) && dy != todayDay) || ((hr >= 0 && hr <= 7)) && dy == todayDay) {
                        if(!(hr == 0 && mn < 5)) {
                            lstHour.add(Integer.parseInt(latestHour));
                            lstNumSteps.add(latestNumSteps);

                            System.out.println("RETREIVED READINGS: " + cursor.getString(dateIndex) + " " + latestNumSteps + " " + " hour: " + hourParts[1] + " Minute: " + latestMinute);
                        }
                    }
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

        //get 21:00 - 23:00
        for(int i=21; i<23; i++) {
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
//            System.out.println("LOWEST - HEIGHEST: " + lowestPerHr + " " + highestPerHr);
        }

        //add get 00:00 - 07:00
        for(int i=0; i<= 7; i++) {
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
//            System.out.println("LOWEST - HEIGHEST: " + lowestPerHr + " " + highestPerHr);
        }

        for(Map.Entry m: mpStepsPerHour.entrySet()){
            System.out.println(m.getKey() + " " + m.getValue());
        }
    }
}
