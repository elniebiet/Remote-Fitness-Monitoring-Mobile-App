package com.example.fitnessmonitor.fitnessmonitor;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Point;
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

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;

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
    private Boolean dbcreated = false;
    private TextView txtBodyTemp;
    private TextView txtHeartRate;
    private TextView txtStatus;
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
    private static int currentNumSteps = 0;
    public static int deviceDiscoverable = 0;
    private boolean grantPermission = false;
    public static String domainName  = "http://172.17.0.1:8080/";
    public static String listeningForPermissionRequestsAPI = domainName + "api/permissions/checkrequests/";
    public static String grantPermissionAPIUrl = domainName + "api/permissions";
    private int threadSleepTime = 1000; //time to sleep if there was a request, response time
    private boolean onRequest = false; //currently on a request

    private int asyncTaskTask = 0;
//    public static String listeningForPermissionRequestsAPI = "https://samples.openweathermap.org/data/2.5/weather?q=London,uk&appid=439d4b804bc8187953eb36d2a8c26a02";


    //variables for async thread for checking requests
    private static final String TAG = "MainActivity";
    private volatile boolean stopThread = false;
    android.app.AlertDialog.Builder builder;
    android.app.AlertDialog permissionDialog;


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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityCreated = 1;

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        /*create database*/
        try {
            sqLiteDatabase = this.openOrCreateDatabase("FitnessMonitorDB", MODE_PRIVATE, null);
            sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS tblUserProfile (id INT(1) PRIMARY KEY NOT NULL, email VARCHAR, firstName VARCHAR, lastName VARCHAR, gender VARCHAR, DOB VARCHAR, height INT(3), weight INT(3), picLocation VARCHAR, picType VARCHAR)");
            sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS tblReadings (bodyTemp INT(3), heartRate INT(4), numOfSteps BIGINT(10), timeRecorded TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP)");
            sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS tblDeviceID (deviceID VARCHAR, discoverable INT(1))");
//            this.deleteDatabase("FitnessMonitorDB"); //to drob db
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

            boolean cursorResponse = cursor.moveToFirst();

            if(cursorResponse){
                //get the device id
                deviceID = cursor.getString(idIndex);
                deviceDiscoverable = cursor.getInt(discoverableIndex);
                Log.i("DEVICE ID IS: ", deviceID);

            } else {
                //user device id not created
                //generate device ID
                //get current timestamp
                Long tsLong = System.currentTimeMillis()/1000;
                String userID = "USER"+tsLong.toString();
                try {
                    sqLiteDatabase.execSQL("INSERT INTO tblDeviceID (deviceID, discoverable) VALUES ('"+userID+"', 0)");
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

        EditText txtRecom = (EditText) findViewById(R.id.txtRecom);
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
        int activeCellHeight = (int)(0.13 * height);
        FrameLayout flActive = (FrameLayout) findViewById(R.id.frmActive);
        ViewGroup.LayoutParams flActiveLayoutParams = flActive.getLayoutParams();
        flActiveLayoutParams.height = activeCellHeight;//(int)(grdMainHeight * 0.9);
        flActive.setLayoutParams(flActiveLayoutParams);
        //set exercise cell height
        FrameLayout flExercise = (FrameLayout) findViewById(R.id.frmExercise);
        ViewGroup.LayoutParams flExerciseLayoutParams = flExercise.getLayoutParams();
        flExerciseLayoutParams.height = activeCellHeight;//(int)(grdMainHeight * 0.9);
        flExercise.setLayoutParams(flExerciseLayoutParams);
        //set sleep cell height
        FrameLayout flSleep = (FrameLayout) findViewById(R.id.frmSleep);
        ViewGroup.LayoutParams flSleepLayoutParams = flSleep.getLayoutParams();
        flSleepLayoutParams.height = activeCellHeight;//(int)(grdMainHeight * 0.9);
        flSleep.setLayoutParams(flSleepLayoutParams);

        imgSteps = (ImageView) findViewById(R.id.imgSteps);
        imgSteps.setRotation(0);

        txtSteps = (TextView) findViewById(R.id.txtSteps);
        txtSteps.setText("0 steps");

        //other UI elements
        txtBodyTemp = (TextView)findViewById(R.id.txtBodyTemp);
        txtHeartRate = (TextView)findViewById(R.id.txtHeartRate);
        txtStatus = (TextView)findViewById(R.id.txtStatus);

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

    //    @SuppressWarnings("StatementWithEmptyBody")
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
        } else if (id == R.id.nav_settings) {

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
            if(readings.contains("|")) {

                //while reading, be checking for a newday, if its a new day, send update to fitness device
                getLatestReadings();
                //check if its a new day, reset the number of steps
                Boolean newDay = checkNewDaySetNumSteps();
                if(newDay == true){
                    //send update to fitness device
                    Log.i("SENDING UPDATE: ", "SENDING NEWDAY UPDATE");
                    byte[] bytes = Integer.toString(latestNumSteps).getBytes(Charset.defaultCharset());
                    mBluetoothConnection.write(bytes);
                }

                String[] splitted = splitString(readings);
                String temp = splitted[0];
                String hRate = splitted[1];
                String numSteps = splitted[2];
                String currentTS = getCurrentTimeStamp();
                //update View
                txtBodyTemp.setText(temp + " 'C");
                txtHeartRate.setText(hRate + " BPM");
                txtSteps.setText(Integer.toString(latestNumSteps) + " steps");
                imgSteps.setRotation(latestNumSteps/5000f * 360f);

                try {
                    currentNumSteps = Integer.parseInt(numSteps.trim());
                }catch (Exception ex){
                    Log.i("CURRENTNUMSTEPS MAIN", ex.getMessage());
                }

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
                    if (Integer.parseInt(hRate) > 100 || Integer.parseInt(hRate) < 60)
                        txtHeartRate.setTextColor(Color.RED);
                    else txtHeartRate.setTextColor(Color.GREEN);
                } catch (Exception ex){
                    Log.i("COLOR SETTING: ", "error "+ex.getMessage());
                }
                //write to database
                try {
                    String query = "INSERT INTO tblReadings (bodyTemp, heartRate, numOfSteps, timeRecorded) VALUES (" + temp + ", " + hRate + ", " + numSteps + ", '"+ currentTS + "')";
//                    System.out.println("INSERT QUERY: " + query);
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

    class MonitorRequestsRunnable implements Runnable {
        int seconds;
        MonitorRequestsRunnable() {
            ;
        }
        @Override
        public void run() {
            while(true){
                txtStatus.post(new Runnable() {
                    @Override
                    public void run() {
                        if(deviceDiscoverable == 1) {
                            if(onRequest == true){
                                threadSleepTime = 10000; //give more time to sleep
                                onRequest = false; //reset
                            } else {
                                threadSleepTime = 1000;
                            }
                            listenForRequests();
                        }
                    }
                });

                try {
                    Thread.sleep(threadSleepTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public class MonitorRequests extends AsyncTask<String,Void,String> {

        @Override
        protected String doInBackground(String... urls) {
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

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            try {
                Log.i("DATA", s);
                JSONObject js = new JSONObject(s);

//                Toast.makeText(getApplicationContext(), Integer.toString(js.length()) , Toast.LENGTH_SHORT).show();

                if(!(js.length() == 0)){ //not empty response
                    Toast.makeText(getApplicationContext(), s , Toast.LENGTH_SHORT).show();
                    //TODO check if there is a request, grant or reject permission request
//                    grantPermission(js.getString("0"));
                    onRequest = true;
                    grantPermission(js.getString("0"));

                }




            } catch (Exception e) {
                Log.i("FAILED TO GET", "FAILED TO GET JSON DATA");
                e.printStackTrace();
            }

        }
    }

    public void listenForRequests(){
        MonitorRequests checkr = new MonitorRequests();
        String apiQuery = listeningForPermissionRequestsAPI + deviceID;
        checkr.execute(apiQuery);
    }

    private void grantPermission(final String userid){
        //create alert dialog for user permission
        builder.setTitle("Grant permission to " + userid + "?");
        builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            public void onClick(DialogInterface dialog, int id) {
                System.out.println("Choice was positive");
                //TODO grant permission, send response to grant permission api, send post request
                try {
                    URL url = new URL(grantPermissionAPIUrl);
                    URLConnection con = url.openConnection();
                    HttpURLConnection http = (HttpURLConnection) con;
                    http.setRequestMethod("POST"); // PUT is another valid option
                    http.setDoOutput(true);

                    String jsonStr = "{\"permissionGranted\": " + Integer.toString(2) + "," +
                            "\"deviceRequested\": \"" + deviceID + "\"" + "," +
                            "\"requestingDevice\": \"" + userid + "\"" + "}";
                    System.out.println("JSON STRING IS : " + jsonStr);
                    byte[] out  = jsonStr.getBytes(StandardCharsets.UTF_8);
//                       byte[] out = "{\"username\":\"root\",\"password\":\"password\"}" .getBytes(StandardCharsets.UTF_8);
                    int length = out.length;

                    http.setFixedLengthStreamingMode(length);
                    http.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                    http.connect();

                    try(OutputStream os = http.getOutputStream()) {
                        os.write(out);
                    }catch (Exception ex){
                        ex.printStackTrace();
                    }

                } catch (Exception ex){
                    System.out.print("ERROR GRANTING PERMISSION");
                    ex.printStackTrace();
                }


                dialog.dismiss();
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                System.out.println("Choice was negative");
                //TODO refuse permission, send response to grant permission api, send post request
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
}
