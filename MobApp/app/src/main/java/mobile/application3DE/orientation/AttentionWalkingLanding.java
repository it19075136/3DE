package mobile.application3DE.orientation;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

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
        intent.putExtra("choice", "walking");

        if ((ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_DENIED))
        Toast.makeText(AttentionWalkingLanding.this, "You need to provide permission to perform this activity", Toast.LENGTH_SHORT).show();

        while (ContextCompat.checkSelfPermission(this,
            Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_DENIED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACTIVITY_RECOGNITION}, 100);
            }
        }
        if (getIntent().getStringExtra("type") != null)
            intent.putExtra("type","once");
        startActivity(intent);
    }
}