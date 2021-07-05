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
            NotificationManager notificationManager =
                    (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N
                    && !notificationManager.isNotificationPolicyAccessGranted()) {
                Intent intent = new Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
                this.startActivityForResult(intent,100);
            }
            else
                startActivity(testintent);
        }

        // else for walking activity
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 100)
            startActivity(testintent);
    }
}