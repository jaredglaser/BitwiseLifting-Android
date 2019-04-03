package com.bitwiselifting.v1;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*
         * View Declarations
         */
        Button orientationTesting = findViewById(R.id.orientationTestingbutton);



        /*
         * Listener Declarations
         */
        orientationTesting.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent myIntent = new Intent(view.getContext(), Orientation.class);
                startActivityForResult(myIntent, 0);

            }
        });


    }
}
