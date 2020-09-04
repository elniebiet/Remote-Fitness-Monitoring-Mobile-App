package com.example.fitnessmonitor.fitnessmonitor;

/**
 * Created by Aniebiet Akpan on 22/06/20.
 */

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.BluetoothSocket;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.DrawableRes;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class ConnectDevice extends AppCompatActivity {
    Button btnSearch;
    Button btnOnOff;

    BluetoothAdapter mBluetoothAdapter;
    FrameLayout flList;
    ListView lstDevices;
    TextView txtConnected;
    private ArrayList<String> deviceNames = new ArrayList<>();
    private ArrayList<String> deviceAddresses = new ArrayList<>();
    private ArrayList<String> devicesDisplay = new ArrayList<>();
    public ArrayList<BluetoothDevice> mBTDevices = new ArrayList<>();
    private ArrayAdapter devicesArrayAdapter;
    private int deviceTapped = 0;
    Set<BluetoothDevice> pairedDevices; //set returned from checking paired devices
    private ArrayList<String> alreadyPaired = new ArrayList<String>();
    private String connectedName;
    private String connectedAddress;
    private String connectedThreadName;

    /*Create a BroadcastReceiver for ACTION_FOUND*/
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
                //check if the device is not already in the list
                if(deviceName != null) {
                    if (!(deviceAddresses.contains(deviceAddress)) && (deviceName.contains("HC-05") || deviceName.contains("HC-06"))) {
                        deviceAddresses.add(deviceAddress);
                        deviceNames.add(deviceName);
                        mBTDevices.add(device);
                        if (deviceName != null) {
                            devicesDisplay.add(deviceName + " - " + deviceAddress);
                        } else {
                            devicesDisplay.add(deviceAddress);
                        }
                    }
                }

                lstDevices.setAdapter(devicesArrayAdapter);
            }
            if (action.equals(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)){
                //check for already paired HC-05 OR HC-06 devices and add to list
                for(BluetoothDevice bt: pairedDevices){
                    if(bt.getName() != null) {
                        if (!(deviceNames.contains("HC-05") || deviceNames.contains("HC-06")) && (bt.getName().contains("HC-05") || bt.getName().contains("HC-06"))) {
                            deviceAddresses.add(bt.getAddress());
                            deviceNames.add(bt.getName());
                            devicesDisplay.add(bt.getName() + " - " + bt.getAddress());
                            mBTDevices.add(bt);
                            lstDevices.setAdapter(devicesArrayAdapter);
                        }
                    }
                }

                btnSearch.setClickable(true);
                btnSearch.setText("Search");

                if(deviceTapped != 1) {
                    Toast.makeText(getApplicationContext(), "Search complete. (" + devicesDisplay.size() + ") devices found.", Toast.LENGTH_SHORT).show();
                    deviceTapped = 0;
                }
            }
        }
    };

    /**
     * Broadcast Receiver that detects bond state changes (Pairing status changes)
     */
    private final BroadcastReceiver mBroadcastReceiver4 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            Log.i("BOND ACTION", action);
            if(action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)){
                BluetoothDevice mDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                //3 cases:
                //case1: bonded already
                if (mDevice.getBondState() == BluetoothDevice.BOND_BONDED){
                    Log.d("", "BroadcastReceiver: BOND_BONDED.");
                    Toast.makeText(getApplicationContext(), "Pairing Complete", Toast.LENGTH_LONG).show();
                    MainActivity.backFromActivity = 1;
                    String connectedTo = (mDevice.getName() != null) ? mDevice.getName(): ("ID - "+ mDevice.getAddress());
                    txtConnected.setText("Connected to Device: " + connectedTo);
                    lstDevices.setVisibility(View.INVISIBLE);
                    flList.setBackgroundResource(R.color.colorWhite);
                    txtConnected.setVisibility(View.VISIBLE);

                    deviceAddresses.clear();
                    deviceNames.clear();
                    devicesDisplay.clear();

                }
                //case2: creating a bone
                if (mDevice.getBondState() == BluetoothDevice.BOND_BONDING) {
                    Log.d("", "BroadcastReceiver: BOND_BONDING.");
                    Toast.makeText(getApplicationContext(), "Pairing", Toast.LENGTH_SHORT).show();
                }
                //case3: breaking a bond
                if (mDevice.getBondState() == BluetoothDevice.BOND_NONE) {
                    System.out.println("CHECKING ALREADY PAIRED: "+ mDevice.getAddress());
                    if(alreadyPaired.contains(mDevice.getAddress())){
                        pairDevice(deviceAddresses.indexOf(mDevice.getAddress()));
                    } else {
                        Toast.makeText(getApplicationContext(), "Pairing Failed, Please check that the fitness device turned on and try again.", Toast.LENGTH_LONG).show();
                    }
                    Log.d("", "BroadcastReceiver: BOND_NONE.");

                }
            }
        }
    };

    @Override
    protected void onDestroy() {
        Log.d("Msg", "onDestroy: called.");
        super.onDestroy();
        try {
            unregisterReceiver(mBroadcastReceiver1);
        } catch (Exception e) {
            Log.i("", "Receiver unregistered.");
        }
        try{
            unregisterReceiver(mBroadcastReceiver3);
        } catch(Exception e){
            Log.i("", "Receiver unregistered.");
        }
        try{
            unregisterReceiver(mBroadcastReceiver4);
        } catch(Exception e){
            Log.i("", "Receiver unregistered.");
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect_device);
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
        flList= (FrameLayout) findViewById(R.id.flList);
        ViewGroup.LayoutParams flListLayoutParams = flList.getLayoutParams();
        flListLayoutParams.height = flListHeight;//(int)(grdMainHeight * 0.9);
        setMargins(flList, 0,0,0,0);
        flList.setLayoutParams(flListLayoutParams);

        btnSearch = (Button)findViewById(R.id.btnSearch);
        btnOnOff = (Button)findViewById(R.id.btnTurnOnOff);
        lstDevices = (ListView) findViewById(R.id.lstDevices);
        txtConnected = (TextView) findViewById(R.id.txtConnected);
        deviceTapped = 0;
        connectedAddress = "";

        lstDevices.setVisibility(View.VISIBLE);
        flList.setBackgroundResource(R.color.colorAsh);

        txtConnected.setVisibility(View.INVISIBLE);

        //get default adapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        //check connected devices
        checkConnected();

        //create array adapter
        devicesArrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, devicesDisplay);

        //get already paired devices
        pairedDevices = mBluetoothAdapter.getBondedDevices();
        for(BluetoothDevice bt : pairedDevices)
            alreadyPaired.add(bt.getAddress());


        //Broadcasts when bond state changes (ie:pairing)
        try {
            IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
            registerReceiver(mBroadcastReceiver4, filter);
            Log.i("SUCCESS", "RECEIVER 4 REGISTERED");
        } catch (Exception e){
            Log.i("Error Registerg Recvr4", e.getMessage());
        }
        //on click of BT device, do pairing
        lstDevices.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //check if device is already paired
                deviceTapped = 1;
                if (deviceNames.get(i) != null) {
                    Toast.makeText(getApplicationContext(), "Pairing to " + devicesDisplay.get(i), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Pairing to " + deviceAddresses.get(i), Toast.LENGTH_SHORT).show();
                }
                unpairDevice(mBTDevices.get(i)); //unpair if already paired
                pairDevice(i); //pair
            }
        });

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
        lstDevices.setVisibility(View.VISIBLE);
        txtConnected.setVisibility(View.INVISIBLE);
        flList.setBackgroundResource(R.color.colorAsh);

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
            devicesDisplay.clear();
            deviceAddresses.clear();
            deviceNames.clear();
        }
        else if(mBluetoothAdapter.isEnabled()){
            Log.d("Msg", "enableDisableBT: disabling BT.");
            mBluetoothAdapter.disable();

            IntentFilter BTIntent = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
            registerReceiver(mBroadcastReceiver1, BTIntent);
            Toast.makeText(getApplicationContext(), "Bluetooth turned off.", Toast.LENGTH_SHORT).show();
            devicesDisplay.clear();
            deviceAddresses.clear();
            deviceNames.clear();

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
        try {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
                int permissionCheck = this.checkSelfPermission("Manifest.permission.ACCESS_FINE_LOCATION");
                permissionCheck += this.checkSelfPermission("Manifest.permission.ACCESS_COARSE_LOCATION");
                if (permissionCheck != 0) {
                    this.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1001); //Any number
                }
            } else {
                Log.d("", "checkBTPermissions: No need to check permissions. SDK version < LOLLIPOP.");
            }
        } catch (Exception e){
            Log.i("", "ERROR GRANTING PERMISSION");
        }
    }

    private void pairDevice(int deviceIndex){
        //first cancel discovery because its very memory intensive.
        mBluetoothAdapter.cancelDiscovery();

        //create the bond.
        //NOTE: Requires API 17+? I think this is JellyBean
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2){
            mBTDevices.get(deviceIndex).createBond();
        }
    }

    private void unpairDevice(BluetoothDevice device) {
        try {
            Method m = device.getClass()
                    .getMethod("removeBond", (Class[]) null);
            m.invoke(device, (Object[]) null);
        } catch (Exception e) {
            Log.e("", e.getMessage());
        }
    }

    /*
    **check for connected devices, i.e if bluetooth is on and device is paired
    * else search for devices. NB: normally app wouldnt get here if not connected to device
    * */
    public void checkConnected()
    {
        //check if bluetooth is on and device is paired
//        if(!(mBluetoothAdapter == null)){ //first check if device has BT
//            if(mBluetoothAdapter.isEnabled()){
//                for(BluetoothDevice bt: pairedDevices){
//                    if(bt.getName() != null) {
//                        if (bt.getName().contains("HC-05") || bt.getName().contains("HC-06")) {
//                            String connectedTo = (bt.getName() != null) ? bt.getName() : ("ID - "+ bt.getAddress());
//                            txtConnected.setText("Connected to Device: " + connectedTo);
//                            lstDevices.setVisibility(View.INVISIBLE);
//                            flList.setBackgroundResource(R.color.colorWhite);
//                            txtConnected.setVisibility(View.VISIBLE);
//                            break;
//                        }
//                    }
//                }
//            }
//        }
        BluetoothAdapter.getDefaultAdapter().getProfileProxy(this, serviceListener, BluetoothProfile.HEADSET);
    }

    private BluetoothProfile.ServiceListener serviceListener = new BluetoothProfile.ServiceListener()
    {
        @Override
        public void onServiceDisconnected(int profile)
        {

        }

        @Override
        public void onServiceConnected(int profile, BluetoothProfile proxy)
        {
            BluetoothDevice device1 = null;
            for (BluetoothDevice device : proxy.getConnectedDevices())
            {
                device1 = device;
                connectedName = device.getName();
                connectedAddress = device.getAddress();
                connectedThreadName = Thread.currentThread().getName();
                Log.i("onServiceConnected", "|" + connectedName + " | " + connectedAddress + " | " + connectedThreadName + "(connected = "
                        + BluetoothProfile.STATE_CONNECTED + ")");

            }
            //if there is a connected device, display
            if(connectedAddress != ""){
                if(connectedName.contains("HC-05") || connectedName.contains("HC-06")){
                    String connectedTo = (connectedName != null) ? connectedName : ("ID - "+ connectedAddress);
                    txtConnected.setText("Connected to Device: " + connectedTo);
                    lstDevices.setVisibility(View.INVISIBLE);
                    flList.setBackgroundResource(R.color.colorWhite);
                    txtConnected.setVisibility(View.VISIBLE);
                } else {;
                    unpairDevice(device1);
                    lstDevices.setVisibility(View.VISIBLE);
                    txtConnected.setVisibility(View.INVISIBLE);
                    flList.setBackgroundResource(R.color.colorAsh);
                }

            } else {
                lstDevices.setVisibility(View.VISIBLE);
                txtConnected.setVisibility(View.INVISIBLE);
                flList.setBackgroundResource(R.color.colorAsh);
            }

            BluetoothAdapter.getDefaultAdapter().closeProfileProxy(profile, proxy);
        }

//        private void resetConnection() {
//            if (mBTInputStream != null) {
//                try {mBTInputStream.close();} catch (Exception e) {}
//                mBTInputStream = null;
//            }
//
//            if (mBTOutputStream != null) {
//                try {mBTOutputStream.close();} catch (Exception e) {}
//                mBTOutputStream = null;
//            }
//
//            if (mBTSocket != null) {
//                try {mBTSocket.close();} catch (Exception e) {}
//                mBTSocket = null;
//            }
//
//        }
    };

}
