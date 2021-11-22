package mobile.application3DE.Visual;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import mobile.application3DE.R;
import mobile.application3DE.Visual.SQLite.Sqlitedb;
import mobile.application3DE.decisionMaking.Dec_making_task;
import mobile.application3DE.models.VisualMemory;
import mobile.application3DE.orientation.AttentionInstructions;
import mobile.application3DE.orientation.tccCalculation;

import java.util.ArrayList;
import java.util.Map;

public class Results extends AppCompatActivity {


    TextView textView1;
    TextView textView2;
    TextView textView3;
    TextView textView4;
    TextView textView5;
    TextView textView6;
    TextView textView7;
    TextView textView8;
    TextView textView9;
    TextView textView10;
    TextView textView12;
    TextView textView13;
    Button button;
    ArrayList<Map<String, String>> SavedData;
    private final int SPLASH_DISPLAY_LENGTH = 4000;
    String str,currentUser,lang;
    DatabaseReference userRef,singleTaskRef;
    Intent intent;
    CountDownTimer countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);


        textView1=(TextView)findViewById(R.id.pic1);
        textView2=(TextView)findViewById(R.id.pic2);
        textView3=(TextView)findViewById(R.id.pic3);
        textView4=(TextView)findViewById(R.id.pic4);
        textView5=(TextView)findViewById(R.id.pic5);
        textView6=(TextView)findViewById(R.id.pic6);
        textView7=(TextView)findViewById(R.id.pic7);
        textView8=(TextView)findViewById(R.id.pic8);
        textView9=(TextView)findViewById(R.id.pic9);
        textView10=(TextView)findViewById(R.id.pic10);
        textView12=(TextView)findViewById(R.id.similarirty);
        textView13=(TextView)findViewById(R.id.risklevel);

        button = (Button)findViewById(R.id.nextbtn);


        new Handler().postDelayed(new Runnable(){
            @SuppressLint("SetTextI18n")
            @Override
            public void run() {
                SavedData = Sqlitedb.Search(Results.this,"SELECT * FROM Predict_Data",3);
                System.out.println(SavedData);


                textView1.setText(SavedData.get(0).get("1"));
                textView2.setText(SavedData.get(1).get("1"));
                textView3.setText(SavedData.get(2).get("1"));
                textView4.setText(SavedData.get(3).get("1"));
                textView5.setText(SavedData.get(4).get("1"));
                textView6.setText(SavedData.get(5).get("1"));
                textView7.setText(SavedData.get(6).get("1"));
                textView8.setText(SavedData.get(7).get("1"));
                textView9.setText(SavedData.get(8).get("1"));
                textView10.setText(SavedData.get(9).get("1"));

                Integer pic1 = Integer.valueOf(textView1.getText().toString());
                Integer pic2 = Integer.valueOf(textView2.getText().toString());
                Integer pic3 = Integer.valueOf(textView3.getText().toString());
                Integer pic4 = Integer.valueOf(textView4.getText().toString());
                Integer pic5 = Integer.valueOf(textView5.getText().toString());
                Integer pic6 = Integer.valueOf(textView6.getText().toString());
                Integer pic7 = Integer.valueOf(textView7.getText().toString());
                Integer pic8 = Integer.valueOf(textView8.getText().toString());
                Integer pic9 = Integer.valueOf(textView9.getText().toString());
                Integer pic10 = Integer.valueOf(textView10.getText().toString());

                Integer cal = pic1+pic2+pic3+pic4+pic5+pic6+pic7+pic8+pic9+pic10;
                Integer total = cal / 10;
                System.out.println(total);
                textView12.setText(total+"%");

                if (total > 0 && total < 27){
                    textView13.setText("High Dementia");
                }else if(total > 27 && total < 35){
                    textView13.setText("Mid Dementia");
                }else if(total > 35 && total < 100){
                    textView13.setText("No Dementia");
                }


                GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(Results.this);
// Adding signed in user.
                FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                DatabaseReference databaseReference = firebaseDatabase.getReference();
                if(acct != null) {
                    currentUser = acct.getId();

                    userRef = databaseReference.child("users/" + currentUser);
                    singleTaskRef = databaseReference.child("ComponentBasedResults/" + currentUser + "/VisualMemory");

                    VisualMemory visualMemory = new VisualMemory(textView12.getText().toString(),textView13.getText().toString());
                    singleTaskRef.setValue(visualMemory);
                }
            }
        }, SPLASH_DISPLAY_LENGTH);


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(Results.this, MainActivity.class);
                startActivity(intent);
            }
        });

        // countdown
        countDownTimer = new CountDownTimer(5000,1000){
            @Override
            public void onTick(long l) {
                Log.d("TICK", "tick");
            }

            @Override
            public void onFinish() {
                setNextActivity(); //calling the method to set the flow according to the result
            }
        }.start();
        Toast.makeText(getApplicationContext(), "Please wait.. You will be directed to the next activity in few seconds",Toast.LENGTH_SHORT).show();
    }

    // method to set next activity
    public void setNextActivity() {
        if (textView13.getText().equals("High Dementia"))
            intent = new Intent(getApplicationContext(), Dec_making_task.class);
        else if (textView13.getText().equals("Mid Dementia"))
            intent = new Intent(getApplicationContext(), tccCalculation.class);
        else
            intent = new Intent(getApplicationContext(), AttentionInstructions.class);
        startActivity(intent);
    }
}