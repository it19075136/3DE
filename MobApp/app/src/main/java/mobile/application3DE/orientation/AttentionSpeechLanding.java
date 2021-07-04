package mobile.application3DE.orientation;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import mobile.application3DE.R;
import mobile.application3DE.utilities.BaseActivity;

public class AttentionSpeechLanding extends BaseActivity {

    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attention_speech_landing);
    }

    public void directToInstructions(View view) {

        intent = new Intent(this, AttentionSingleTaskLanding.class);
        intent.putExtra("choice","speech");
        startActivity(intent);
    }
}