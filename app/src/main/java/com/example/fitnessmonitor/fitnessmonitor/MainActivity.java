package com.example.fitnessmonitor.fitnessmonitor;

/**
 * Created by Aniebiet Akpan
 */

import android.accounts.Account;
import android.accounts.AccountManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.util.Patterns;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.example.fitnessmonitor.fitnessmonitor.views.NearbyHealthCentres;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.acl.Group;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;

import static android.support.v4.content.ContextCompat.startActivity;
import static android.widget.FrameLayout.*;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    Button btnSearch;
    Button btnOnOff;

    BluetoothAdapter mBluetoothAdapter;
    Set<BluetoothDevice> pairedDevices; //set returned from checking paired devices
    BluetoothDevice pairedDevice;
    String readings;
    TextView txtUserEmail;
    public static int backFromActivity = 0;
    private SQLiteDatabase sqLiteDatabase = null;
    private int activityCreated = 0;
    private String userEmail = "";
    private String deviceID = "";
    private int socialDistancingEnable = 0;
    private Boolean dbcreated = false;
    private TextView txtBodyTemp;
    private TextView txtHeartRate;
    private TextView lblHeartRate;
    private TextView txtActiveTime;
    private Menu sidebarMenu = null;
    private MenuItem socialDist = null;
    private Boolean socialDistEnabled = false;
    private ImageView imgStatus;
    private int currBodyTemp = 0;
    private int currHeartRate = 0;
    private int currNumSteps = 0;
    private int latestNumSteps = 0;
    private int latestHeartRate = 0;
    private int latestBodyTemp = 0;
    private String latestTimeStamp = "";
    private String latestHour = "";
    private String latestMinute = "";
    private String latestDay = "1";
    private String todayDay = "";
    private ImageView imgSteps;
    private TextView txtSteps;
    private EditText txtRecom;
    private static int currentNumSteps = 0;
    public static int deviceDiscoverable = 0;
    public static String domainName  = "http://172.17.0.1:8080/"; //domain server ip address
    public static String listeningForPermissionRequestsAPI = domainName + "api/permissions/checkrequests/";
    public static String grantPermissionAPIUrl = domainName + "api/permissions";
    public static String updateFitnessAPIUrl = domainName + "api/fitnessupdate";
    private int threadSleepTime = 1000; //time to sleep if there was a request, response time
    private boolean onRequest = false; //currently on a request
    private String requestingId = "";
    private int grantPerm = 0; //0 to reject permission to a user, 2 to grant permission to a user

    private int asyncTaskTask = 0;
    private int listenSend = 0; //toggle between listening for request and semding data to API each time
//    public static String listeningForPermissionRequestsAPI = "https://samples.openweathermap.org/data/2.5/weather?q=London,uk&appid=439d4b804bc8187953eb36d2a8c26a02";
    private static Context mainActivityContext = null;

    //variables for async thread for checking requests
    private static final String TAG = "MainActivity";
    private volatile boolean stopThread = false;
    android.app.AlertDialog.Builder builder;
    android.app.AlertDialog permissionDialog;

    MediaPlayer mediaPlayer = null;


    BluetoothConnectionService mBluetoothConnection;
    private static final UUID MY_UUID_INSECURE =
            UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    @Override
    public void onStart() {
        super.onStart();
        //check if HC-05 or HC-06 is paired
        pairedDevice = null;
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        pairedDevices = mBluetoothAdapter.getBondedDevices();

        if(pairedDevices.size() != 0) {
            for (BluetoothDevice bt : pairedDevices) {
                if (bt.getName() != null) {
                    if(bt.getName().contains("HC-05") || bt.getName().contains("HC-06")){
                        pairedDevice = bt;
                        break;
                    }
                }
            }
        }

        if(pairedDevice == null){
            AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
            alertDialog.setTitle("Not Connected");
            alertDialog.setMessage("Please connect to fitness monitor");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            Intent connecDeviceIntent = new Intent(getApplicationContext(), ConnectDevice.class);
                            startActivity(connecDeviceIntent);
                        }
                    });
            alertDialog.show();
        }
    }
    @Override
    public void onResume(){
        super.onResume();
        //on restart connection if a new connection is made, by checking if just coming from another activity
        if(pairedDevice != null && backFromActivity == 1) {
            mBluetoothConnection = new BluetoothConnectionService(MainActivity.this);
            //start connection
            System.out.println("DEVICE CONNECTED, STARTING CONNECTION");
            System.out.println("PAIRED DEVICE IS " + pairedDevice.getName());
            startConnection();
            readings = "";
            LocalBroadcastManager.getInstance(this).registerReceiver(rReceiver, new IntentFilter("incomingReadings"));
            if(dbcreated){
                getLatestReadings();
            }
        }
        backFromActivity = 0;
    }
    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        this.moveTaskToBack(true);
    }

