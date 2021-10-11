package mobile.application3DE;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.auth.api.signin.GoogleSignInClient;

import mobile.application3DE.decisionMaking.Dec_making_task;
import mobile.application3DE.orientation.OrientationChoice;

public class ChoiceLandingLegacy extends AppCompatActivity {

    Button decisionBtn,visualMemoryBtn,orientationBtn,VerbalBtn;
    Intent i;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choice_landing_legacy);

        VerbalBtn = findViewById(R.id.btn2);
        visualMemoryBtn = findViewById(R.id.btn4);
        orientationBtn = findViewById(R.id.btn3);
        decisionBtn = findViewById(R.id.btn1);


        VerbalBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                i = new Intent(getApplicationContext(), mobile.application3DE.verbalMemory.LevelMusicPlayActivity.class);
                startActivity(i);
            }
        });
        visualMemoryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                i = new Intent(getApplicationContext(), mobile.application3DE.Visual.MainActivity.class);
                startActivity(i);
            }
        });

        decisionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                i = new Intent(getApplicationContext(), Dec_making_task.class);
                startActivity(i);
            }
        });

    }

    public void directToOrientation(View view) {

        i = new Intent(this, OrientationChoice.class);
        startActivity(i);

    }
}