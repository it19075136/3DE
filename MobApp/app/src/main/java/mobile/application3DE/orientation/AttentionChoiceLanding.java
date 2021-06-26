package mobile.application3DE.orientation;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.button.MaterialButton;

import mobile.application3DE.R;
import mobile.application3DE.utilities.BaseActivity;

public class AttentionChoiceLanding extends BaseActivity {

    MaterialButton talkingBtn,walkingBtn;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attention_choice_landing);

        findViewById(R.id.walkingBtn).setEnabled(false);
        talkingBtn = findViewById(R.id.talkingBtn);

        talkingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(getApplicationContext(), AttentionSingleTaskLanding.class);
                intent.putExtra("choice","speech");
                startActivity(intent);
            }
        });


    }
}