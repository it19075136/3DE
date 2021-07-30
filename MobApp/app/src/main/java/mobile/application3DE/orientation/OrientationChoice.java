package mobile.application3DE.orientation;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;

import mobile.application3DE.R;
import mobile.application3DE.utilities.BaseActivity;

public class OrientationChoice extends BaseActivity {

    Intent intent;
    MaterialButton attentionBtn,tccBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orientation_choice);

        attentionBtn = findViewById(R.id.attentionBtn);
        tccBtn = findViewById(R.id.tccBtn);

        attentionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(getApplicationContext(),AttentionInstructions.class);
                startActivity(intent);
            }
        });

        tccBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(getApplicationContext(),tccCalculation.class);
                startActivity(intent);
            }
        });
    }
}