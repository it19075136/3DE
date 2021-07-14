package mobile.application3DE.verbalMemory;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.TextView;

import mobile.application3DE.R;

public class ResultActivity extends AppCompatActivity {
    SharedPreferences pref ;
    SharedPreferences.Editor editor;
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
        editor = pref.edit();
        String dddr = pref.getString("DR", null);
        String iiir = pref.getString("IR", null);
        TextView result1 = findViewById(R.id.result1);
        TextView result2 = findViewById(R.id.result2);
        TextView ir = findViewById(R.id.ir);
        TextView dr = findViewById(R.id.dr);
        float dd = Float.parseFloat(dddr);
        float ii = Float.parseFloat(iiir);
        if(dd*100>=40 && ii*100>=50){
            String xx = "Memory Status : Pass";
            String yy = "Risk Level : No";
            result1.setText(xx);
            result2.setText(yy);
        }
        else if(dd*100<40 && ii*100>=50){
            String xx = "Memory Status : Fail";
            String yy = "Risk Level : Moderate";
            result1.setText(xx);
            result2.setText(yy);
        }
        else if(dd*100>=40 && ii*100<50){
            String xx = "Memory Status : Pass";
            String yy = "Risk Level : Low";
            result1.setText(xx);
            result2.setText(yy);
        }
        else if(dd*100<40 && ii*100<50){
            String xx = "Memory Status : Fail";
            String yy = "Risk Level : High";
            result1.setText(xx);
            result2.setText(yy);
        }
        ir.setText("Immediate recall :"+((int) (ii*100))+"%");


        dr.setText("Delayed recall :" +((int) (dd*100))+"%");
        editor.putBoolean("notComplete",true);
        editor.commit();
    }
}