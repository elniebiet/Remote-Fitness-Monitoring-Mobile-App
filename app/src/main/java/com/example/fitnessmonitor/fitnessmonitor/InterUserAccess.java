package com.example.fitnessmonitor.fitnessmonitor;

/**
 * Created by Aniebiet Akpan
 */

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
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

import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class InterUserAccess extends AppCompatActivity {

    private int distanceCovered = 0;
    private int caloriesBurnt = 0;

    private int userWeight = 70; //70kg as default weight
    private int userHeight = 170; //170cm as default height in cm

    private FrameLayout frmBack;
    private FrameLayout frmSearch;
    private FrameLayout frmBodyTemp;
    private FrameLayout frmHeartRate;
    private FrameLayout frmDailySteps;
    private FrameLayout frmDistanceCovered;
    private FrameLayout frmCalories;
    private TextView txtBodyTemp;
    private TextView txtHeartRate;
    private TextView txtStepsToday;
    private TextView txtDistance;
    private TextView txtCalories;

    private EditText edtSearch;
    private Button btnDiscoverability;
    private Button btnSearch;
    private String deviceID = "";
    private String requestedId = "";
    private int discoverable = 0;
    private int socialDistancingEnable = 0;
    private SQLiteDatabase sqLiteDatabase;
    private String requestForPermissionRequestsAPI =  MainActivity.domainName + "api/permissions/";
    private String getFitnessDataAPI = MainActivity.domainName + "api/fitnessupdate/getupdate/";
    private int asyncTaskTask = 0; //to share the AsyncTask thread for requesting permission and getting update from API


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inter_user_access);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        //check device discoverability
        sqLiteDatabase = this.openOrCreateDatabase("FitnessMonitorDB", MODE_PRIVATE, null);
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM tblDeviceID", null);
        int idIndex = cursor.getColumnIndex("deviceID");
        int discoverableIndex = cursor.getColumnIndex("discoverable");
        int socialDistancingEnableIndex = cursor.getColumnIndex("socialDistancingEnable");
        boolean cursorResponse = cursor.moveToFirst();

        try {
            if (cursorResponse) {
                //get the device id and device discoverability
                deviceID = cursor.getString(idIndex);
                discoverable = cursor.getInt(discoverableIndex);
                socialDistancingEnable = cursor.getInt(socialDistancingEnableIndex);
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

        txtBodyTemp = (TextView) findViewById(R.id.txtBodyTemp);
        txtHeartRate = (TextView) findViewById(R.id.txtHeartRate);
        txtStepsToday = (TextView) findViewById(R.id.txtStepsToday);
        txtDistance = (TextView) findViewById(R.id.txtDistance);
        txtCalories = (TextView) findViewById(R.id.txtCalories);

        frmBodyTemp.setVisibility(View.INVISIBLE);
        frmHeartRate.setVisibility(View.INVISIBLE);
        frmDailySteps.setVisibility(View.INVISIBLE);
        frmDistanceCovered.setVisibility(View.INVISIBLE);
        frmCalories.setVisibility(View.INVISIBLE);

        edtSearch = (EditText) findViewById(R.id.edtSearch);
        btnDiscoverability = (Button) findViewById(R.id.btnDiscoverability);
        btnSearch = (Button) findViewById(R.id.btnSearch);
        //Check initial state of discoverability
        if(discoverable == 1){
            //device discoverable
            btnDiscoverability.setText("DISABLE DISCOVERABILITY");
        } else {
            btnDiscoverability.setText("ENABLE DISCOVERABILITY");
        }



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
        btnSearch.setText("CHECK FITNESS");

        frmBodyTemp.setVisibility(View.INVISIBLE);
        frmHeartRate.setVisibility(View.INVISIBLE);
        frmDailySteps.setVisibility(View.INVISIBLE);
        frmDistanceCovered.setVisibility(View.INVISIBLE);
        frmCalories.setVisibility(View.INVISIBLE);

        requestedId = edtSearch.getText().toString().trim();
        requestedId = requestedId.toUpperCase();
        if(requestedId.contains("USER")){
            //valid user id format
            //request for permission
            requestForPermission(requestedId, deviceID);
            Toast.makeText(getApplicationContext(), "Requesting permission ...", Toast.LENGTH_SHORT).show();

        } else {
            Toast.makeText(getApplicationContext(), "Invalid User ID", Toast.LENGTH_SHORT).show();
        }
    }

    /*Shared Asynctask thread for requesting permission and getting update, determined using var : asyncTaskTask*/
    public class RequestPermission extends AsyncTask<String,Void,String> {

        @Override
        protected String doInBackground(String... urls) {
            if(asyncTaskTask == 0) { //SEND PERMISSION REQUEST TO API
                String result = "";
                URL url;
                HttpURLConnection urlConnection = null;

                try {

                    url = new URL(urls[0]);
                    urlConnection = (HttpURLConnection) url.openConnection();
                    InputStream in = urlConnection.getInputStream();
                    InputStreamReader reader = new InputStreamReader(in);
                    int data = reader.read();

                    while (data != -1) {
                        char current = (char) data;
                        result += current;
                        data = reader.read();
                    }

                    return result;

                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            } else if(asyncTaskTask == 1) { //FETCH FITNESS UPDATE FROM API
                System.out.println("LISTENSEND IS 0");
                String result = "";
                URL url;
                HttpURLConnection urlConnection = null;

                try {

                    url = new URL(urls[0]);
                    urlConnection = (HttpURLConnection) url.openConnection();
                    InputStream in = urlConnection.getInputStream();
                    InputStreamReader reader = new InputStreamReader(in);
                    int data = reader.read();

                    while (data != -1) {
                        char current = (char) data;
                        result += current;
                        data = reader.read();
                    }

                    return result;

                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }
            return null;
        }

        /*Share asyncthread response*/
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(asyncTaskTask == 0) {
                try {
                    Log.i("DATA", s);
                    JSONObject obj = new JSONObject(s);
                    int resp = obj.getInt("permissionStatus");
//                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();

                    if (resp == 2) { //permission granted
                        Toast.makeText(getApplicationContext(), "Permission granted, requesting update ...", Toast.LENGTH_SHORT).show();
                        checkForFitnessUpdate();


                    } else { //permission not granted
                        //messagebox, failed to get permission
                        Toast.makeText(getApplicationContext(), "Failed to get permission", Toast.LENGTH_SHORT).show();
                        ; //do nothing
                    }

                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "Failed to get permission...", Toast.LENGTH_SHORT).show();
                    Log.i("FAILED TO REQUEST PERM.", "Failed to get permission...");
                    e.printStackTrace();
                }
            } else if(asyncTaskTask == 1){
                try {
                    Log.i("DATA", s);
                    JSONObject obj = new JSONObject(s);

//                    Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
                    int bodyTemp = 0, heartRate = 0, numOfSteps = 0;
                    try {

                        bodyTemp = obj.getInt("bodyTemp");
                        heartRate = obj.getInt("heartRate");
                        numOfSteps = obj.getInt("numOfSteps");

                        distanceCovered = (int)(userHeight / 100 * 0.45 * numOfSteps); //in meters
                        caloriesBurnt = (int) (userWeight * distanceCovered * 1.036); //in calories

                        txtBodyTemp.setText(Integer.toString(bodyTemp) + " 'C");
                        txtHeartRate.setText(Integer.toString(heartRate) + " BPM");
                        txtStepsToday.setText(Integer.toString(numOfSteps) + "/5000");
                        txtDistance.setText(Integer.toString(distanceCovered) + " km");
                        txtCalories.setText(Integer.toString(caloriesBurnt) + " kcal");
                        btnSearch.setText("REFRESH");
                        frmBodyTemp.setVisibility(View.VISIBLE);
                        frmHeartRate.setVisibility(View.VISIBLE);
                        frmDailySteps.setVisibility(View.VISIBLE);
                        frmDistanceCovered.setVisibility(View.VISIBLE);
                        frmCalories.setVisibility(View.VISIBLE);


                    } catch(Exception ex){
                        ex.printStackTrace();
                    }


                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "Failed to fetch fitness update...", Toast.LENGTH_SHORT).show();
                    Log.i("FAILED TO FETCH UPDATE.", "Failed to fetch fitness update...");
                    e.printStackTrace();
                }
            }

        }
    }


    public void checkForFitnessUpdate(){
        RequestPermission getFitnessUpdate = new RequestPermission();
        String apiQuery = getFitnessDataAPI + requestedId;

        asyncTaskTask = 1;
        getFitnessUpdate.execute(apiQuery);
    }

    /*Send request for permission to API*/
    private void requestForPermission(String requestedId, String requestingId){
        RequestPermission checkr = new RequestPermission();
        String apiQuery = requestForPermissionRequestsAPI + requestedId + "/" + requestingId;
        System.out.println(apiQuery);
        asyncTaskTask = 0;
        checkr.execute(apiQuery);
    }

    public void btnDiscoverabilityClicked(View view){
        //check discoverabiltiy
        if(discoverable == 0) {
            discoverable = 1; //toggle disvoverability
            MainActivity.deviceDiscoverable = 1; //toggle discoverability in MainActivity
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
            MainActivity.deviceDiscoverable = 0; //toggle discoverability in MainActivity
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
