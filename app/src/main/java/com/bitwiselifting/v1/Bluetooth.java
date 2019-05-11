package com.bitwiselifting.v1;


import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import com.ederdoski.simpleble.utils.BluetoothLEHelper;
import java.util.ArrayList;




public class Bluetooth extends AppCompatActivity {
/*
    protected boolean mScanning;
    private BluetoothAdapter bluetoothAdapter;
    final int REQUEST_ENABLE_BT = 55;
    private Handler handler = new Handler();
    private static final long SCAN_PERIOD = 3000;
    private ArrayList<BluetoothDevice> devicesScanned = new ArrayList<BluetoothDevice>();
    private myLeScanCallback leScanCallback = new myLeScanCallback();
    private BluetoothDevice bDevice;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);


// Initializes Bluetooth adapter.
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();

        //turn on the bluetooth adaptor
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
        */
/*
         * Bluetooth Scan
         *//*

        scanLeDevice(true);


    }


    private void scanLeDevice(final boolean enable) {
        if (enable) {
            // Stops scanning after a pre-defined scan period.
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScanning = false;
                    bluetoothAdapter.stopLeScan(leScanCallback);
                    for(BluetoothDevice b : devicesScanned){
                        if(b.getName()!=null && b.getName().equals("Adafruit Bluefruit LE")){
                            bDevice = b;

                        }

                    }

                    bDevice.connectGatt(getApplicationContext(),false,new ScanCallback());
                }
            }, SCAN_PERIOD);

            mScanning = true;
            bluetoothAdapter.startLeScan(leScanCallback);
        } else {
            mScanning = false;
            bluetoothAdapter.stopLeScan(leScanCallback);
        }

    }

    public class myLeScanCallback implements BluetoothAdapter.LeScanCallback{
        @Override
        public void onLeScan(BluetoothDevice dev, int rssi,byte[] scanRecord){
            devicesScanned.add(dev);
            Log.d("Device Scanned",dev.toString());
        }
    }
*/
}



