package mobile.application3DE.orientation;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import mobile.application3DE.R;

public class AttentionDualTaskInstruction extends AppCompatActivity {

    Intent startDual;
    String originator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dual_task_instruction);

        originator = getIntent().getStringExtra("originator");

        if(originator.equals("walkTest"))
            startDual = new Intent(this,AttentionDualTaskTestWalkBased.class);
        else
            startDual = new Intent(this,AttentionDualTaskTest.class);
    }

    public void startPage(View view) {
        startDual.putExtra("singleTaskResult",getIntent().getStringExtra("singleTaskResult"));
        if (getIntent().getStringExtra("type") != null)
            startDual.putExtra("type","once");
        startActivity(startDual);
    }

}