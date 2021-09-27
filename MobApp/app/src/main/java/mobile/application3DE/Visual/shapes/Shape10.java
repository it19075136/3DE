package mobile.application3DE.Visual.shapes;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import mobile.application3DE.R;
import mobile.application3DE.Visual.Third;

public class Shape10 extends AppCompatActivity {

    TextView textView;
    ImageView imageView;
    Integer data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shape10);

        textView=findViewById(R.id.seconds);
        imageView=findViewById(R.id.imageView);

        data = getIntent().getExtras().getInt("correctCount");
        System.out.println("Wtest"+data);

        new CountDownTimer(5000, 1000) {

            public void onTick(long millisUntilFinished) {
                textView.setText("seconds remaining: " + millisUntilFinished / 1000);
            }

            public void onFinish() {
                Intent intent = new Intent(Shape10.this,Third.class);
                intent.putExtra("imgCount", data+1);
                startActivity(intent);
            }
        }.start();
    }
}