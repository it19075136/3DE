package mobile.application3DE;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import mobile.application3DE.models.User;
import mobile.application3DE.utilities.BaseActivity;

public class FinalResultActivity extends BaseActivity {

    // we will get the default FirebaseDatabase instance
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

    // we will get a DatabaseReference for the database root node
    DatabaseReference databaseReference = firebaseDatabase.getReference();

    DatabaseReference finalResultRef;
    SimpleDateFormat formatDate;
    String currentUser;
    MaterialTextView emails,result,date,user;
    HashMap<String,String> u;
    MaterialButton btnHome;

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_final_result);

        user = findViewById(R.id.user);
        date = findViewById(R.id.date);
        result = findViewById(R.id.result);
        emails = findViewById(R.id.emails);
        btnHome = findViewById(R.id.btnHome);
        btnHome.setEnabled(false);

        u = (HashMap<String, String>) getIntent().getSerializableExtra("user");

        formatDate = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

        user.setText(getString(R.string.resultsFor) + " " + u.get("_fName") + " " +u.get("_lName"));
        date.setText(getString(R.string.examDate) + formatDate.format(new Date()));
        result.setText(getString(R.string.demetiaStatus)+getIntent().getStringExtra("result"));
        emails.setText(getString(R.string.resultsSent)+"\n"+ u.get("email") + ",\n" +u.get("guardianMail"));

        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
// Adding signed in user.

        if(acct != null)
            currentUser = acct.getId();

        finalResultRef = databaseReference.child("FinalResults/"+currentUser);

            finalResultRef.child(formatDate.format(new Date())).setValue(getIntent().getStringExtra("result")).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Toast.makeText(getApplicationContext(), "You've successfully completed the test!", Toast.LENGTH_LONG).show();
                    btnHome.setEnabled(true);
                }

            });

    }

    public void navigateHome(View view) {
        Intent intent = new Intent(this, StartFullTest.class);
        startActivity(intent);
    }

}