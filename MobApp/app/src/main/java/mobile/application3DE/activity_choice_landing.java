package mobile.application3DE;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;

import mobile.application3DE.orientation.tccCalculation;

public class activity_choice_landing extends AppCompatActivity {

    Button memoryBtn,orientationBtn,decisionBtn,logout;
    Intent i;
    GoogleSignInClient googleSignInClient;
    private static final int TIME_INTERVAL = 2000;
    private long mBackPressed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choice_landing);

        memoryBtn = findViewById(R.id.btn2);
        orientationBtn = findViewById(R.id.btn3);
        decisionBtn = findViewById(R.id.btn1);
        logout = findViewById(R.id.logout);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, gso);

        memoryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                i = new Intent(getApplicationContext(), mobile.application3DE.LevelMusicPlayActivity.class);
                startActivity(i);
            }
        });
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                googleSignInClient.signOut()
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                i  = new Intent(view.getContext(),SignInPage.class);
                                startActivity(i);
                            }
                        });
            }
        });

    }

    @Override
    public void onBackPressed() {

        if (mBackPressed + TIME_INTERVAL > System.currentTimeMillis()) {
            finish();
            moveTaskToBack(true);
        } else {
            Toast.makeText(getBaseContext(), "Double tap back to exit", Toast.LENGTH_SHORT).show();
        }
        mBackPressed = System.currentTimeMillis();

    }

    public void directToTCC(View view) {

        i = new Intent(this, tccCalculation.class);
        startActivity(i);

    }

}