package com.bitwiselifting.v1;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.bitwiselifting.v1.AlgorithmCode.ExerciseData;
import com.bitwiselifting.v1.AlgorithmCode.StaticMethods;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class Exercise extends AppCompatActivity {
    protected boolean mScanning;
    private BluetoothAdapter bluetoothAdapter;
    final int REQUEST_ENABLE_BT = 55;
    private Handler handler = new Handler();
    private static final long SCAN_PERIOD = 3000;
    protected ArrayList<String> data = new ArrayList<>();
    protected boolean recording = false;
    protected boolean wasCalibrate = false;
    private ArrayList<BluetoothDevice> devicesScanned = new ArrayList<BluetoothDevice>();
    private myLeScanCallback leScanCallback = new myLeScanCallback();
    private BluetoothDevice bDevice;
    protected TextView connectedText;
    protected ArrayList<ArrayList<Float>> calibrationData = new ArrayList<ArrayList<Float>>();
    protected ArrayList<ArrayList<Float>> lastExData = new ArrayList<ArrayList<Float>>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise);
        /*
        BLE Declarations
         */
        // Initializes Bluetooth adapter.
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();

        //turn on the bluetooth adaptor
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

        /*
         * View Declarations
         */
        Button startExercise = findViewById(R.id.startExerciseButton);
        Button stopExercise = findViewById(R.id.stopExerciseButton);
        Button calibrateExercise = findViewById(R.id.calibrateButton);
        Button connectBtn = findViewById(R.id.connectBtn);
        connectedText = findViewById(R.id.connectedText);
        connectedText.setText("DISCONNECTED");
        Spinner workoutChoice = findViewById(R.id.workoutList);
        ArrayAdapter<String> workoutAdapter = new ArrayAdapter<String>(Exercise.this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.Workouts));
        workoutAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        workoutChoice.setAdapter(workoutAdapter);
        TextView csv = findViewById(R.id.dataDump);



        /*
         * Listener Declarations
         */
        connectBtn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                /*
                 * Bluetooth Scan
                 */
                scanLeDevice(true);
            }
        });
        stopExercise.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                recording = false;
                //TODO: remove bad data here


                if(wasCalibrate){ //save the data into calibration file
                    //clear out current calibration data
                    calibrationData.clear();
                    //convert the string array into floats
                    //first remove all of the bad data
                    ArrayList<String> goodCal = new ArrayList<String>();
                    for(String s:data){
                        if(s.length() == 19){
                            goodCal.add(s);
                        }
                    }
                    for(String str:goodCal){
                        String line= str;
                        String[] values = line.split(",");
                        Float[] floatrow = new Float[values.length];

                        int i = 0;
                        for(String s : values){
                            float res = Float.parseFloat(s);
                            floatrow[i] = res;
                            i++;
                        }
                        // this adds the currently parsed line to the 2-dimensional string array
                        calibrationData.add(new ArrayList<Float>(Arrays.asList(floatrow)));
                    }

                }
                else{ //compare to calibration file
                    lastExData.clear();
                    //convert the string array into floats
                    //first remove all of the bad data
                    ArrayList<String> goodEx = new ArrayList<String>();
                    for(String s:data){
                        if(s.length() == 19){
                            goodEx.add(s);
                        }
                    }
                    for(String str:goodEx){
                        String line= str;
                        String[] values = line.split(",");
                        Float[] floatrow = new Float[values.length];

                        int i = 0;
                        for(String s : values){
                            float res = Float.parseFloat(s);
                            floatrow[i] = res;
                            i++;
                        }
                        // this adds the currently parsed line to the 2-dimensional string array
                        lastExData.add(new ArrayList<Float>(Arrays.asList(floatrow)));
                    }



                    ExerciseData cal = StaticMethods.analyseData(calibrationData);
                    ExerciseData current = StaticMethods.analyseData(lastExData);

                    Log.d("Calibration:",Double.toString(cal.getAverageTime()));
                    Log.d("Current:",Double.toString(current.getAverageTime()));
                }

                data.clear();
            }
        });
        startExercise.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                /*
                */
                recording = true;
                wasCalibrate = false;

            }
        });

        calibrateExercise.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {


            wasCalibrate = true;
            recording = true;
            }

        });
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


    public class Bluetooth extends AppCompatActivity {

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
            /*
             * Bluetooth Scan
             */
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

    }

    public class ScanCallback extends BluetoothGattCallback {
        BluetoothGattCharacteristic datastream;
        BluetoothGattService datastreamService;
        /**
         * Callback triggered as result of {@link BluetoothGatt#setPreferredPhy}, or as a result of
         * remote device changing the PHY.
         *
         * @param gatt GATT client
         * @param txPhy the transmitter PHY in use. One of {@link BluetoothDevice#PHY_LE_1M}, {@link
         * BluetoothDevice#PHY_LE_2M}, and {@link BluetoothDevice#PHY_LE_CODED}.
         * @param rxPhy the receiver PHY in use. One of {@link BluetoothDevice#PHY_LE_1M}, {@link
         * BluetoothDevice#PHY_LE_2M}, and {@link BluetoothDevice#PHY_LE_CODED}.
         * @param status Status of the PHY update operation. {@link BluetoothGatt#GATT_SUCCESS} if the
         * operation succeeds.
         */
        public void onPhyUpdate(BluetoothGatt gatt, int txPhy, int rxPhy, int status) {
        }

        /**
         * Callback triggered as result of {@link BluetoothGatt#readPhy}
         *
         * @param gatt GATT client
         * @param txPhy the transmitter PHY in use. One of {@link BluetoothDevice#PHY_LE_1M}, {@link
         * BluetoothDevice#PHY_LE_2M}, and {@link BluetoothDevice#PHY_LE_CODED}.
         * @param rxPhy the receiver PHY in use. One of {@link BluetoothDevice#PHY_LE_1M}, {@link
         * BluetoothDevice#PHY_LE_2M}, and {@link BluetoothDevice#PHY_LE_CODED}.
         * @param status Status of the PHY read operation. {@link BluetoothGatt#GATT_SUCCESS} if the
         * operation succeeds.
         */
        public void onPhyRead(BluetoothGatt gatt, int txPhy, int rxPhy, int status) {
        }

        /**
         * Callback indicating when GATT client has connected/disconnected to/from a remote
         * GATT server.
         *
         * @param gatt GATT client
         * @param status Status of the connect or disconnect operation. {@link
         * BluetoothGatt#GATT_SUCCESS} if the operation succeeds.
         * @param newState Returns the new connection state. Can be one of {@link
         * BluetoothProfile#STATE_DISCONNECTED} or {@link BluetoothProfile#STATE_CONNECTED}
         */
        public void onConnectionStateChange(BluetoothGatt gatt, int status,
                                            int newState) {

            Log.d(Integer.toString(status), gatt.getDevice().toString());
            if(newState == BluetoothProfile.STATE_CONNECTED){
                runOnUiThread(new Runnable(){
                    @Override
                    public void run(){
                        connectedText.setText("CONNECTED");
                    }
                });
            }
            else{
                runOnUiThread(new Runnable(){
                    @Override
                    public void run(){
                        connectedText.setText("DISCONNECTED");
                    }
                });
            }
            gatt.discoverServices();



        }

        /**
         * Callback invoked when the list of remote services, characteristics and descriptors
         * for the remote device have been updated, ie new services have been discovered.
         *
         * @param gatt GATT client invoked {@link BluetoothGatt#discoverServices}
         * @param status {@link BluetoothGatt#GATT_SUCCESS} if the remote device has been explored
         * successfully.
         */
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                for (BluetoothGattService gattService : gatt.getServices()) {
                    Log.d("SERVICE FOUND", gattService.getUuid().toString());
                    if(gattService.getUuid().toString().equals("6e400001-b5a3-f393-e0a9-e50e24dcca9e")){
                        datastreamService = gattService;
                        datastream = gattService.getCharacteristics().get(0);
                    }
                }
            }
            else{
                Log.d("SERVICE FOUND","n/a");
                gatt.discoverServices();
            }

            if(datastream != null){

                Log.d("READING",datastream.toString());
                gatt.setCharacteristicNotification(datastream,true);
                //gatt.readDescriptor(datastream.getDescriptors().get(0));
                //gatt.readDescriptor(datastream.getDescriptors().get(1));
                BluetoothGattDescriptor d = datastream.getDescriptors().get(1);
                d.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                gatt.writeDescriptor(d);
            }
        }

        /**
         * Callback reporting the result of a characteristic read operation.
         *
         * @param gatt GATT client invoked {@link BluetoothGatt#readCharacteristic}
         * @param characteristic Characteristic that was read from the associated remote device.
         * @param status {@link BluetoothGatt#GATT_SUCCESS} if the read operation was completed
         * successfully.
         */
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic,
                                         int status) {
            Log.d("CHARACTERISTIC READ",characteristic.getValue().toString());
        }

        /**
         * Callback indicating the result of a characteristic write operation.
         *
         * <p>If this callback is invoked while a reliable write transaction is
         * in progress, the value of the characteristic represents the value
         * reported by the remote device. An application should compare this
         * value to the desired value to be written. If the values don't match,
         * the application must abort the reliable write transaction.
         *
         * @param gatt GATT client invoked {@link BluetoothGatt#writeCharacteristic}
         * @param characteristic Characteristic that was written to the associated remote device.
         * @param status The result of the write operation {@link BluetoothGatt#GATT_SUCCESS} if the
         * operation succeeds.
         */
        public void onCharacteristicWrite(BluetoothGatt gatt,
                                          BluetoothGattCharacteristic characteristic, int status) {
        }

        /**
         * Callback triggered as a result of a remote characteristic notification.
         *
         * @param gatt GATT client the characteristic is associated with
         * @param characteristic Characteristic that has been updated as a result of a remote
         * notification event.
         */
        public void onCharacteristicChanged(BluetoothGatt gatt,
                                            BluetoothGattCharacteristic characteristic) {
            //Log.d("CHARACTERISTIC CHANGED",characteristic.getStringValue(0));
            if(recording) {
                data.add(characteristic.getStringValue(0));
            }
            gatt.readCharacteristic(characteristic);
        }

        /**
         * Callback reporting the result of a descriptor read operation.
         *
         * @param gatt GATT client invoked {@link BluetoothGatt#readDescriptor}
         * @param descriptor Descriptor that was read from the associated remote device.
         * @param status {@link BluetoothGatt#GATT_SUCCESS} if the read operation was completed
         * successfully
         */
        public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor,
                                     int status) {
            byte [] b = descriptor.getValue();
            for(int i=0;i<descriptor.getValue().length;i++) {

                Log.d("DESCRIPTOR READ", Byte.toString(b[i]));
            }

        }

        /**
         * Callback indicating the result of a descriptor write operation.
         *
         * @param gatt GATT client invoked {@link BluetoothGatt#writeDescriptor}
         * @param descriptor Descriptor that was writte to the associated remote device.
         * @param status The result of the write operation {@link BluetoothGatt#GATT_SUCCESS} if the
         * operation succeeds.
         */
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor,
                                      int status) {
            Log.d("DESCRIPTOR WRITE",descriptor.toString());
            BluetoothGattCharacteristic c = descriptor.getCharacteristic();
            gatt.readCharacteristic(c);
        }

        /**
         * Callback invoked when a reliable write transaction has been completed.
         *
         * @param gatt GATT client invoked {@link BluetoothGatt#executeReliableWrite}
         * @param status {@link BluetoothGatt#GATT_SUCCESS} if the reliable write transaction was
         * executed successfully
         */
        public void onReliableWriteCompleted(BluetoothGatt gatt, int status) {
        }

        /**
         * Callback reporting the RSSI for a remote device connection.
         *
         * This callback is triggered in response to the
         * {@link BluetoothGatt#readRemoteRssi} function.
         *
         * @param gatt GATT client invoked {@link BluetoothGatt#readRemoteRssi}
         * @param rssi The RSSI value for the remote device
         * @param status {@link BluetoothGatt#GATT_SUCCESS} if the RSSI was read successfully
         */
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
        }

        /**
         * Callback indicating the MTU for a given device connection has changed.
         *
         * This callback is triggered in response to the
         * {@link BluetoothGatt#requestMtu} function, or in response to a connection
         * event.
         *
         * @param gatt GATT client invoked {@link BluetoothGatt#requestMtu}
         * @param mtu The new MTU size
         * @param status {@link BluetoothGatt#GATT_SUCCESS} if the MTU has been changed successfully
         */
        public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {
        }

        /**
         * Callback indicating the connection parameters were updated.
         *
         * @param gatt GATT client involved
         * @param interval Connection interval used on this connection, 1.25ms unit. Valid range is from
         * 6 (7.5ms) to 3200 (4000ms).
         * @param latency Slave latency for the connection in number of connection events. Valid range
         * is from 0 to 499
         * @param timeout Supervision timeout for this connection, in 10ms unit. Valid range is from 10
         * (0.1s) to 3200 (32s)
         * @param status {@link BluetoothGatt#GATT_SUCCESS} if the connection has been updated
         * successfully
         * @hide
         */
        public void onConnectionUpdated(BluetoothGatt gatt, int interval, int latency, int timeout,
                                        int status) {
        }

    }
}


