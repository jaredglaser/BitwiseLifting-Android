package com.bitwiselifting.v1;

import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.graphics.Matrix;
import android.widget.TextView;

public class Orientation extends AppCompatActivity {
    /*
    Put any UI elements the various other threads/subclasses need to access
     */
    private ImageView barbell;
    private TextView rotationValue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orientation);

        /*
        Give the protected variables values corresponding to the elements in the UI
         */
        barbell = findViewById(R.id.barbell);
        rotationValue = findViewById(R.id.rotationValue);

        /*
        Create an instance of the rotater thread so the UI can still be properly rendered.
         */
        RotationRunnable rotateAction = new RotationRunnable();
        new Thread(rotateAction).start();


    }

    class RotationRunnable implements Runnable {

        int i;

        /*
        Rotate the barbell by increments of 1 degree (and update text) every 50ms
         */
        @Override
        public void run(){
            i = 0;
            while(true){
                Log.d("Thread:",""+i);

                i++;
                if (i > 360)
                    i = 0;

                /*
                Send the changes we want to make to the UI Thread
                 */
                runOnUiThread(new Runnable(){

                    @Override
                    public void run(){
                        Log.d("Thread","rotation by: "+i);
                        barbell.setRotation(i);
                        rotationValue.setText("Current Rotation: "+i+" degrees");
                    }
                });

                /*
                Set the thread to sleep for 50ms
                 */
                try{
                    Thread.sleep(50);
                }
                catch(InterruptedException ex){
                    Log.d("THREAD SLEEP:","Interrupted Exception Caught");
                }

            }
        }
    }
}
