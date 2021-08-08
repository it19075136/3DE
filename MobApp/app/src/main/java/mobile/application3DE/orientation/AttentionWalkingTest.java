package mobile.application3DE.orientation;


import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.TriggerEvent;
import android.hardware.TriggerEventListener;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.google.android.material.textview.MaterialTextView;
import com.squareup.picasso.Picasso;

import mobile.application3DE.R;
import mobile.application3DE.utilities.BaseActivity;

//@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
public class AttentionWalkingTest extends BaseActivity implements SensorEventListener {

    MaterialTextView counter,instruct,output;
    ImageView status;
    int count = 3;
    SensorManager  sensorManager;
    CountDownTimer pauseCounter;
    int pauseTimer = 0;
    Sensor sensor;
    boolean walking = true;
    float initSteps = 0;

//    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attention_walking_test);

        counter = findViewById(R.id.counter);
        instruct = findViewById(R.id.instruct);
        instruct.setVisibility(View.INVISIBLE);
        output = findViewById(R.id.output);
        output.setText("Start walking in");
        status = findViewById(R.id.status);
        status.setVisibility(View.INVISIBLE);

//        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
//        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_SIGNIFICANT_MOTION);

//        TriggerEventListener triggerEventListener = new TriggerEventListener() {
//            @Override
//            public void onTrigger(TriggerEvent triggerEvent) {
//                Log.d("WALKING","Object is moving");
//                Picasso.get().load(R.drawable.success).into(status);
//                pauseTimer = 0;
//                pauseCounter.cancel();
//                pauseCounter.start();
//            }
//        };
//
//        sensorManager.requestTriggerSensor(triggerEventListener, sensor);

        startTest();

    }

    private void startTest() {
        count = 3;
        counter.setText(String.valueOf(count));
        counter.setVisibility(View.VISIBLE);
        CountDownTimer countDownTimer = new CountDownTimer(3000, 1000) {
            @Override
            public void onTick(long l) {
                counter.setText(String.valueOf(count--));
            }

            @Override
            public void onFinish() {
                prepareWalkTest();
                startWalkTest();
            }
        };
        countDownTimer.start();
    }

    private void prepareWalkTest() {

        counter.setVisibility(View.INVISIBLE);
        status.setVisibility(View.VISIBLE);
        instruct.setVisibility(View.VISIBLE);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
    }

    private void startWalkTest() {

        if(sensor != null) {
            sensorManager.registerListener(this, sensor, SensorManager.);
            pauseCounter = new CountDownTimer(5000, 1000) {
                @Override
                public void onTick(long l) {
                    Log.d("ONTICK","1 sec");
                    ++pauseTimer;
                    if(pauseTimer > 3)
                        Picasso.get().load(R.drawable.failure).into(status);
                }

                @Override
                public void onFinish() {
                    Log.d("ONFINSISH","counter stopped");
                }
            }.start();
        }
        else
            Toast.makeText(this, "Sensor not found!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if(initSteps == 0)
            initSteps = sensorEvent.values[0];

        if(walking) {
            output.setText(String.valueOf(sensorEvent.values[0] - initSteps));
            Log.d("WALKING","Object is moving");
            Picasso.get().load(R.drawable.success).into(status);
            pauseTimer = 0;
            pauseCounter.cancel();
            pauseCounter.start();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        Toast.makeText(this,"Accuracy changed : "+i, Toast.LENGTH_SHORT).show();
    }

}