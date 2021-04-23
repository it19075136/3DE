package mobile.application3DE;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.material.textfield.TextInputEditText;

import mobile.application3DE.orientation.tccCalculation;

public class activity_choice_landing extends AppCompatActivity {

    Button memoryBtn,orientationBtn,decisionBtn;
    Intent i;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choice_landing);

        memoryBtn = findViewById(R.id.btn2);
        orientationBtn = findViewById(R.id.btn3);
        decisionBtn = findViewById(R.id.btn1);

    }

    public void directToTCC(View view) {

        i = new Intent(this, tccCalculation.class);
        startActivity(i);

    }
}