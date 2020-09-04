package com.example.fitnessmonitor.fitnessmonitor;

/**
 * Created by Aniebiet Akpan on 22/06/20.
 */

import android.Manifest;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Point;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.app.AlertDialog;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.w3c.dom.Text;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

//public class ExerciseRunning extends AppCompatActivity {
public class ExerciseRunning extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    private Spinner spnTarget;
    private ArrayAdapter<String> targetAdapter;
    private List<String> lstTarget;
    private TextView txtUnit;
    private TextView txtCount;
    private Button btnPlus;
    private Button btnMinus;
    private int selectedTargetIndex;
    private int countTarget = 0;
    private int startNumberOfSteps = 0;
    private int currentNumberOfSteps = 0;
    private int currentDistanceCovered = 0;
    private int currentCaloriesBurnt = 0;
    private int currentMinutes = 0;
    private long currentSecs = 0;
    private int currentDuration = 0;
    private int secsInMin = 0;
    private boolean startedCounting = false;
    private Timestamp startedCountingTime;
    FrameLayout flCounting;
    FrameLayout flCount;
    private TextView lblDuration;
    private TextView lblDistanceCovered;
    private TextView lblPaces;
    private TextView lblCalories;
    private TextView txtDuration;
    private TextView txtDistanceCovered;
    private TextView txtPaces;
    private TextView txtCalories;
    private Button btnStart;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private int userWeight = 70; //70kg as default weight
    private int userHeight = 170; //170cm as default height in cm
    private SQLiteDatabase sqLiteDatabase = null;
    private int profileDetailsSupplied;
    private CountDownTimer runningTime;
    private String myLocationLong = "";
    private String myLocationLat = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_running);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
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
        flCount = (FrameLayout) findViewById(R.id.flCount);
        ViewGroup.LayoutParams flCountLayoutParams = flCount.getLayoutParams();
        flCountLayoutParams.height = flCountHeight;//(int)(grdMainHeight * 0.9);
        setMargins(flCount, 0,flCountMarginTop,0,0);
        flCount.setLayoutParams(flCountLayoutParams);

        //set flCounting height
        int flCountingHeight = (int)(0.08 * height);
        int flCountingWidth  = (int)(0.9 * width);
        int flCountingMarginTop = 0;
        flCounting = (FrameLayout) findViewById(R.id.flCounting);
        ViewGroup.LayoutParams flCountingLayoutParams = flCounting.getLayoutParams();
        flCountingLayoutParams.height = flCountingHeight;//(int)(grdMainHeight * 0.9);
        flCountingLayoutParams.width = flCountingWidth;
        setMargins(flCounting, 0,flCountingMarginTop,0,0);
        flCounting.setLayoutParams(flCountingLayoutParams);

        //set flDuration width
        int flDurationHeight = flCountingHeight;
        int flDurationWidth = flCountingWidth/4;
        FrameLayout flDuration = (FrameLayout) findViewById(R.id.flDuration);
        ViewGroup.LayoutParams flDurationLayoutParams = flDuration.getLayoutParams();
        flDurationLayoutParams.height = flDurationHeight;//(int)(grdMainHeight * 0.9);
        flDurationLayoutParams.width = flDurationWidth;
        setMargins(flDuration, 0,0,0,0);
        flDuration.setLayoutParams(flDurationLayoutParams);

        //set flDistance width
        int flDistanceHeight = flCountingHeight;
        int flDistanceWidth = flCountingWidth/4;
        FrameLayout flDistance = (FrameLayout) findViewById(R.id.flDistance);
        ViewGroup.LayoutParams flDistanceLayoutParams = flDistance.getLayoutParams();
        flDistanceLayoutParams.height = flDistanceHeight;//(int)(grdMainHeight * 0.9);
        flDistanceLayoutParams.width = flDistanceWidth;
        setMargins(flDistance, 0,0,0,0);
        flDistance.setLayoutParams(flDistanceLayoutParams);

        //set flPaces width
        int flPacesHeight = flCountingHeight;
        int flPacesWidth = flCountingWidth/4;
        FrameLayout flPaces = (FrameLayout) findViewById(R.id.flPaces);
        ViewGroup.LayoutParams flPacesLayoutParams = flPaces.getLayoutParams();
        flPacesLayoutParams.height = flPacesHeight;//(int)(grdMainHeight * 0.9);
        flPacesLayoutParams.width = flPacesWidth;
        setMargins(flPaces, 0,0,0,0);
        flPaces.setLayoutParams(flPacesLayoutParams);

        //set flCalories width
        int flCaloriesHeight = flCountingHeight;
        int flCaloriesWidth = flCountingWidth/4;
        FrameLayout flCalories = (FrameLayout) findViewById(R.id.flCalories);
        ViewGroup.LayoutParams flCaloriesLayoutParams = flCalories.getLayoutParams();
        flCaloriesLayoutParams.height = flCaloriesHeight;//(int)(grdMainHeight * 0.9);
        flCaloriesLayoutParams.width = flCaloriesWidth;
        setMargins(flCalories, 0,0,0,0);
        flCalories.setLayoutParams(flCaloriesLayoutParams);


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

        //display flCount, hide flCounting
        flCount.setVisibility(View.VISIBLE);
        flCounting.setVisibility(View.INVISIBLE);

        lblDistanceCovered = (TextView) findViewById(R.id.lblDistanceCovered);
        lblDuration = (TextView) findViewById(R.id.lblDuration);
        lblPaces = (TextView) findViewById(R.id.lblPaces);
        lblCalories = (TextView) findViewById(R.id.lblCalories);
        txtDistanceCovered = (TextView) findViewById(R.id.txtDistanceCovered);
        txtDuration = (TextView) findViewById(R.id.txtDuration);
        txtPaces = (TextView) findViewById(R.id.txtPaces);
        txtCalories = (TextView) findViewById(R.id.txtCalories);
        btnStart = (Button) findViewById(R.id.btnStart);

        spnTarget = (Spinner) findViewById(R.id.spnTarget);
        txtUnit = (TextView) findViewById(R.id.txtUnit);
        txtCount = (TextView) findViewById(R.id.txtCount);

        lstTarget = new ArrayList<String>();
        lstTarget.add("Distance");
        lstTarget.add("Paces");
        lstTarget.add("Duration");
        lstTarget.add("Calories burnt");

        targetAdapter = new ArrayAdapter<String>(getApplicationContext(),
                android.R.layout.simple_spinner_item, lstTarget);
        targetAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnTarget.setAdapter(targetAdapter);


        //Target Spinner listener
        spnTarget.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Log.i("SELECTED TARGET: ", lstTarget.get(i));
                selectedTargetIndex = i;
                switch (selectedTargetIndex) {
                    case 0:
                        txtUnit.setText("meters");
                        txtCount.setText("500");
                        bolden(lblDistanceCovered);
                        bolden(txtDistanceCovered);
                        unbolden(lblDuration);
                        unbolden(lblPaces);
                        unbolden(lblCalories);
                        unbolden(txtDuration);
                        unbolden(txtPaces);
                        unbolden(txtCalories);
                        break;
                    case 1:
                        txtUnit.setText("paces");
                        txtCount.setText("1000");
                        bolden(lblPaces);
                        bolden(txtPaces);
                        unbolden(lblDuration);
                        unbolden(lblDistanceCovered);
                        unbolden(lblCalories);
                        unbolden(txtDuration);
                        unbolden(txtDistanceCovered);
                        unbolden(txtCalories);

                        break;
                    case 2:
                        txtUnit.setText("minutes");
                        txtCount.setText("30");
                        bolden(lblDuration);
                        bolden(txtDuration);
                        unbolden(lblDistanceCovered);
                        unbolden(lblPaces);
                        unbolden(lblCalories);
                        unbolden(txtDistanceCovered);
                        unbolden(txtPaces);
                        unbolden(txtCalories);
                        break;
                    case 3:
                        txtUnit.setText("kcal");
                        txtCount.setText("100");
                        bolden(lblCalories);
                        bolden(txtCalories);
                        unbolden(lblDuration);
                        unbolden(lblPaces);
                        unbolden(lblDistanceCovered);
                        unbolden(txtDuration);
                        unbolden(txtPaces);
                        unbolden(txtDistanceCovered);
                        break;
                    default:
                        break;
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //get weight and height if supplied
        try {
            sqLiteDatabase = this.openOrCreateDatabase("FitnessMonitorDB", MODE_PRIVATE, null);
        } catch (Exception ex){
            Log.i("EXRUN: ERROR OPENG DB: ", "couldn't open database"+ex.getMessage());
        }

        //check if profile details have been supplied
        if(sqLiteDatabase != null){
            try {
                Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM tblUserProfile", null);
                int heightIndex = cursor.getColumnIndex("height");
                int weightIndex = cursor.getColumnIndex("weight");

                boolean cursorResponse = cursor.moveToFirst();

                if (cursorResponse) { //profile details supplied
                    profileDetailsSupplied = 1;
                    //get the user details
                    userHeight = cursor.getInt(heightIndex);
                    userWeight = cursor.getInt(weightIndex);
                    Log.i("EX RUNNING", "profile details supplied");


                } else {
                    profileDetailsSupplied = 0;
                    Log.i("EX RUNNING", "profile details not supplied");
                    //user profile details not specified
                }
            } catch (Exception ex){
                Log.i("ERROR FETCHN USER DET", ex.getMessage());
            }
        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                }
            }
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        try {
            LocalBroadcastManager.getInstance(this).registerReceiver(stepsReceiver, new IntentFilter("currentReadings"));
        } catch (Exception ex){
            Log.i("EXERCISE RUNNING", "error getting number of steps");
        }
    }

    BroadcastReceiver stepsReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String steps = intent.getStringExtra("mainActReadings");
            try {
                currentNumberOfSteps = Integer.parseInt(steps.trim()) - startNumberOfSteps;
                currentDistanceCovered = (int)(userHeight / 100 * 0.45 * currentNumberOfSteps); //in meters
                currentCaloriesBurnt = (int) (userWeight * currentDistanceCovered * 1.036); //in calories

                txtPaces.setText(Integer.toString(currentNumberOfSteps));
                txtDistanceCovered.setText(Integer.toString(currentDistanceCovered) + " m");
                txtCalories.setText(String.format("%.2f kcal", currentCaloriesBurnt/1000.0f)); //output in kcal


                //check what parameter was selected, end if target met
                switch (selectedTargetIndex) {
                    case 0:
                        if((currentNumberOfSteps >= countTarget) && countTarget != 0){
                            endRunning();
                        }
                        break;
                    case 1:
                        if((currentDistanceCovered >= countTarget) && countTarget != 0){
                            endRunning();
                        }
                        break;
                    case 2:
                        if((currentDuration >= countTarget) && countTarget != 0){
                            endRunning();
                        }
                        break;
                    case 3:
                        if((currentCaloriesBurnt >= countTarget) && countTarget != 0){
                            endRunning();
                        }
                        break;
                    default:
                        break;
                }


            } catch(Exception ex){
                Log.i("EXERCISE RUNNING: ", "invalid number of steps; "+ ex.getLocalizedMessage());
            }
            Log.i("EXERCISE RUNNING: ", "NUMBER OF STEPS: " + currentNumberOfSteps);
        }
    };
    public void goBackHome(View view){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirm exit");
        builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
                locationManager.removeUpdates(locationListener);
                finish();
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();

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

        //create location manager
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        //create location listener
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                mMap.clear();
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                myLocationLong = Double.toString(location.getLongitude());
                myLocationLat = Double.toString(location.getLatitude());
                System.out.println("MYLOCATION: LONG " + myLocationLong + " LAT " + myLocationLat);
                LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());                Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                mMap.addMarker(new MarkerOptions().position(userLocation).title("Your Location").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 17));
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };

        //ignore if version < 23, just request location updates
        if (Build.VERSION.SDK_INT < 23) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 12000, 0, locationListener);
        } else {
            //request permission if not granted
            if (ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
            } else {
                //get current location
                try {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 12000, 0, locationListener);
                    Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                    mMap.clear();
                    mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

                    myLocationLong = Double.toString(lastKnownLocation.getLongitude());
                    myLocationLat = Double.toString(lastKnownLocation.getLatitude());
                    System.out.println("MYLOCATION: LONG " + myLocationLong + " LAT " + myLocationLat);

                    LatLng userLocation = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
                    mMap.addMarker(new MarkerOptions().position(userLocation).title("Your Location").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 17));
                } catch(Exception ex){
                    Log.i("ERROR GETTING LOCATN: ", ex.getMessage());
                }
            }
        }
    }

    public void btnPlusClicked(View view){
        btnPlus = (Button) findViewById(R.id.btnPlus);

        switch (selectedTargetIndex) {
            case 0:
                int currentCountDistance = Integer.parseInt(txtCount.getText().toString());
                if(currentCountDistance >= 10000) {
                    currentCountDistance = 10000;
                    countTarget = 10000;
                }
                else {
                    countTarget = currentCountDistance + 500;
                    txtCount.setText(Integer.toString(countTarget));
                }
                break;
            case 1:
                int currentCountPace = Integer.parseInt(txtCount.getText().toString());
                if(currentCountPace >= 50000) {
                    currentCountPace = 50000;
                    countTarget = 50000;
                }
                else {
                    countTarget = currentCountPace + 1000;
                    txtCount.setText(Integer.toString(countTarget));
                }
                break;
            case 2:
                int currentCountDuration = Integer.parseInt(txtCount.getText().toString());
                if(currentCountDuration >= 300) {
                    currentCountPace = 300;
                    countTarget = 300;
                }
                else {
                    countTarget = currentCountDuration + 10;
                    txtCount.setText(Integer.toString(countTarget));
                }
                break;
            case 3:
                int currentCountCalories= Integer.parseInt(txtCount.getText().toString());
                if(currentCountCalories >= 1000) {
                    currentCountPace = 1000;
                    countTarget = 1000;
                }
                else {
                    countTarget = currentCountCalories + 50;
                    txtCount.setText(Integer.toString(countTarget));
                }
                break;
            default:
                break;
        }
    }

    public void btnMinusClicked(View view){
        btnMinus = (Button) findViewById(R.id.btnMinus);

        switch (selectedTargetIndex) {
            case 0:
                int currentCountDistance = Integer.parseInt(txtCount.getText().toString());
                if(currentCountDistance <= 500) {
                    currentCountDistance = 500;
                    countTarget = 500;
                }
                else {
                    countTarget = currentCountDistance - 500;
                    txtCount.setText(Integer.toString(countTarget));
                }
                break;
            case 1:
                int currentCountPace = Integer.parseInt(txtCount.getText().toString());
                if(currentCountPace <= 1000) {
                    currentCountPace = 1000;
                    countTarget = 1000;
                }
                else {
                    countTarget = currentCountPace - 1000;
                    txtCount.setText(Integer.toString(countTarget));
                }
                break;
            case 2:
                int currentCountDuration = Integer.parseInt(txtCount.getText().toString());
                if(currentCountDuration <= 30) {
                    currentCountPace = 30;
                    countTarget = 30;
                }
                else {
                    countTarget = currentCountDuration - 10;
                    txtCount.setText(Integer.toString(countTarget));
                }
                break;
            case 3:
                int currentCountCalories= Integer.parseInt(txtCount.getText().toString());
                if(currentCountCalories <= 100) {
                    currentCountPace = 100;
                    countTarget = 100;
                }
                else {
                    countTarget = currentCountCalories - 50;
                    txtCount.setText(Integer.toString(countTarget));
                }
                break;
            default:
                break;
        }

    }

    public void startCounting(View view){

        startNumberOfSteps = MainActivity.getCurrentNumSteps();
        Log.i("START NUM STEPS : " , Integer.toString(startNumberOfSteps));
        startedCounting = (startedCounting == true) ? false : true; //toggle startedCounting

        if(startedCounting == true){
            flCounting.setVisibility(View.VISIBLE);
            flCount.setVisibility(View.INVISIBLE);
            btnStart.setText("Stop");
            spnTarget.setEnabled(false);

            currentSecs = 0;
            final long futureTime = 86400000; //num secs in a day
            //create countdown timer
            runningTime = new CountDownTimer(86400000, 1000) { //86400000 = secs a day

                public void onTick(long millisUntilFinished) {
                    currentSecs = (futureTime -  millisUntilFinished) / 1000;
                    currentMinutes = (int) (currentSecs / 60);
                    currentDuration = currentMinutes;
                    secsInMin = (int) (currentSecs % 60);
                    txtDuration.setText(Integer.toString(currentMinutes) + ":" + Long.toString(secsInMin));
                    //here you can have your logic to set text to edittext
                }

                public void onFinish() {
                    txtDuration.setText("done!");
                }

            }.start();

        } else {
            endRunning();
        }

    }

    private void bolden(TextView tv){
        tv.setTypeface(Typeface.DEFAULT_BOLD);
    }
    private void unbolden(TextView tv){
        tv.setTypeface(Typeface.DEFAULT);
    }
    private void endRunning(){

        //alert when workout is completed
        AlertDialog.Builder builder = new AlertDialog.Builder(this); // setup the alert builder
        builder.setTitle("Workout Summary");
        String sum = "Steps count: " + currentNumberOfSteps + "\n";
        sum += "Distance covered: " + currentDistanceCovered + " meters \n";
        sum += "Calories burnt: " + currentCaloriesBurnt + " kcal \n";
        sum += "Time spent: " + currentDuration + " mins " + secsInMin + " secs \n";
        builder.setMessage(sum);
        builder.setPositiveButton("OK", null); // add a button
        AlertDialog dialog = builder.create();
        dialog.show();

//        Toast.makeText(getApplicationContext(), "Workout saved, see recent workouts for summary.", Toast.LENGTH_LONG).show();
        flCounting.setVisibility(View.INVISIBLE);
        flCount.setVisibility(View.VISIBLE);
        btnStart.setText("Start");
        spnTarget.setEnabled(true);

        currentNumberOfSteps = 0;
        currentDistanceCovered = 0;
        currentCaloriesBurnt = 0;
        currentSecs = 0;
        currentMinutes = 0;
        countTarget = 0;

        txtPaces.setText("0");
        txtDistanceCovered.setText(Integer.toString(currentDistanceCovered) + "0 m");
        txtCalories.setText("0 kcal"); //output in kcal
        txtDuration.setText("0:0");
        runningTime.cancel();
    }
}
