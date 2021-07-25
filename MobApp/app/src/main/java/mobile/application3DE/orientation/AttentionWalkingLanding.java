package mobile.application3DE.orientation;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;

import mobile.application3DE.R;
import mobile.application3DE.utilities.BaseActivity;

public class AttentionWalkingLanding extends BaseActivity {

    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attention_walking_landing);

    }

    public void directToInstructions(View view) {

            intent = new Intent(this, AttentionSingleTaskLanding.class);
            intent.putExtra("choice","walking");
            startActivity(intent);
    }
}