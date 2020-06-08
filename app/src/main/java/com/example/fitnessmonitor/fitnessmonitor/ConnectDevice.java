package com.example.fitnessmonitor.fitnessmonitor;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Point;
import android.os.Build;
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
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class ConnectDevice extends AppCompatActivity {
    Button btnSearch;
    Button btnOnOff;

    BluetoothAdapter mBluetoothAdapter;

    ListView lstDevices;
    ArrayList<String> deviceNames = new ArrayList<>();
    ArrayList<String> deviceAddresses = new ArrayList<>();
    ArrayList<String> devicesDisplay = new ArrayList<>();
    ArrayAdapter devicesArrayAdapter;

    // Create a BroadcastReceiver for ACTION_FOUND
    private final BroadcastReceiver mBroadcastReceiver1 = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // When discovery finds a device
            if (action.equals(mBluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, mBluetoothAdapter.ERROR);

                switch(state){
                    case BluetoothAdapter.STATE_OFF:
                        Log.d("Msg", "onReceive: STATE OFF");
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        Log.d("Msg", "mBroadcastReceiver1: STATE TURNING OFF");
                        break;
                    case BluetoothAdapter.STATE_ON:
                        Log.d("Msg", "mBroadcastReceiver1: STATE ON");
                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        Log.d("Msg", "mBroadcastReceiver1: STATE TURNING ON");
                        break;
                }
            }
        }
    };

    /**
     * Broadcast Receiver for listing devices that are not yet paired
     * -Executed by btnDiscover() method.
     */
    private BroadcastReceiver mBroadcastReceiver3 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            Log.d("Msg", "onReceive: ACTION FOUND."+ action);
            if (action.equals(BluetoothDevice.ACTION_FOUND)){
                BluetoothDevice device = intent.getParcelableExtra (BluetoothDevice.EXTRA_DEVICE);
                //add device to list
                String deviceName = device.getName();
                String deviceAddress = device.getAddress();
                Log.d("Msg", "onReceive: " + deviceName + ": " + deviceAddress);
                if(!(deviceAddresses.contains(deviceAddress))){
                    deviceAddresses.add(deviceAddress);
                    deviceNames.add(deviceName);
                    if(deviceName != null){
                        devicesDisplay.add(deviceName+" - " + deviceAddress);
                    } else {
                        devicesDisplay.add(deviceAddress);
                    }
                }

                lstDevices.setAdapter(devicesArrayAdapter);

                lstDevices.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                        Toast.makeText(getApplicationContext(), devicesDisplay.get(i), Toast.LENGTH_SHORT).show();

                    }
                });


                System.out.println(deviceAddresses);
            }
            if (action.equals(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)){
                btnSearch.setClickable(true);
                btnSearch.setText("Search");
                Toast.makeText(getApplicationContext(), "Search complete. ("+devicesDisplay.size()+ ") devices found.", Toast.LENGTH_SHORT).show();

            }
        }
    };

    @Override
    protected void onDestroy() {
        Log.d("Msg", "onDestroy: called.");
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect_device);

        //get current screen dimensions
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;
        System.out.println("width is "+width);
        //set flBack height
        int flBackHeight = (int)(0.05 * height);
        FrameLayout flBack = (FrameLayout) findViewById(R.id.flBack);
        ViewGroup.LayoutParams flBackLayoutParams = flBack.getLayoutParams();
        flBackLayoutParams.height = flBackHeight;//(int)(grdMainHeight * 0.9);
        setMargins(flBack, 0,10,0,0);
        flBack.setLayoutParams(flBackLayoutParams);
        //set flMessage height
        int flMessageHeight = (int)(0.15 * height);
        FrameLayout flMessage = (FrameLayout) findViewById(R.id.flMessage);
        ViewGroup.LayoutParams flMessageLayoutParams = flMessage.getLayoutParams();
        flMessageLayoutParams.height = flMessageHeight;//(int)(grdMainHeight * 0.9);
        setMargins(flMessage, 0,0,0,0);
        flMessage.setLayoutParams(flMessageLayoutParams);
        //set flList height
        int flListHeight = (int)(0.65 * height);
        FrameLayout flList= (FrameLayout) findViewById(R.id.flList);
        ViewGroup.LayoutParams flListLayoutParams = flList.getLayoutParams();
        flListLayoutParams.height = flListHeight;//(int)(grdMainHeight * 0.9);
        setMargins(flList, 0,0,0,0);
        flList.setLayoutParams(flListLayoutParams);

        btnSearch = (Button)findViewById(R.id.btnSearch);
        btnOnOff = (Button)findViewById(R.id.btnTurnOnOff);
        lstDevices = findViewById(R.id.lstDevices);
        //create array adapter
        devicesArrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, devicesDisplay);
        //get default adapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

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

    public void searchDevices(View view){
        //clear the list
        deviceNames.clear();
        deviceAddresses.clear();
        devicesDisplay.clear();
        //check if bluetooth is on, then list devices
        if(!(mBluetoothAdapter == null)){ //first check if device has BT
            if(mBluetoothAdapter.isEnabled()){
                btnSearch.setText("Searching...");
                btnSearch.setClickable(false);
                discoverDevices();
            }else {
                Toast.makeText(getApplicationContext(), "Please Turn on Bluetooth", Toast.LENGTH_SHORT).show();
            }
        }

    }

    public void onOffBT(View view){
        enableDisableBT();
    }


    /*enable or disable BT*/
    public void enableDisableBT(){
        if(mBluetoothAdapter == null){
            Log.d("Msg", "enableDisableBT: Does not have BT capabilities.");
            Toast.makeText(getApplicationContext(), "Device Does not have Bluetooth capabilities.", Toast.LENGTH_SHORT).show();
        }
        else if(!mBluetoothAdapter.isEnabled()){
            Log.d("Msg", "enableDisableBT: enabling BT.");
            mBluetoothAdapter.enable();
            IntentFilter BTIntent = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
            registerReceiver(mBroadcastReceiver1, BTIntent);
            Toast.makeText(getApplicationContext(), "Bluetooth turned on.", Toast.LENGTH_SHORT).show();
        }
        else if(mBluetoothAdapter.isEnabled()){
            Log.d("Msg", "enableDisableBT: disabling BT.");
            mBluetoothAdapter.disable();

            IntentFilter BTIntent = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
            registerReceiver(mBroadcastReceiver1, BTIntent);
            Toast.makeText(getApplicationContext(), "Bluetooth turned off.", Toast.LENGTH_SHORT).show();

        }

    }

    /*Discover devices*/
    public void discoverDevices() {
        Log.d("", "btnDiscover: Looking for unpaired devices.");
            //check BT permissions in manifest
            checkBTPermissions();

            mBluetoothAdapter.startDiscovery();
            IntentFilter discoverDevicesIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            discoverDevicesIntent.addAction(BluetoothDevice.ACTION_FOUND);
            discoverDevicesIntent.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
            discoverDevicesIntent.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
            registerReceiver(mBroadcastReceiver3, discoverDevicesIntent);
    }

    /**
     * This method is required for all devices running API23+
     * Android must programmatically check the permissions for bluetooth. Putting the proper permissions
     * in the manifest is not enough.
     *
     * NOTE: This will only execute on versions > LOLLIPOP because it is not needed otherwise.
     */
    private void checkBTPermissions() {
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP){
            int permissionCheck = this.checkSelfPermission("Manifest.permission.ACCESS_FINE_LOCATION");
            permissionCheck += this.checkSelfPermission("Manifest.permission.ACCESS_COARSE_LOCATION");
            if (permissionCheck != 0) {
                this.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1001); //Any number
            }
        }else{
            Log.d("", "checkBTPermissions: No need to check permissions. SDK version < LOLLIPOP.");
        }
    }

}
