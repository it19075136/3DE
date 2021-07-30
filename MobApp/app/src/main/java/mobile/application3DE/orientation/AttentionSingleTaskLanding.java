package mobile.application3DE.orientation;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;

import com.google.android.material.textview.MaterialTextView;

import mobile.application3DE.R;
import mobile.application3DE.utilities.BaseActivity;

public class AttentionSingleTaskLanding extends BaseActivity {

    Intent testintent;
    String choiceIdentifier;
    MaterialTextView type,instruct,note;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attention_single_task_landing);

        type = findViewById(R.id.singleTaskType);
        instruct = findViewById(R.id.singleTaskInstruct);
        note = findViewById(R.id.singleTaskNote);

        testintent = getIntent();
        choiceIdentifier = testintent.getStringExtra("choice");

        if(choiceIdentifier.equals("speech")) {
            testintent = new Intent(this, AttentionSpeechTest.class);
        }
        else {
            type.setText("Walking Task");
            instruct.setText("Keep walking until you hear a beep");
            note.setVisibility(View.INVISIBLE);
            testintent = new Intent(this, AttentionWalkingTest.class);
        }

    }

    public void directToTest(View view) {
        startActivity(testintent);
    }
}