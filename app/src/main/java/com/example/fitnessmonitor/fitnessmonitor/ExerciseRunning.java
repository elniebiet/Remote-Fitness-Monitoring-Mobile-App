package com.example.fitnessmonitor.fitnessmonitor;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Point;
import android.graphics.Typeface;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.app.AlertDialog;
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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.w3c.dom.Text;

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
    private int stopNumberOfSteps = 0;
    private int currentNumberOfSteps = 0;
    private boolean startedCounting = false;
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


        spnTarget.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                Toast.makeText(getApplicationContext(), "Selected: " + lstTarget.get(i), Toast.LENGTH_SHORT).show();
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
                        txtUnit.setText("cal");
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


        //show map
        FragmentManager fm = getSupportFragmentManager();
        SupportMapFragment supportMapFragment =  SupportMapFragment.newInstance();
        fm.beginTransaction().replace(flMap.getId(), supportMapFragment).commit();
        supportMapFragment.getMapAsync(this);

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
                currentNumberOfSteps = Integer.parseInt(steps);
            } catch(NumberFormatException ex){
                Log.i("EXERCISE RUNNING: ", "invalid number of steps");
            }
            Log.i("EXERCISE RUNNING: ", "NUMBER OF STEPS: " + steps);
        }
    };
    public void goBackHome(View view){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirm exit");
        builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
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

        //add marker
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
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

        startNumberOfSteps = currentNumberOfSteps;
        startedCounting = (startedCounting == true) ? false : true; //toggle startedCounting

        if(startedCounting == true){
            flCounting.setVisibility(View.VISIBLE);
            flCount.setVisibility(View.INVISIBLE);
            btnStart.setText("Stop");
            spnTarget.setEnabled(false);
        } else {
            flCounting.setVisibility(View.INVISIBLE);
            flCount.setVisibility(View.VISIBLE);
            btnStart.setText("Start");
            spnTarget.setEnabled(true);
        }

    }

    private void bolden(TextView tv){
        tv.setTypeface(Typeface.DEFAULT_BOLD);
    }
    private void unbolden(TextView tv){
        tv.setTypeface(Typeface.DEFAULT);
    }
}
