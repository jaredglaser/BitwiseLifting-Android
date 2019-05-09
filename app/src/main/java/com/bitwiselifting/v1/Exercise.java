package com.bitwiselifting.v1;

import android.content.Context;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

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
        TextView csv = findViewById(R.id.dataDump);

        /*
         * Listener Declarations
         */
        startExercise.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                try {
                    OutputStreamWriter outputStreamWriter = new OutputStreamWriter(view.getContext().openFileOutput("Test.csv", Context.MODE_PRIVATE));
                    outputStreamWriter.write("testing");
                    outputStreamWriter.close();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        calibrateExercise.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String ret = "";

                try {
                    InputStream inputStream = view.getContext().openFileInput("Test.csv");

                    if (inputStream != null) {
                        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                        String receiveString = "";
                        StringBuilder stringBuilder = new StringBuilder();

                        while ((receiveString = bufferedReader.readLine()) != null) {
                            stringBuilder.append(receiveString);
                        }

                        inputStream.close();
                        ret = stringBuilder.toString();
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                csv.setText(ret);
            }
        });
    }
}
