package mobile.application3DE.orientation;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import mobile.application3DE.R;

public class AttentionDualTaskStart extends AppCompatActivity {

    Intent dualTaskTest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attention_dual_task_start);
    }

    public void directToDualTaskTest(View view) {

        dualTaskTest = new Intent(getApplicationContext(),AttentionDualTaskInstruction.class);
        dualTaskTest.putExtra("singleTaskSpeechResult",getIntent().getStringExtra("singleTaskSpeechResult"));
        startActivity(dualTaskTest);

    }
}