package mobile.application3DE.orientation;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import mobile.application3DE.R;
import mobile.application3DE.utilities.BaseActivity;

public class AttentionSingleTaskLanding extends BaseActivity {

    Intent intent;
    String choiceIdentifier;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attention_single_task_landing);

        intent = getIntent();
        choiceIdentifier = intent.getStringExtra("choice");

    }

    public void directToSingleTask(View view) {

        if(choiceIdentifier.equals("speech"))
            intent = new Intent(this, AttentionSpeechLanding.class);
        // else for walking activity
        startActivity(intent);
    }
}