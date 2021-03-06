package com.bitwiselifting.v1;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

public class MainActivity extends AppCompatActivity {
    public static Activity bleReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*
         * View Declarations
         */
        Button orientationTesting = findViewById(R.id.orientationTestingButton);
        Button bluetoothTesting = findViewById(R.id.bluetoothTestingButton);
        Button startBtn = findViewById(R.id.startButton);

        /*
         * Listener Declarations
         */
        orientationTesting.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent myIntent = new Intent(view.getContext(), Orientation.class);
                startActivityForResult(myIntent, 0);

            }
        });

        bluetoothTesting.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent myIntent = new Intent(view.getContext(), Bluetooth.class);
                startActivityForResult(myIntent, 0);

            }
        });

        startBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent myIntent = new Intent(view.getContext(), Exercise.class);
                startActivityForResult(myIntent, 0);

            }
        });

        /*
        * Permission Declarations
         */
        //TODO: BAD BAD BAD implementation... if they say no then we crash
        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {


            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    2);


        }


    }
}
