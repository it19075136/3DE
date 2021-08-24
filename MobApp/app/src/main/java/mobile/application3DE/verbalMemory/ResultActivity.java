package mobile.application3DE.verbalMemory;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import mobile.application3DE.R;
import mobile.application3DE.utilities.BaseActivity;

public class ResultActivity extends BaseActivity {
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
            String xx = getResources().getString(R.string.memorystuts_pass);
            String yy = getResources().getString(R.string.risklevel_no);
            result1.setText(xx);
            result2.setText(yy);
        }
        else if(dd*100<40 && ii*100>=50){
            String xx = getResources().getString(R.string.memorystuts_fail);
            String yy = getResources().getString(R.string.risklevel_medium);
            result1.setText(xx);
            result2.setText(yy);
        }
        else if(dd*100>=40 && ii*100<50){
            String xx = getResources().getString(R.string.memorystuts_pass);
            String yy = getResources().getString(R.string.risklevel_low);
            result1.setText(xx);
            result2.setText(yy);
        }
        else if(dd*100<40 && ii*100<50){
            String xx = getResources().getString(R.string.memorystuts_fail);
            String yy = getResources().getString(R.string.risklevel_high);
            result1.setText(xx);
            result2.setText(yy);
        }
        ir.setText(getResources().getString(R.string.immediate)+((int) (ii*100))+"%");
        dr.setText(getResources().getString(R.string.delayed) +((int) (dd*100))+"%");
        editor.putBoolean("notComplete",true);
        editor.commit();
    }
}