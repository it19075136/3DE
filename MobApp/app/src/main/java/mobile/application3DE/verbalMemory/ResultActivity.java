package mobile.application3DE.verbalMemory;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import mobile.application3DE.R;
import mobile.application3DE.decisionMaking.Dec_making_task;
import mobile.application3DE.orientation.AttentionInstructions;
import mobile.application3DE.orientation.tccCalculation;
import mobile.application3DE.utilities.BaseActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ResultActivity extends BaseActivity {

    SharedPreferences pref;
    SharedPreferences.Editor editor;
    String yy;
    Intent i;
    CountDownTimer countDownTimer;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
        editor = pref.edit();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        GoogleSignInAccount acc = GoogleSignIn.getLastSignedInAccount(this);
        String dddr = pref.getString("DR", null);
        String iiir = pref.getString("IR", null);
        //TextView result1 = findViewById(R.id.result1);
        // TextView result2 = findViewById(R.id.result2);
        TextView ir = findViewById(R.id.ir);
        TextView dr = findViewById(R.id.dr);
        float dd = Float.parseFloat(dddr);
        float ii = Float.parseFloat(iiir);
        if (dd * 100 >= 40 && ii * 100 >= 50) {


            String xx = "Pass";
            yy = "No";
            //result1.setText(xx);
            //result2.setText(yy);

            mDatabase.child("ComponentBasedResults").child(acc.getId()).child("verbalMemory").child("MemoryStatus").setValue(xx);
            mDatabase.child("ComponentBasedResults").child(acc.getId()).child("verbalMemory").child("RiskLevel").setValue(yy);

        } else if (dd * 100 < 40 && ii * 100 >= 50) {
            String xx = "Fail";
            yy = "Moderate";
            //result1.setText(xx);
            //result2.setText(yy);

            mDatabase.child("ComponentBasedResults").child(acc.getId()).child("verbalMemory").child("MemoryStatus").setValue(xx);
            mDatabase.child("ComponentBasedResults").child(acc.getId()).child("verbalMemory").child("RiskLevel").setValue(yy);


        } else if (dd * 100 >= 40 && ii * 100 < 50) {
            String xx = "Pass";
            yy = "Mild";
            // result1.setText(xx);
            //result2.setText(yy);

            mDatabase.child("ComponentBasedResults").child(acc.getId()).child("verbalMemory").child("MemoryStatus").setValue(xx);
            mDatabase.child("ComponentBasedResults").child(acc.getId()).child("verbalMemory").child("RiskLevel").setValue(yy);

        } else if (dd * 100 < 40 && ii * 100 < 50) {
            String xx = "Fail";
            yy = "High";
            //result1.setText(xx);
            //result2.setText(yy);

            mDatabase.child("ComponentBasedResults").child(acc.getId()).child("verbalMemory").child("MemoryStatus").setValue(xx);
            mDatabase.child("ComponentBasedResults").child(acc.getId()).child("verbalMemory").child("RiskLevel").setValue(yy);

        }
        ir.setText(getResources().getString(R.string.immediate) + ((int) (ii * 100)) + "%");
        dr.setText(getResources().getString(R.string.delayed) + ((int) (dd * 100)) + "%");
        editor.putBoolean("notComplete", true);
        editor.commit();
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
        if (yy.equals("Moderate") || yy.equals("High"))
            i = new Intent(getApplicationContext(), Dec_making_task.class);
        else if (yy.equals("Mild"))
            i = new Intent(getApplicationContext(), tccCalculation.class);
        else
            i = new Intent(getApplicationContext(), AttentionInstructions.class);
        startActivity(i);
    }
}