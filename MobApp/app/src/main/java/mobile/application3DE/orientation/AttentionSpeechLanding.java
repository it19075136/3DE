package mobile.application3DE.orientation;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import mobile.application3DE.R;
import mobile.application3DE.utilities.BaseActivity;

public class AttentionSpeechLanding extends BaseActivity {

    Intent intent;
    private static final int RECORD_PERMISSIONS_REQUEST_CODE = 15623;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attention_speech_landing);

        if (!hasRequiredPermissions(getApplicationContext()))
            requestRequiredPermissions(AttentionSpeechLanding.this);
    }

    public void directToInstructions(View view) {

        intent = new Intent(this, AttentionSingleTaskLanding.class);
        intent.putExtra("choice","speech");
        if (getIntent().getStringExtra("type") != null)
            intent.putExtra("type","once");
        if (!hasRequiredPermissions(getApplicationContext()))
            startActivity(intent);
        else {
            requestRequiredPermissions(AttentionSpeechLanding.this);
            directToInstructions(view);
        }
    }

    public void requestRequiredPermissions(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            activity.requestPermissions(
                    new String[]{
                            Manifest.permission.RECORD_AUDIO,
                            Manifest.permission.MANAGE_EXTERNAL_STORAGE
                    },
                    RECORD_PERMISSIONS_REQUEST_CODE
            );
        }
    }

    public boolean hasRequiredPermissions(Context context) {
        int recordAudioPermissionCheck = ContextCompat.checkSelfPermission(
                context, Manifest.permission.RECORD_AUDIO);
        int writeExternalStoragePermissionCheck = ContextCompat.checkSelfPermission(
                context, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int manageExternalStoragePermissionCheck = ContextCompat.checkSelfPermission(
                context, Manifest.permission.MANAGE_EXTERNAL_STORAGE);
        return recordAudioPermissionCheck == PackageManager.PERMISSION_GRANTED &&
                writeExternalStoragePermissionCheck == PackageManager.PERMISSION_GRANTED &&
                manageExternalStoragePermissionCheck == PackageManager.PERMISSION_GRANTED;
    }


}