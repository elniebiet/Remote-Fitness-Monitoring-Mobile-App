package com.example.fitnessmonitor.fitnessmonitor;

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
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
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
    ArrayList<String> bluetoothDevices = new ArrayList<>();
    ArrayList<String> addresses = new ArrayList<>();
    ArrayAdapter arrayAdapter;

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

    @Override
    protected void onDestroy() {
        Log.d("Msg", "onDestroy: called.");
        super.onDestroy();
        unregisterReceiver(mBroadcastReceiver1);
    }

//    static final UUID mUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
//    ListView lstDevices;
//    TextView statusTextView;
//    Button btnSearch;
//    ArrayList<String> bluetoothDevices = new ArrayList<>();
//    ArrayList<String> addresses = new ArrayList<>();
//    ArrayAdapter arrayAdapter;
//
//    BluetoothAdapter bluetoothAdapter;

//    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            String action = intent.getAction();
//            Log.i("Action",action);
//
//            if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
//                Log.i("Msg","finished");
//                btnSearch.setEnabled(true);
//            } else if (BluetoothDevice.ACTION_FOUND.equals(action)) {
//                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
//                String name = device.getName();
//                String address = device.getAddress();
//                String rssi = Integer.toString(intent.getShortExtra(BluetoothDevice.EXTRA_RSSI,Short.MIN_VALUE));
//                Log.i("Device Found","Name: " + name + " Address: " + address + " RSSI: " + rssi);
//
//                if (!addresses.contains(address)) {
//                    addresses.add(address);
//                    String deviceString = "";
//                    if (name == null || name.equals("")) {
//                        deviceString = address + " - RSSI " + rssi + "dBm";
//                    } else {
//                        deviceString = name + " - RSSI " + rssi + "dBm";
//                    }
//
//                    bluetoothDevices.add(deviceString);
//                    arrayAdapter.notifyDataSetChanged();
//                }
//            }
//        }
//    };

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
        //get default adapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

//        lstDevices = findViewById(R.id.lstDevices);
////        statusTextView = findViewById(R.id.statusTextView);
//        btnSearch = findViewById(R.id.btnSearch);
//
//        arrayAdapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,bluetoothDevices);
//
//        lstDevices.setAdapter(arrayAdapter);
//
//        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
//
//        IntentFilter intentFilter = new IntentFilter();
//        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
//        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
//        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
//        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
//        registerReceiver(broadcastReceiver, intentFilter);

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
        btnSearch.setText("Searching...");
        btnSearch.setClickable(false);

//        bluetoothDevices.clear();
//        addresses.clear();
//        bluetoothAdapter.startDiscovery();
    }

    public void onOffBT(View view){
        enableDisableBT();
    }

    public void enableDisableBT(){
        if(mBluetoothAdapter == null){
            Log.d("Msg", "enableDisableBT: Does not have BT capabilities.");
        }
        else if(!mBluetoothAdapter.isEnabled()){
            Log.d("Msg", "enableDisableBT: enabling BT.");
            mBluetoothAdapter.enable();
//            startActivity(enableBTIntent);
            IntentFilter BTIntent = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
            registerReceiver(mBroadcastReceiver1, BTIntent);
        }
        else if(mBluetoothAdapter.isEnabled()){
            Log.d("Msg", "enableDisableBT: disabling BT.");
            mBluetoothAdapter.disable();

            IntentFilter BTIntent = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
            registerReceiver(mBroadcastReceiver1, BTIntent);
        }

    }

}
