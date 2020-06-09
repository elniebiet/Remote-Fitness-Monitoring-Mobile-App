package com.example.fitnessmonitor.fitnessmonitor;

/**
 * Created by Aniebiet Akpan on 09/06/20.
 */

//bluetooth connectio
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public class GetReadings {

    static final UUID mUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    Set<BluetoothDevice> pairedDevices;
    private ArrayList<String> alreadyPaired = new ArrayList<String>();
    private String connectionAddress = "";

    public void readHC05(){
        BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
        pairedDevices = btAdapter.getBondedDevices();
        if(pairedDevices.size() != 0){
            for(BluetoothDevice bt : pairedDevices) {
                if(bt.getName().contains("HC-05") || bt.getName().contains("HC-06")){
                    connectionAddress = bt.getAddress();
                }

            }
            BluetoothDevice hc05 = btAdapter.getRemoteDevice(connectionAddress);
            System.out.println(hc05.getName());

            BluetoothSocket btSocket = null;
            int counter = 0;
            try {
                do {
                    try {
                        btSocket = hc05.createRfcommSocketToServiceRecord(mUUID);
                        System.out.println(btSocket);
                        btSocket.connect();
                        System.out.println(btSocket.isConnected());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    counter++;
                } while (!(btSocket.isConnected()) && counter < 3);
            } catch (Exception e){
                Log.i("", e.getMessage());
            }
//
//
//            try {
//                OutputStream outputStream = btSocket.getOutputStream();
//                outputStream.write(48);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
            String readings = "";
            InputStream inputStream = null;
            try {
                inputStream = btSocket.getInputStream();
                inputStream.skip(inputStream.available());

                while (true) {
                    for(int i=0; i<10; i++){
                        byte b = (byte) inputStream.read();
//                        System.out.println((char) b);
                        readings += (char) b;
                    }
                    System.out.println(readings);
                    readings = "";
                }

            } catch (IOException e) {
                e.printStackTrace();
            }


            try {
                btSocket.close();
                System.out.println(btSocket.isConnected());
            } catch (IOException e) {
                e.printStackTrace();
            }


        } else {
            ;
//            Toast.makeText(getApplicationContext(), "Please connect to fitness monitor.", Toast.LENGTH_LONG).show();
        }
    }

}
