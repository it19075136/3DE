package mobile.application3DE.orientation;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import mobile.application3DE.R;

public class AttentionDualTaskInstruction extends AppCompatActivity {

    Intent startDual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dual_task_instruction);

    }

    public void startPage(View view) {
        startDual = new Intent(this,AttentionDualTaskTest.class);
        startDual.putExtra("singleTaskSpeechResult",getIntent().getStringExtra("singleTaskSpeechResult"));
        startActivity(startDual);
    }

}