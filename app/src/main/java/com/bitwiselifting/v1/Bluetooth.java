package com.bitwiselifting.v1;


import android.bluetooth.BluetoothDevice;
import android.os.Handler;
import android.os.ParcelUuid;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.github.douglasjunior.bluetoothclassiclibrary.BluetoothConfiguration;
import com.github.douglasjunior.bluetoothclassiclibrary.BluetoothService;
import com.github.douglasjunior.bluetoothclassiclibrary.BluetoothStatus;
import com.github.douglasjunior.bluetoothlowenergylibrary.BluetoothLeService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;




public class Bluetooth extends AppCompatActivity {


    protected BluetoothDevice device;
    protected BluetoothService service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);

        /*
        *
        * Bluetooth Configuration
        *
         */
        BluetoothConfiguration config = new BluetoothConfiguration();
        config.context = getApplicationContext();
        config.bluetoothServiceClass = BluetoothLeService.class; // BluetoothClassicService.class or BluetoothLeService.class
        config.bufferSize = 1024;
        config.characterDelimiter = '\n';
        config.deviceName = "BitwiseLifting";
        config.callListenersInMainThread = true;


        // Bluetooth LE Setup
        config.uuidService = UUID.fromString("e7810a71-73ae-499d-8c15-faa9aef0c3f2");
        config.uuidCharacteristic = UUID.fromString("bef8d6c9-9c21-4c9e-b632-bd58c1009f9f");
        config.transport = BluetoothDevice.TRANSPORT_LE; // Required for dual-mode devices
        config.uuid = UUID.fromString("6e400001-b5a3-f393-e0a9-e50e24dcca9e"); // Used to filter for our BlueFruit LE Device

        BluetoothService.init(config);

        /*
        * Bluetooth Service Initialization
         */

        service = BluetoothService.getDefaultInstance();

        /*
        * Callback for Bluetooth Scan
         */
        service.setOnScanCallback(new BluetoothService.OnBluetoothScanCallback() {
            /*
            * We found the device, handle it and then end the scan.
             */
            @Override
            public void onDeviceDiscovered(BluetoothDevice bDevice, int rssi) {
                device = bDevice;
                connectToDevice();
            }

            /*
            * When we scan, wait 5 seconds and then end the scan.
             * We should have found the device by then. If not then it was not in range.
             */
            @Override
            public void onStartScan() {
                final Handler handle = new Handler();
                handle.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        service.stopScan();
                    }
                },5000);
            }

            /*
            * Then scan stopped, if we did not find anything then we should probably tell the user.
             */
            @Override
            public void onStopScan() {

            }
        });

        /*
        * Start the scan
         */
        service.startScan();

    }

    public void connectToDevice(){

        service.setOnEventCallback(new BluetoothService.OnBluetoothEventCallback() {

            @Override
            public void onDataRead(byte[] buffer, int length) {
                String res = "";
                for(byte b:buffer){
                    res += " " + b;
                }
                Log.d("Date Read:",res);
            }

            @Override
            public void onStatusChange(BluetoothStatus status) {
                if(status == BluetoothStatus.NONE) {
                    service.disconnect();
                }
                Log.d("Status Changed:",status.toString());

            }

            @Override
            public void onDeviceName(String deviceName) {
                Log.d("Device Name:",deviceName);
            }

            @Override
            public void onToast(String message) {
                Log.d("Toast:",message);
            }

            @Override
            public void onDataWrite(byte[] buffer) {
                String res = "";
                for(byte b:buffer){
                    res += " " + b;
                }
                Log.d("Date Write:",res);
            }
        });

        service.connect(device); // See also service.disconnect();
    }


}