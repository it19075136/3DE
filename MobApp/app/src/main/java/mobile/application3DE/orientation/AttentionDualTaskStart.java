package mobile.application3DE.orientation;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import mobile.application3DE.R;
import mobile.application3DE.utilities.BaseActivity;

public class AttentionDualTaskStart extends BaseActivity {

    Intent dualTaskTest;
    String originator,type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attention_dual_task_start);

        originator = getIntent().getStringExtra("originator");

    }

    public void directToDualTaskTest(View view) {

        dualTaskTest = new Intent(getApplicationContext(),AttentionDualTaskInstruction.class);
        if(originator.equals("speechTest"))
            dualTaskTest.putExtra("singleTaskResult",getIntent().getStringExtra("singleTaskSpeechResult"));
        else
            dualTaskTest.putExtra("singleTaskResult", getIntent().getStringExtra("singleTaskWalkingResult"));
        dualTaskTest.putExtra("originator",originator);
        if (getIntent().getStringExtra("type") != null)
            dualTaskTest.putExtra("type","once");
        startActivity(dualTaskTest);

    }
}