package com.bitwiselifting.v1;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattServer;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.os.Handler;
import android.os.ParcelUuid;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


import no.nordicsemi.android.support.v18.scanner.BluetoothLeScannerCompat;
import no.nordicsemi.android.support.v18.scanner.ScanCallback;
import no.nordicsemi.android.support.v18.scanner.ScanFilter;
import no.nordicsemi.android.support.v18.scanner.ScanResult;
import no.nordicsemi.android.support.v18.scanner.ScanSettings;

import static android.bluetooth.BluetoothGatt.GATT_SUCCESS;
import static android.bluetooth.BluetoothProfile.GATT;
import static android.bluetooth.BluetoothProfile.STATE_CONNECTED;

public class Bluetooth extends AppCompatActivity {
    protected BluetoothLeScannerCompat scanner;
    protected myCallback callback;
    protected BluetoothDevice device;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);

        callback = new myCallback();

        scanner = BluetoothLeScannerCompat.getScanner();
        ScanSettings settings = new ScanSettings.Builder()
                .setLegacy(false)
                .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                .setReportDelay(1000)
                .setUseHardwareBatchingIfSupported(false)
                .build();
        List<ScanFilter> filters = new ArrayList<>();
        filters.add(new ScanFilter.Builder().setServiceUuid(new ParcelUuid(UUID.fromString("6e400001-b5a3-f393-e0a9-e50e24dcca9e"))).build());
        scanner.startScan(filters, settings, callback);


        final Handler handle = new Handler();
        handle.postDelayed(new Runnable() {
            @Override
            public void run() {
                stopScan();
            }
        },5000);

        /*
        * Handle connecting to bluetooth device
         */
        if(device != null){

        }
        else{
            Log.d("Bluetooth Device:","NULL");
        }
    }

    private void stopScan() {
        Log.i("Device Found: ", "Scan Stopped");
        scanner.stopScan(callback);
    }


    public class myCallback extends ScanCallback {
        @Override
        public void onScanResult(final int callbackType, @NonNull final ScanResult result) {

            Log.d("SCANNED:", result.toString());
        }

        @Override
        public void onScanFailed(final int errorCode) {
            Log.d("SCAN FAILED:", Integer.toString(errorCode));
        }

        @Override
        public void onBatchScanResults(@NonNull final List<ScanResult> results) {
            if(results.size()==1) { //we have a filter on UUID so if we got one then we are good
                device = results.get(0).getDevice();
            }

            Log.d("BATCH:", "SIZE " + results.size());

        }
    }

    public class GattCallback extends BluetoothGattCallback {
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
        @Override
        public void onPhyUpdate(BluetoothGatt gatt, int txPhy, int rxPhy, int status) {
            if(status == GATT_SUCCESS){
                Log.d("PHY UPDATE:","SUCCESS");
            }
            else{
                Log.d("PHY UPDATE:","FAILURE");
            }
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
            if(status == GATT_SUCCESS){
                Log.d("PHY READ:","SUCCESS");
            }
            else{
                Log.d("PHY READ:","FAILURE");
            }
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
            if(status == GATT_SUCCESS){
                Log.d("CONNECTION CHANGED:","SUCCESS");
                Log.d("NEW DEVICE:",(newState==STATE_CONNECTED?"STATE CONNECTED":"STATE DISCONNECTED"));
            }
            else{
                Log.d("CONNECTION CHANGED:","FAILURE");
            }
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
            if(status == GATT_SUCCESS) {
                List<BluetoothGattService> services = gatt.getServices();
                for(BluetoothGattService s : services){
                    Log.d("Service found:",s.toString());
                }
            }
            else{
                Log.d("Service found:", "FAILURE");
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
            if(status == GATT_SUCCESS) {
                Log.d("Characteristic found:",characteristic.toString());
            }
            else{
                Log.d("Characteristic found:", "FAILURE");
            }
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
            if(status == GATT_SUCCESS){
                Log.d("Characteristic written:",characteristic.toString());
            }
            else{
                Log.d("Char write FAILED:",characteristic.toString());
            }
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
           if(characteristic != null){
               Log.d("Characteristic Changed",characteristic.toString());
           }
           else{
               Log.d("Characteristic Changed","FAILURE");
           }
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
            if(status == GATT_SUCCESS){
                Log.d("Descriptor Read:",descriptor.toString());
            }
            else{
                Log.d("Descriptor Read:","FAILURE");
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
            if(status == GATT_SUCCESS){
                Log.d("Descriptor Write:",descriptor.toString());
            }
            else{
                Log.d("Descriptor Write:","FAILURE");
            }
        }

        /**
         * Callback invoked when a reliable write transaction has been completed.
         *
         * @param gatt GATT client invoked {@link BluetoothGatt#executeReliableWrite}
         * @param status {@link BluetoothGatt#GATT_SUCCESS} if the reliable write transaction was
         * executed successfully
         */
        public void onReliableWriteCompleted(BluetoothGatt gatt, int status) {
            if(status == GATT_SUCCESS){
                Log.d("Reliable Write:","SUCCESS");
            }
            else{
                Log.d("Reliable Write:","FAILURE");
            }
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
            if(status == GATT_SUCCESS){
                Log.d("Read Remote Rssi:","SUCCESS");
            }
            else{
                Log.d("Read Remote Rssi:","FAILURE");
            }

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
            if(status == GATT_SUCCESS){
                Log.d("MTU CHANGE:","VALUE: "+ mtu );
            }
            else{
                Log.d("MTU CHANGE:","FAILURE" );
            }
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
            if(status == GATT_SUCCESS){
                Log.d("Connection Updated:","Interval: " + interval + " Latency: "+ latency+" Timeout: " + timeout);
            }
            else{
                Log.d("Connection Updated:", "FAILURE");
            }
        }
    }

}

