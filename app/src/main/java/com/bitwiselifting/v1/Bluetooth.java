package com.bitwiselifting.v1;

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

public class Bluetooth extends AppCompatActivity {
    protected BluetoothLeScannerCompat scanner;
    protected myCallback callback;
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
        scanner.startScan(filters, settings, callback);


        final Handler handle = new Handler();
        handle.postDelayed(new Runnable() {
            @Override
            public void run() {
                stopScan();
            }
        },5000);
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
            for (ScanResult s : results) {
                Log.d("BATCH:", s.toString());
            }

            Log.d("BATCH:", "SIZE " + results.size());

        }
    }

    class ScanRunnable implements Runnable {


        @Override
        public void run(){
            try{
                Thread.sleep(2000);
                Log.d("SLEEP: ","slept");
            }
            catch(InterruptedException ex){
                //do nothing
            }
            BluetoothLeScannerCompat scanner = BluetoothLeScannerCompat.getScanner();
            scanner.stopScan(new myCallback());
        }
    }
}

