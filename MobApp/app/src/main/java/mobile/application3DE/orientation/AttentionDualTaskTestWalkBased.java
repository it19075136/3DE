package mobile.application3DE.orientation;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import mobile.application3DE.R;
import mobile.application3DE.models.Result;
import mobile.application3DE.utilities.BaseActivity;

public class AttentionDualTaskTestWalkBased extends BaseActivity implements SensorEventListener {

    TextView testInstruct,counter;
    ImageView status;
    ProgressBar progressBar;
    int count = 3,activityCounter = 0;;
    SensorManager sensorManager;
    CountDownTimer pauseCounter,activityTimer;;
    int pauseTimer = 0;
    Sensor sensor;
    boolean walking = true;
    AlertDialog dialog;
    float walkingSpeed = 0,diff,initSteps = 0;
    String currentUser,type = "gen";
    DatabaseReference userRef,dualTaskRef,attentionRef;
    SimpleDateFormat formatDate;
    Intent resultIntent;

    // we will get the default FirebaseDatabase instance
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

    // we will get a DatabaseReference for the database root node
    DatabaseReference databaseReference = firebaseDatabase.getReference();
    private float Finalresult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attention_dual_task_test_walk_based);

        progressBar = findViewById(R.id.progressBar);
        counter = findViewById(R.id.counter);
        testInstruct = findViewById(R.id.testInstruct);
        testInstruct.setVisibility(View.INVISIBLE);
        status = findViewById(R.id.status);
        status.setVisibility(View.INVISIBLE);
        progressBar = findViewById(R.id.progressBar);

        formatDate = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
// Adding signed in user.

        if(acct != null)
            currentUser = acct.getId();

        if (getIntent().getStringExtra("type") != null)
            type = "once";

        userRef = databaseReference.child("users/"+currentUser);
        dualTaskRef = databaseReference.child("ComponentBasedResults/"+currentUser+"/Orientation/Attention/1/walking");
        attentionRef = databaseReference.child("AttentionResults/"+currentUser+"/Orientation/Attention");
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

        testInstruct.setText(R.string.startIn);
        progressBar.setProgress(0);
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

        initSteps = 0;
        activityCounter = 0;
        counter.setVisibility(View.INVISIBLE);
        status.setVisibility(View.VISIBLE);
        testInstruct.setVisibility(View.VISIBLE);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        prepareDialog();

    }

    private void startWalkTest() {

        walking = true;
        activityTimer = new CountDownTimer(600000, 1000) {
            @Override
            public void onTick(long l) {
                ++activityCounter;
            }

            @Override
            public void onFinish() {
                Toast.makeText(AttentionDualTaskTestWalkBased.this, "You failed to complete the activity, Please retry!",Toast.LENGTH_SHORT).show();
                dialog.show();
            }
        }.start();
        if(sensor != null) {
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_FASTEST);
            pauseCounter = new CountDownTimer(10000, 1000) {
                @Override
                public void onTick(long l) {
//                    Toast.makeText(AttentionWalkingTest.this,"ON TICK "+pauseTimer, Toast.LENGTH_SHORT).show();
//                    Log.d("ONTICK","1 sec");
                    ++pauseTimer;
                    if(pauseTimer > 7)
                        Picasso.get().load(R.drawable.failure).into(status);
                }

                @Override
                public void onFinish() {
//                    Toast.makeText(AttentionWalkingTest.this,"Finsihed timer", Toast.LENGTH_SHORT).show();
//                    Log.d("ONFINSISH","counter stopped");
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

        if((sensorEvent.values[0] - initSteps) >= 50) {
            walking = false;
            activityTimer.cancel();
            pauseCounter.cancel();
            testInstruct.setText(String.valueOf(sensorEvent.values[0] - initSteps));
            sensorManager.unregisterListener(this);
            walkingSpeed = (sensorEvent.values[0] - initSteps)/activityCounter;
            dialog.show();
        }



        if(walking) {
            testInstruct.setText(String.valueOf(sensorEvent.values[0] - initSteps));
            progressBar.setProgress((int)(sensorEvent.values[0] - initSteps)*2);
//            Toast.makeText(this,"WALKING", Toast.LENGTH_SHORT).show();
//            Log.d("WALKING","Object is moving");
            Picasso.get().load(R.drawable.success).into(status);
            pauseTimer = 0;
            pauseCounter.cancel();
            pauseCounter.start();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        String accuracy = "High";
        switch (i){
            case SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM:
                accuracy = "Normal";
                break;
            case SensorManager.SENSOR_STATUS_ACCURACY_LOW:
                accuracy = "Low";
                break;
            case SensorManager.SENSOR_STATUS_NO_CONTACT:
                accuracy = "Sensor has no Contact";
                break;
            case SensorManager.SENSOR_STATUS_UNRELIABLE:
                accuracy = "Unreliable";
                break;
            default:
                accuracy = "High";
        }
        Toast.makeText(this,"Accuracy changed : "+accuracy, Toast.LENGTH_SHORT).show();
    }

    public void prepareDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Do you confirm the result?")
                .setTitle("Confirm result");

        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
                Toast.makeText(AttentionDualTaskTestWalkBased.this,"Your walking speed is "+walkingSpeed/2+" secs per metre",Toast.LENGTH_SHORT).show();
                diff = Float.parseFloat(getIntent().getStringExtra("singleTaskResult")) - walkingSpeed/2;
                if(diff < 0)
                    diff = (float)0;
                // add diff and result to firebase,add timestamps to user
                //validate when you have more
                attentionRef.child(formatDate.format(new Date())).setValue(new Result(type,formatDate.format(new Date()),getIntent().getStringExtra("singleTaskResult"),String.valueOf(walkingSpeed/2),getFinalResult(),String.format("%.4f",diff))).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        resultIntent = new Intent(getApplicationContext(),AttentionResultsPage.class);
                        resultIntent.putExtra("result",getFinalResult());
                        resultIntent.putExtra("originator","walk");
                        resultIntent.putExtra("diff",String.format("%.4f",diff));
                        resultIntent.putExtra("type",type);
                        if (type.equals("gen"))
                            dualTaskRef.child("dualTask").setValue(walkingSpeed/2).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                userRef.child("DualTask1WalkBasedCompleted").setValue(formatDate.format(new Date())).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        dualTaskRef.child("difference").setValue(String.format("%.4f",diff)).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                dualTaskRef.child("impairment").setValue(getFinalResult());
                                                startActivity(resultIntent);
                                            }
                                        });
                                    }
                                });
                            }
                        });
                        else
                            startActivity(resultIntent);
                    }
                });
            }
        });
        builder.setNegativeButton(R.string.retry, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                startTest();
            }
        });
        dialog = builder.create();

    }

    private String getFinalResult() {

        Finalresult = (diff/Float.parseFloat(getIntent().getStringExtra("singleTaskResult"))) * 100;
        return String.format("%.2f",Finalresult);
    }
}