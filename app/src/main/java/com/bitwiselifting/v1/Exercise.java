package com.bitwiselifting.v1;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

public class Exercise extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise);

        /*
         * View Declarations
         */
        Button startExercise = findViewById(R.id.startExerciseButton);
        Button calibrateExercise = findViewById(R.id.calibrateButton);
        Spinner workoutChoice = findViewById(R.id.workoutList);
        ArrayAdapter<String> workoutAdapter = new ArrayAdapter<String>(Exercise.this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.Workouts));
        workoutAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        workoutChoice.setAdapter(workoutAdapter);

        /*
         * Listener Declarations
         */
        startExercise.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent myIntent = new Intent(view.getContext(), Orientation.class);
                startActivityForResult(myIntent, 0);

            }
        });

        calibrateExercise.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent myIntent = new Intent(view.getContext(), Orientation.class);
                startActivityForResult(myIntent, 0);

            }
        });
    }
}
