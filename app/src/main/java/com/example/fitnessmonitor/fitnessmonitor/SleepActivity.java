package com.example.fitnessmonitor.fitnessmonitor;

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

import java.util.ArrayList;

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

        //open DB
        try {
            sqLiteDatabase = this.openOrCreateDatabase("FitnessMonitorDB", MODE_PRIVATE, null);
        } catch (Exception ex){
            Log.i("EXRUN: ERROR OPENG DB: ", "couldn't open database"+ex.getMessage());
        }

        //get todays readings 9pm yesterday to 6am today
        getTodaysAndYesterdaysReadings();

        

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
//            String query = "SELECT  * FROM tblReadings WHERE timeRecorded >= date('now', 'start of day', 'localtime')";
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
                    int dy = Integer.parseInt(latestDay);

                    //check that hour gotten is between 9pm and 6am, previous day - today
                    if(((hr >= 21 && hr <= 23) && dy != todayDay) || ((hr >= 0 && hr <= 6)) && dy == todayDay) {
                        lstHour.add(Integer.parseInt(latestHour));
                        lstNumSteps.add(latestNumSteps);

                        System.out.println("RETREIVED READINGS: " + cursor.getString(dateIndex) + " " + latestNumSteps + " " + " hour: " + hourParts[1] + " Minute: "+ latestMinute);
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
}
