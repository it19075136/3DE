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

import mobile.application3DE.R;
import mobile.application3DE.utilities.BaseActivity;

public class AttentionSingleTaskLanding extends BaseActivity {

    Intent testintent;
    String choiceIdentifier;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attention_single_task_landing);

        testintent = getIntent();
        choiceIdentifier = testintent.getStringExtra("choice");

    }

    public void directToTest(View view) {

        if(choiceIdentifier.equals("speech")){
            testintent = new Intent(this, AttentionSpeechTest.class);
            startActivity(testintent);
        }

        // else for walking activity
    }
}