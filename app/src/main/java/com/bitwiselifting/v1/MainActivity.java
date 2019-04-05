package com.bitwiselifting.v1;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;

public class MainActivity extends AppCompatActivity {

    Spinner spinner;

    Bench bench;
    Deadlift deadlift;
    PowerClean powerclean;
    Snatch snatch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*
         * View Declarations
         */

        spinner = findViewById(R.id.workoutList);
        ImageButton bluetoothBtn = findViewById(R.id.bluetoothButton);

        bench = new Bench();
        deadlift = new Deadlift();
        powerclean = new PowerClean();
        snatch = new Snatch();

        ArrayAdapter<String> workoutAdapter = new ArrayAdapter<String>(MainActivity.this,
                R.layout.workout_spinner, getResources().getStringArray(R.array.Workouts));
        workoutAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(workoutAdapter);
        /*
         * Listener Declarations
         */

        bluetoothBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent myIntent = new Intent(view.getContext(), Bluetooth.class);
                startActivityForResult(myIntent, 0);

            }
        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        setFragment(bench);
                        break;
                    case 1:
                        setFragment(deadlift);
                        break;
                    case 2:
                        setFragment(powerclean);
                        break;
                    case 3:
                        setFragment(snatch);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

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
    public void setFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_frame, fragment);
        fragmentTransaction.commit();
    }
}
