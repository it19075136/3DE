package mobile.application3DE.verbalMemory;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.widget.TextView;

import mobile.application3DE.R;
import mobile.application3DE.utilities.BaseActivity;

public class WaitingActivity extends BaseActivity {
    Handler handler;
    int Seconds, Minutes, MilliSeconds,Hours ;
    long MillisecondTime, StartTime, TimeBuff, UpdateTime = 0L ;
    Long sstime;
    TextView start;
    SharedPreferences pref ;
    SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiting);
        pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
        editor = pref.edit();
        sstime = SystemClock.uptimeMillis();
        handler = new Handler();
        handler.postDelayed(runnable, 0);
        start = findViewById(R.id.time);
    }
    public Runnable runnable = new Runnable() {

        public void run() {

            MillisecondTime = SystemClock.uptimeMillis() - StartTime;

            UpdateTime = sstime + 6*1000 - MillisecondTime;

            Seconds = (int) (UpdateTime / 1000);

            Minutes = Seconds / 60;
            Hours = Minutes / 60;
            Seconds = Seconds % 60;

            MilliSeconds = (int) (UpdateTime % 1000);

            start.setText("" +  String.format("%02d", Minutes) + ":"
                    + String.format("%02d", Seconds));

            handler.postDelayed(this, 0);
            if(UpdateTime<0){
                editor.putBoolean("notComplete",false);
                editor.commit();
                handler.removeCallbacks(runnable);
                if(pref.getBoolean("sinhala",false)){
                    Intent i1 = new Intent(getApplicationContext(), SinhalaTestActivity.class);
                    startActivity(i1);
                }else{
                    Intent i1 = new Intent(getApplicationContext(), SpeechTestActivity.class);
                    startActivity(i1);
                }

            }
        }

    };
}