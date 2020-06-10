package com.example.fitnessmonitor.fitnessmonitor;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Display;
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


import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

import static android.widget.FrameLayout.*;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    Button btnSearch;
    Button btnOnOff;

    BluetoothAdapter mBluetoothAdapter;
//    FrameLayout flList;
//    ListView lstDevices;
//    TextView txtConnected;
//    private ArrayList<String> deviceNames = new ArrayList<>();
//    private ArrayList<String> deviceAddresses = new ArrayList<>();
//    private ArrayList<String> devicesDisplay = new ArrayList<>();
//    public ArrayList<BluetoothDevice> mBTDevices = new ArrayList<>();
//    private ArrayAdapter devicesArrayAdapter;
//    private int deviceTapped = 0;
    Set<BluetoothDevice> pairedDevices; //set returned from checking paired devices
//    private ArrayList<String> alreadyPaired = new ArrayList<String>();
    BluetoothDevice pairedDevice;
//    private String connectedName;
//    private String connectedAddress;
//    private String connectedThreadName;


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
//            Toast.makeText(getApplicationContext(), "Please pair to fitness monitor device", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

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
        //set gridlayout height
        int glHeight = (int)(0.95*height);
        GridLayout gridLayout = (GridLayout) findViewById(R.id.gridLayoutMain);
        ViewGroup.LayoutParams glParams = gridLayout.getLayoutParams();
        glParams.height = glHeight;
        gridLayout.setLayoutParams(glParams);
        //set status cell height
        int statusCellHeight = (int)(0.4*glHeight);
        FrameLayout flStatus = (FrameLayout) findViewById(R.id.frmStatus);
        ViewGroup.LayoutParams flStatusLayoutParams = flStatus.getLayoutParams();
        flStatusLayoutParams.height = statusCellHeight;//(int)(grdMainHeight * 0.9);
        flStatus.setLayoutParams(flStatusLayoutParams);
        //set active cell height
        int activeCellHeight = (int)(glHeight/4.5 - 0.035*glHeight);
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

        if(pairedDevice != null) {
            mBluetoothConnection = new BluetoothConnectionService(MainActivity.this);
            //start connection
            System.out.println("DEVICE CONNECTED, STARTING CONNECTION");
            System.out.println("PAIRED DEVICE IS "+pairedDevice.getName());
            startConnection();
        }

    }

//    @Override
//    public void onBackPressed() {
//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        if (drawer.isDrawerOpen(GravityCompat.START)) {
//            drawer.closeDrawer(GravityCompat.START);
//        } else {
//            super.onBackPressed();
//        }
//    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.main, menu);
//        return true;
//    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

//    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_profile) {
            // Handle the camera action
        } else if (id == R.id.nav_weekly_summary) {

        } else if (id == R.id.nav_connect_device) {
            Intent connecDeviceIntent = new Intent(getApplicationContext(), ConnectDevice.class);
            startActivity(connecDeviceIntent);
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
        Log.d("", "startBTConnection: Initializing RFCOM Bluetooth Connection.");
        System.out.println("startBTConnection: Initializing RFCOM Bluetooth Connection");
        System.out.println("mBluetoothConnection is "+ mBluetoothConnection);
        mBluetoothConnection.startClient(device,uuid);
    }

}