//    @Override
//    protected void onPause() {
//        super.onPause();
//        try {
//            mediaPlayer.stop();
//            mediaPlayer.release();
//        } catch (Exception ex){
//            System.out.println("ERROR STOPPING MEDIA");
//        }
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityCreated = 1;

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        mainActivityContext = this.getApplicationContext();
        mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.beep);

        /*create database*/
        try {
            sqLiteDatabase = this.openOrCreateDatabase("FitnessMonitorDB", MODE_PRIVATE, null);
            sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS tblUserProfile (id INT(1) PRIMARY KEY NOT NULL, email VARCHAR, firstName VARCHAR, lastName VARCHAR, gender VARCHAR, DOB VARCHAR, height INT(3), weight INT(3), picLocation VARCHAR, picType VARCHAR)");
            sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS tblReadings (bodyTemp INT(3), heartRate INT(4), numOfSteps BIGINT(10), timeRecorded TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP)");
            sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS tblDeviceID (deviceID VARCHAR, discoverable INT(1), socialDistancingEnable INT(1))");
//            this.deleteDatabase("FitnessMonitorDB"); //uncomment to drob db
            Log.i("SUCCESS CREATING DB: ", "database created");
            dbcreated = true;
        } catch (Exception ex){
            Log.i("ERROR CREATING DB: ", "couldn't create database"+ex.getMessage());
            dbcreated = false;
        }

        //check if device ID is assigned to device else create one, also get discoverability here
        if(dbcreated){
            Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM tblDeviceID", null);
            int idIndex = cursor.getColumnIndex("deviceID");
            int discoverableIndex = cursor.getColumnIndex("discoverable");
            int socialDistancingEnableIndex = cursor.getColumnIndex("socialDistancingEnable");
            boolean cursorResponse = cursor.moveToFirst();

            if(cursorResponse){
                //get the device id
                deviceID = cursor.getString(idIndex);
                deviceDiscoverable = cursor.getInt(discoverableIndex);
                socialDistancingEnable = cursor.getInt(socialDistancingEnableIndex);
                Log.i("DEVICE ID IS: ", deviceID);

            } else {
                //user device id not created
                //generate device ID
                //get current timestamp
                Long tsLong = System.currentTimeMillis()/1000;
                String userID = "USER"+tsLong.toString();
                deviceID = userID;
                try {
                    sqLiteDatabase.execSQL("INSERT INTO tblDeviceID (deviceID, discoverable, socialDistancingEnable) VALUES ('"+userID+"', 0, 0)");
                    Log.i("SUCCESS INSERTING ID", "inserted generated user ID");

                } catch(Exception e){
                    Log.i("ERROR INSERTING ID", "Couldnt insert generated user ID");
                }
            }
        }


        //check for user email if account added
        try {
            Pattern emailPattern = Patterns.EMAIL_ADDRESS; // API level 8+
            Account[] accounts = AccountManager.get(this).getAccounts();
            for (Account account : accounts) {
                if (emailPattern.matcher(account.name).matches()) {
                    userEmail = account.name;
                    Log.i("POSSIBLE EMAIL ADDR: ", userEmail);
                    break;
                }
            }
            if (accounts.length == 0) {
                Log.i("NO EMAIL ADDR: ", "Couldnt find email address");

            }
        } catch (Exception ex){
            Log.i("ERROR GETTING EMAIL: ", "couldn't get email address "+ex.getMessage());
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        sidebarMenu = navigationView.getMenu();
        socialDist = sidebarMenu.findItem(R.id.nav_social_dist);

        //set the socialDistEnabled nav item
        if(socialDistancingEnable == 0){
            socialDist.setTitle("Enable social distancing");
        } else if(socialDistancingEnable == 1){
            socialDist.setTitle("Disable social distancing");
        }
//        mniSocialDist.setTitle("DISABLE SOCI");

        txtRecom = (EditText) findViewById(R.id.txtRecom);
        txtRecom.setKeyListener(null);

        //get current screen dimensions
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;
        //set toolbar height
        int toolbarHeight = (int)(0.05 * height);
        Toolbar tb = (Toolbar) findViewById(R.id.toolbar);
        ViewGroup.LayoutParams tbLayoutParams = tb.getLayoutParams();
        tbLayoutParams.height = toolbarHeight;//(int)(grdMainHeight * 0.9);
        tb.setLayoutParams(tbLayoutParams);
        //set picture frame height
        int frmPictureHeight = (int)(0.2*height);
        FrameLayout frmPicture = (FrameLayout) findViewById(R.id.frmPicture);
        ViewGroup.LayoutParams frmPictureLayoutParams = frmPicture.getLayoutParams();
        frmPictureLayoutParams.height = frmPictureHeight;//(int)(grdMainHeight * 0.9);
        frmPicture.setLayoutParams(frmPictureLayoutParams);
        //set status cell height
        int statusCellHeight = (int)(0.3*height);
        FrameLayout flStatus = (FrameLayout) findViewById(R.id.frmStatus);
        ViewGroup.LayoutParams flStatusLayoutParams = flStatus.getLayoutParams();
        flStatusLayoutParams.height = statusCellHeight;//(int)(grdMainHeight * 0.9);
        flStatus.setLayoutParams(flStatusLayoutParams);
        //set active cell height
        int activeCellHeight = (int)(0.135 * height);
        FrameLayout flActive = (FrameLayout) findViewById(R.id.frmActive);
        ViewGroup.LayoutParams flActiveLayoutParams = flActive.getLayoutParams();
        flActiveLayoutParams.height = (int)(activeCellHeight);//(int)(grdMainHeight * 0.9);
        flActive.setLayoutParams(flActiveLayoutParams);
        //set exercise cell height
        FrameLayout flExercise = (FrameLayout) findViewById(R.id.frmExercise);
        ViewGroup.LayoutParams flExerciseLayoutParams = flExercise.getLayoutParams();
        flExerciseLayoutParams.height = (int)(activeCellHeight);//(int)(grdMainHeight * 0.9);
        flExercise.setLayoutParams(flExerciseLayoutParams);
        //set sleep cell height
        FrameLayout flSleep = (FrameLayout) findViewById(R.id.frmSleep);
        ViewGroup.LayoutParams flSleepLayoutParams = flSleep.getLayoutParams();
        flSleepLayoutParams.height = (int)(0.99 * activeCellHeight);//(int)(grdMainHeight * 0.9);
        flSleep.setLayoutParams(flSleepLayoutParams);

        imgSteps = (ImageView) findViewById(R.id.imgSteps);
        imgSteps.setRotation(0);

        txtSteps = (TextView) findViewById(R.id.txtSteps);
        txtSteps.setText("0 steps");

        //other UI elements
        txtBodyTemp = (TextView)findViewById(R.id.txtBodyTemp);
        txtHeartRate = (TextView)findViewById(R.id.txtHeartRate);
        lblHeartRate = (TextView)findViewById(R.id.lblHeartRate);
//        txtStatus = (TextView)findViewById(R.id.txtStatus);
        imgStatus = (ImageView) findViewById(R.id.imgStatus);
        txtActiveTime = (TextView) findViewById(R.id.txtActiveTime);

        //create builder
        builder = new android.app.AlertDialog.Builder(this);
        permissionDialog = builder.create(); //initialise empty dialog

        //start listening thread, to listen to API for any request
        startListenerThreadForRemoteAccess();


        View header = navigationView.getHeaderView(0);
        txtUserEmail = (TextView) header.findViewById(R.id.txtUserEmail);

        //set email on drawer
        if(userEmail.length() == 0){
            txtUserEmail.setText("Add email account to device." + "\n(User ID: " + deviceID + ")");
            txtUserEmail.setTextColor(Color.GREEN);
            txtUserEmail.setTextSize(14);
        } else {
            txtUserEmail.setText(userEmail + "\n(User ID: " + deviceID + ")");
            txtUserEmail.setTextColor(Color.DKGRAY);
            txtUserEmail.setTextSize(14);
        }
        //check if HC-05 or HC-06 is paired
        pairedDevice = null;
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        pairedDevices = mBluetoothAdapter.getBondedDevices();

        if(pairedDevices.size() != 0) {
            for (BluetoothDevice bt : pairedDevices) {
                if (bt.getName() != null) {
                    if(bt.getName().contains("HC-05") || bt.getName().contains("HC-06")){
                        pairedDevice = bt;
                        break;
                    }
                }
            }
        }

        //if paired, start connection
        if(pairedDevice != null) {
            mBluetoothConnection = new BluetoothConnectionService(MainActivity.this);
            //start connection
            System.out.println("DEVICE CONNECTED, STARTING CONNECTION");
            System.out.println("PAIRED DEVICE IS " + pairedDevice.getName());
            startConnection();
            readings = "";
            LocalBroadcastManager.getInstance(this).registerReceiver(rReceiver, new IntentFilter("incomingReadings"));
        }

        //Check is its a new day, set number of steps to 0, else set number of steps to the latest
        if(dbcreated) {
            getLatestReadings();
            //check if its a new day, reset the number of steps
            checkNewDaySetNumSteps();
            txtSteps.setText(Integer.toString(latestNumSteps) + " steps");
            imgSteps.setRotation(latestNumSteps/5000f * 360f);
        }

    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_profile) {
            Intent userProfileIntent = new Intent(getApplicationContext(), UserProfile.class);
            startActivity(userProfileIntent);
        } else if (id == R.id.nav_weekly_summary) {

        } else if (id == R.id.nav_connect_device) {
            Intent connecDeviceIntent = new Intent(getApplicationContext(), ConnectDevice.class);
            startActivity(connecDeviceIntent);
        } else if (id == R.id.nav_nearby) {
            Intent nearbyCentresIntent = new Intent(getApplicationContext(), NearbyHealthCentres.class);
            startActivity(nearbyCentresIntent);
        } else if(id == R.id.nav_remote_access) {
            Intent remoteAccessIntent = new Intent(getApplicationContext(), InterUserAccess.class);
            startActivity(remoteAccessIntent);
        } else if (id == R.id.nav_social_dist) {
            if(socialDist.getTitle().equals("Enable social distancing")){
                socialDist.setTitle("Disable social distancing");
                //update database
                try {
                    String query = "UPDATE tblDeviceID SET socialDistancingEnable = 1 WHERE deviceID = '" + deviceID + "';";
                    sqLiteDatabase.execSQL(query);
                    Log.i("SUCCESS UPDATING SD", "set socialDistancingEnable to 1");

                } catch(Exception e){
                    Log.i("ERROR UPDATING SD", "Couldnt Update socialDistancing");
                    e.printStackTrace();
                }

                //send update to arduino
                Log.i("SENDING UPDATE: ", "SENDING SOCIAL DISTANCING UPDATE");
                byte[] bytes = Integer.toString(-2).getBytes(Charset.defaultCharset()); //-2 For enable, -1 for disable
                mBluetoothConnection.write(bytes);

            } else if(socialDist.getTitle().equals("Disable social distancing")){
                socialDist.setTitle("Enable social distancing");
                //update database
                try {
                    String query = "UPDATE tblDeviceID SET socialDistancingEnable = 0 WHERE deviceID = '" + deviceID + "';";
                    sqLiteDatabase.execSQL(query);
                    Log.i("SUCCESS UPDATING SD", "set socialDistancingEnable to 0");

                } catch(Exception e){
                    Log.i("ERROR UPDATING SD", "Couldnt Update socialDistancing");
                    e.printStackTrace();
                }
                //send update to arduino
                Log.i("SENDING UPDATE: ", "SENDING SOCIAL DISTANCING UPDATE");
                byte[] bytes = Integer.toString(-1).getBytes(Charset.defaultCharset()); //-2 For enable, -1 for disable
                mBluetoothConnection.write(bytes);
            }
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void openStatusActivity(View view){
        FrameLayout fl = (FrameLayout)findViewById(R.id.frmStatus);
        Intent statusIntent = new Intent(getApplicationContext(), StatActivity.class);
        startActivity(statusIntent);
    }

    public void goToWalking(View view){
        ImageView img = (ImageView) findViewById(R.id.imgWalk);
        Intent walkingIntent = new Intent(getApplicationContext(), ExerciseWalking.class);
        startActivity(walkingIntent);
    }

    public void goToRunning(View view){
        ImageView img = (ImageView) findViewById(R.id.imgRun);
        Intent runningIntent = new Intent(getApplicationContext(), ExerciseRunning.class);
        startActivity(runningIntent);
    }

    public void goToRecentWorkOuts(View view){
        TextView txtview = (TextView) findViewById(R.id.lblRecent);
        Intent RecentWorkOutsIntent = new Intent(getApplicationContext(), RecentWorkOuts.class);
        startActivity(RecentWorkOutsIntent);
    }

    public void goToSleep(View view){
        FrameLayout flSleep = (FrameLayout) findViewById(R.id.frmSleep);
        Intent SleepIntent = new Intent(getApplicationContext(), SleepActivity.class);
        startActivity(SleepIntent);
    }

    /*
     * create method for starting connection
     * conncction will fail and app will crash if you haven't paired first
     */
    public void startConnection(){
        startBTConnection(pairedDevice,MY_UUID_INSECURE);
    }

    /*
     * starting chat service method
     */
    public void startBTConnection(BluetoothDevice device, UUID uuid){
        //this method is observed to start before the Oncreate finishes loading
        //so we check for update readings here as well.
        Log.d("", "startBTConnection: Initializing RFCOM Bluetooth Connection.");
        System.out.println("startBTConnection: Initializing RFCOM Bluetooth Connection");
        System.out.println("mBluetoothConnection is "+ mBluetoothConnection);

        //on start of new BT connection
        //get latestReadings and send new update
        getLatestReadings();
        //check for new day
        Boolean newDay = checkNewDaySetNumSteps();
        latestNumSteps = (newDay) ? 0 : latestNumSteps;

        String updateString = Integer.toString(latestNumSteps);

        mBluetoothConnection.startClient(device, uuid, updateString);

    }

    BroadcastReceiver rReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String readings = intent.getStringExtra("theReadings");
            System.out.println("FROM ACTIVITY: READINGS: "+ readings);
            //get readings from activity, confirm it is the right reading
            //check if -1 is in the received text, object detection notification else normal readings received
            if(readings.contains("-1")){
                Toast.makeText(getApplicationContext(), "object detected", Toast.LENGTH_SHORT).show();
                try {
                    mediaPlayer.start();
                } catch (Exception ex){
                    System.out.println("Error playing media: " + ex.getMessage());
                }
                return;
                //buzz sound and vibrate
            } else if(readings.contains("|") && (readings.contains("-1") == false)) {

                //while reading, be checking for a newday, if its a new day, send update to fitness device
                getLatestReadings();
                //check if its a new day, reset the number of steps
                Boolean newDay = checkNewDaySetNumSteps();
                if(newDay == true){
                    //send update to fitness device
                    Log.i("SENDING UPDATE: ", "SENDING NEWDAY UPDATE");
                    byte[] bytes = Integer.toString(latestNumSteps).getBytes(Charset.defaultCharset());
                    mBluetoothConnection.write(bytes);

                    if(socialDistancingEnable == 1) {
                        //send update to arduino
                        Log.i("SENDING UPDATE: ", "SENDING SOCIAL DISTANCING UPDATE");
                        byte[] byts = Integer.toString(-2).getBytes(Charset.defaultCharset()); //-2 For enable, -1 for disable
                        mBluetoothConnection.write(byts);
                    } else if(socialDistancingEnable == 0){
                        //send update to arduino
                        Log.i("SENDING UPDATE: ", "SENDING SOCIAL DISTANCING UPDATE");
                        byte[] byts = Integer.toString(-1).getBytes(Charset.defaultCharset()); //-2 For enable, -1 for disable
                        mBluetoothConnection.write(byts);
                    }
                }

                String[] splitted = splitString(readings);
                String temp = splitted[0];
                String hRate = splitted[1];
                String numSteps = splitted[2];

                /*start bug fix: numSteps has lengthy string on start error, take numSteps and discard the rest*/
                if(numSteps.contains("|")){
                    int indexOfPipe = numSteps.indexOf("|");
                    numSteps = numSteps.substring(0, indexOfPipe);
                }
                //replace non-digits
                temp = temp.replaceAll("\\D+","");
                hRate = hRate.replaceAll("\\D+","");
                numSteps = numSteps.replaceAll("\\D+","");
                System.out.println(" TEMP: " + temp + " HRATE: " + hRate + " NUMSTEPS: " + numSteps);
                /* end bug fix */

                String currentTS = getCurrentTimeStamp();


                //update View
                txtBodyTemp.setText(temp + " 'C");
                //If heart rate is > 100, set text to 100+
                if(Integer.parseInt(hRate) < 55){
                    txtHeartRate.setText("-55 BPM");
                } else if(Integer.parseInt(hRate) < 100) {
                    txtHeartRate.setText(hRate + " BPM");
                }else {
                    txtHeartRate.setText("100+ BPM");
                }
                txtSteps.setText(Integer.toString(latestNumSteps) + " steps");
                imgSteps.setRotation(latestNumSteps/5000f * 360f);

                System.out.println("NUMBER OF STEPS BEFORE: " + numSteps);
                currentNumSteps = Integer.parseInt(numSteps);
                System.out.println(Integer.parseInt(numSteps));
                System.out.println("NUMBER OF STEPS AFTER: " + numSteps);


                //create intent to send number of steps as broadcast to Exercise Running
                Intent intSendReadings = new Intent("currentReadings");
                intSendReadings.putExtra("mainActReadings", numSteps);
                Log.i("MAIN SENT MESSAGE: ", numSteps);
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intSendReadings);


                //set color
                try {
                    if (Integer.parseInt(temp) < 34 || Integer.parseInt(temp) > 38)
                        txtBodyTemp.setTextColor(Color.RED);
                    else txtBodyTemp.setTextColor(Color.GREEN);
                    if (Integer.parseInt(hRate) > 100 || Integer.parseInt(hRate) < 55)
                        txtHeartRate.setTextColor(Color.RED);
                    else txtHeartRate.setTextColor(Color.GREEN);
                } catch (Exception ex){
                    Log.i("COLOR SETTING: ", "error "+ex.getMessage());
                }
                //set current variables
                currBodyTemp = Integer.parseInt(temp);
                currHeartRate = Integer.parseInt(hRate);
                currNumSteps = Integer.parseInt(numSteps);

                //set recommendation
                currNumSteps = Integer.parseInt(numSteps);
                if( currNumSteps< 1000){
                    imgStatus.setImageResource(R.drawable.starrr1);
                    txtRecom.setText("Fitness level is low today: More exercise needed to reach your daily limit");
                } else if(currNumSteps >= 1000 && currNumSteps < 2000){
                    imgStatus.setImageResource(R.drawable.starrr2);
                    txtRecom.setText("Fitness level is low today: More exercise needed to reach your daily limit");
                } else if(currNumSteps >= 2000 && currNumSteps < 3000){
                    imgStatus.setImageResource(R.drawable.starrr3);
                    txtRecom.setText("Fitness level is avarage today: More exercise needed to reach your daily limit");
                } else if(currNumSteps >= 3000 && currNumSteps < 4000){
                    imgStatus.setImageResource(R.drawable.starrr4);
                    txtRecom.setText("Fitness level is high today: you can still reach your daily limit");
                } else if(currNumSteps >= 4000){
                    imgStatus.setImageResource(R.drawable.starrr5);
                    txtRecom.setText("Fitness level is high today: You have reached your daily limit");
                }

                //set active time

                String activeTime = Integer.toString((int)((double)currNumSteps/24.0));
                txtActiveTime.setText(activeTime + "/60 mins");

                //write to database
                try {
                    String query = "INSERT INTO tblReadings (bodyTemp, heartRate, numOfSteps, timeRecorded) VALUES (" + temp + ", " + hRate + ", " + numSteps + ", '"+ currentTS + "')";
                    System.out.println("INSERT QUERY: " + query);
                    sqLiteDatabase.execSQL(query);
                    Log.i("SUCCESSFUL INSERT: ","readings inserted to db" );
                } catch(Exception ex){
                    Log.i("FAILED INSERT: ","failed to insert readings to db: " + ex.getLocalizedMessage());
                }
            }

        }
    };

    private String[] splitString(String str){
        int firstPipe = str.indexOf("|"); //check for index of first pipe
        StringBuilder tmp = new StringBuilder(str);
        tmp.setCharAt(firstPipe, ':'); //replace first pipe with : to get index of second pipe
        str = tmp.toString();
        int secondPipe = str.indexOf("|");
        str = str.replace(':', '|'); //replace back the :
        String[] parts = {"","",""};
        parts[0] = str.substring(0,firstPipe);
        parts[1] = str.substring(firstPipe+1, secondPipe);
        parts[2] = str.substring(secondPipe+1, str.length());

        return parts;
    }

    private void getLatestReadings(){
        try {
            Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM tblReadings ORDER BY timeRecorded DESC LIMIT 1", null);
            int dateIndex = cursor.getColumnIndex("timeRecorded");
            int numStepsIndex = cursor.getColumnIndex("numOfSteps");
            int heartRateIndex = cursor.getColumnIndex("heartRate");
            int bodyTempIndex = cursor.getColumnIndex("bodyTemp");
            boolean cursorResponse = cursor.moveToFirst();

            if (cursorResponse) {
                //get latest timestamp and number of steps
                latestTimeStamp = cursor.getString(dateIndex);
                latestNumSteps = cursor.getInt(numStepsIndex);
                latestHeartRate = cursor.getInt(heartRateIndex);
                latestBodyTemp = cursor.getInt(bodyTempIndex);

                String datetimeParts[] = latestTimeStamp.split(":");
                String dateHourParts[] = datetimeParts[0].split("-");
                String hourParts[] = dateHourParts[2].split(" ");
                latestDay = hourParts[0];
                latestHour = hourParts[1];
                latestMinute = datetimeParts[1];

                System.out.println("LATEST READINGS: " + cursor.getString(dateIndex) + " " + latestNumSteps + " " + latestBodyTemp + " " + latestHeartRate + " hour: " + hourParts[1] + " Minute: "+ latestMinute);



            } else {
                latestNumSteps = 0;
                latestBodyTemp = 0;
                latestHeartRate = 0;
                System.out.println("LATEST READINGS: " + latestNumSteps + " " + latestBodyTemp + " " + latestHeartRate);
            }
        } catch (Exception ex){
            Log.i("ERR GETNG LATST READGS ", ex.getMessage());
        }
    }

    public static String getCurrentTimeStamp(){
        try {

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String currentDateTime = dateFormat.format(new Date()); // Find todays date

            return currentDateTime;
        } catch (Exception e) {
            e.printStackTrace();

            return null;
        }
    }

    public Date stringToTimeStamp(String strTime){
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date d = null;
        try {
            //convert string to date
            d = inputFormat.parse(strTime);
        } catch (Exception ex) {
            System.out.println("Date Format Not Supported");
            ex.printStackTrace();
        }
        return d;
    }

    public boolean checkNewDaySetNumSteps(){

        //get current time
        String currentTimeStamp = getCurrentTimeStamp();
        String datetimeParts[] = currentTimeStamp.split(":");
        String dateHourParts[] = datetimeParts[0].split("-");
        String hourParts[] = dateHourParts[2].split(" ");
        todayDay = hourParts[0];
        System.out.println("TODAY: " + todayDay + " LATEST READNG: "+ latestDay);
        if((Integer.parseInt(todayDay) - Integer.parseInt(latestDay)) > 0){
            //its a new day
            latestNumSteps = 0;
            return true;
        } else {
            ; //latest number of steps already set
            return false;
        }
    }

    public static int getCurrentNumSteps(){
        return currentNumSteps;
    }

    //start listener thread
    private void startListenerThreadForRemoteAccess() {
        stopThread = false;
        MonitorRequestsRunnable runnable = new MonitorRequestsRunnable();
        new Thread(runnable).start();
    }
    //stop listener thread
    public void stopThread() {
        stopThread = true;
    }

    //Runnable class to listen for monitor requests
    class MonitorRequestsRunnable implements Runnable {
        MonitorRequestsRunnable() {
            ;
        }
        @Override
        public void run() {
            while(true){
                lblHeartRate.post(new Runnable() {
                    @Override
                    public void run() {
                        if(deviceDiscoverable == 1) { //only listen for requests if the device is discoverable
                            if(onRequest == true){
                                threadSleepTime = 10000; //give more time to sleep, 10secs if a request has been sent
                                onRequest = false; //reset
                            } else {
                                threadSleepTime = 1000; //otherwise continue checking every second
                            }
                            if(listenSend == 0) {
                                listenForRequests();
                            }
                            if(listenSend == 1) {
                                sendFitnessUpdate();
                            }
                        }
                    }
                });

                try {
                    Thread.sleep(threadSleepTime); //default sleep time is 1 sec.
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /* Main class to listen for monitor requests,
    * This thread is shared: when asyncTaskTask == 0, listen for request, otherwise i.e when 1, respond to permission request
    * */
    public class MonitorRequests extends AsyncTask<String,Void,String> {

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected String doInBackground(String... urls) { //shared doInBackground function

            if(asyncTaskTask == 0){
                if(listenSend == 0) { //LISTEN FOR ANY AVAILABLE REQUESTS
                    System.out.println("LISTENSEND IS 0");
                    listenSend = 1;
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
                } else if(listenSend == 1){ //SEND UPDATE TO FITNESS API

                    System.out.println("LISTENSEND IS 1");

                    listenSend = 0;
                    try {

                        URL url = new URL(updateFitnessAPIUrl);
                        System.out.println("UPDATE URL IS + "+ updateFitnessAPIUrl);
                        String jsonStr = "{\"userId\": \"" + deviceID + "\"," +
                                "\"bodyTemp\": " + currBodyTemp + "," +
                                "\"heartRate\": " + currHeartRate + "," +
                                "\"numberOfSteps\": " + currNumSteps + "}";

                        System.out.println("JSON STRING IS : " + jsonStr);
                        byte[] postDataBytes  = jsonStr.getBytes(StandardCharsets.UTF_8);
                        HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                        conn.setRequestMethod("POST");
                        conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                        conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
                        conn.setDoOutput(true);
                        conn.getOutputStream().write(postDataBytes);
                        Reader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                        StringBuilder sb = new StringBuilder();
                        for (int c; (c = in.read()) >= 0;)
                            sb.append((char)c);
                        String response = sb.toString();
                        System.out.println("RESPONSE IS : "+ response);

                    } catch (Exception ex){
                        System.out.print("ERROR UPDATING DATA");
                        ex.printStackTrace();
                    }
                }
            } else { //RESPOND TO REQUEST
                try {

                    URL url = new URL(grantPermissionAPIUrl);
                    String jsonStr = "{\"permissionGranted\": " + Integer.toString(grantPerm) + "," +
                            "\"deviceRequested\": \"" + deviceID + "\"" + "," +
                            "\"requestingDevice\": \"" + requestingId + "\"" + "}";
                    System.out.println("JSON STRING IS : " + jsonStr);
                    byte[] postDataBytes  = jsonStr.getBytes(StandardCharsets.UTF_8);
                    HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                    conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
                    conn.setDoOutput(true);
                    conn.getOutputStream().write(postDataBytes);
                    Reader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                    StringBuilder sb = new StringBuilder();
                    for (int c; (c = in.read()) >= 0;)
                        sb.append((char)c);
                    String response = sb.toString();
                    System.out.println("RESPONSE IS : "+ response);

                } catch (Exception ex){
                    System.out.print("ERROR GRANTING PERMISSION");
                    ex.printStackTrace();
                }
                return null;

            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) { //shared function
            super.onPostExecute(s);
            if(asyncTaskTask == 0) {
                if(listenSend == 1) { //after executing listen request, listenSend status is 1
                    if(s != null) {
                        try {
                            Log.i("DATA IS ", s);
                            JSONObject js = new JSONObject(s);

                            if (!(js.length() == 0)) { //not empty response
//                                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
                                onRequest = true;
                                grantPermission(js.getString("0"));

                            }


                        } catch (Exception e) {
                            Log.i("FAILED TO GET", "FAILED TO GET JSON DATA");
                            e.printStackTrace();
                        }
                    }
                }
            } else {
                ; //do nothing, sending a post request
            }

        }
    }

    public void listenForRequests(){
        MonitorRequests checkr = new MonitorRequests();
        String apiQuery = listeningForPermissionRequestsAPI + deviceID;

        asyncTaskTask = 0;
        checkr.execute(apiQuery);
    }

    public void sendFitnessUpdate(){
        MonitorRequests checkr = new MonitorRequests();
        asyncTaskTask = 0;
        checkr.execute();
    }
    private void grantPermission(final String userid){
        //create alert dialog for user permission
        builder.setTitle("Grant permission to " + userid + "?");
        builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            public void onClick(DialogInterface dialog, int id) {
                System.out.println("Choice was positive");
                grantPerm = 2;
                requestingId = userid;
                MonitorRequests checkr = new MonitorRequests();
                asyncTaskTask = 1;
                checkr.execute();

                dialog.dismiss();
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                System.out.println("Choice was negative");
                grantPerm = 0;
                requestingId = userid;
                MonitorRequests checkr = new MonitorRequests();
                asyncTaskTask = 1;
                checkr.execute();
                dialog.dismiss();
            }
        });
        if(permissionDialog.isShowing()){
            ;
        } else {
            permissionDialog = builder.create();
            permissionDialog.show();
        }
    }

    public static boolean isNumeric(final String str) {

        // null or empty
        if (str == null || str.length() == 0) {
            return false;
        }

        for (char c : str.toCharArray()) {
            if (!Character.isDigit(c)) {
                return false;
            }
        }

        return true;

    }

    public static Context getMainContext(){
        return mainActivityContext;
    }

}
