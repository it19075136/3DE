package mobile.application3DE;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import mobile.application3DE.orientation.tccCalculation;

public class activity_choice_landing extends AppCompatActivity {

    Button memoryBtn,orientationBtn,decisionBtn,logout;
    Intent i;
    GoogleSignInClient googleSignInClient;

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

    public void directToTCC(View view) {

        i = new Intent(this, tccCalculation.class);
        startActivity(i);

    }

}