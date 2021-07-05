package mobile.application3DE.orientation;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import mobile.application3DE.R;
import mobile.application3DE.utilities.BaseActivity;

public class AttentionInstructions extends BaseActivity {

    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attention_instructions);
    }

    public void startAttention(View view) {
        intent = new Intent(this,AttentionChoiceLanding.class);
        startActivity(intent);
    }
}