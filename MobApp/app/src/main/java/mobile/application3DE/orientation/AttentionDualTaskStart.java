package mobile.application3DE.orientation;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import mobile.application3DE.R;

public class AttentionDualTaskStart extends AppCompatActivity {

    Intent dualTaskTest;
    String originator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attention_dual_task_start);

        originator = getIntent().getStringExtra("originator");

    }

    public void directToDualTaskTest(View view) {

        dualTaskTest = new Intent(getApplicationContext(),AttentionDualTaskInstruction.class);
        if(originator == "speechTest")
            dualTaskTest.putExtra("singleTaskResult",getIntent().getStringExtra("singleTaskSpeechResult"));
        else
            dualTaskTest.putExtra("singleTaskResult", getIntent().getStringExtra("singleTaskWalkingResult"));
        dualTaskTest.putExtra("originator",originator);
        startActivity(dualTaskTest);

    }
}