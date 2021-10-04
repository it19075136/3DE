package mobile.application3DE.Visual;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import mobile.application3DE.R;
import mobile.application3DE.Visual.SQLite.Sqlitedb;
import mobile.application3DE.models.VisualMemory;

import java.util.ArrayList;
import java.util.Map;

public class Results extends AppCompatActivity {


    TextView textView12;
    TextView textView13;
    Button button;
    ArrayList<Map<String, String>> SavedData;
    private final int SPLASH_DISPLAY_LENGTH = 4000;
    String str,currentUser,lang;
    DatabaseReference userRef,singleTaskRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);


        textView12=(TextView)findViewById(R.id.similarirty);
        textView13=(TextView)findViewById(R.id.risklevel);

        button = (Button)findViewById(R.id.nextbtn);


        new Handler().postDelayed(new Runnable(){
            @SuppressLint("SetTextI18n")
            @Override
            public void run() {
                SavedData = Sqlitedb.Search(Results.this,"SELECT * FROM Predict_Data",3);
                System.out.println(SavedData);
                

                Integer pic1 = Integer.valueOf(SavedData.get(0).get("1"));
                Integer pic2 = Integer.valueOf(SavedData.get(1).get("1"));
                Integer pic3 = Integer.valueOf(SavedData.get(2).get("1"));
                Integer pic4 = Integer.valueOf(SavedData.get(3).get("1"));
                Integer pic5 = Integer.valueOf(SavedData.get(4).get("1"));
                Integer pic6 = Integer.valueOf(SavedData.get(5).get("1"));
                Integer pic7 = Integer.valueOf(SavedData.get(6).get("1"));
                Integer pic8 = Integer.valueOf(SavedData.get(7).get("1"));
                Integer pic9 = Integer.valueOf(SavedData.get(8).get("1"));
                Integer pic10 = Integer.valueOf(SavedData.get(9).get("1"));

                Integer cal = pic1+pic2+pic3+pic4+pic5+pic6+pic7+pic8+pic9+pic10;
                Integer total = cal / 10;
                System.out.println(total);
                textView12.setText(total+"%");

                if (total > 0 && total < 27){
                    textView13.setText("High Dementia");
                }else if(total > 27 && total < 35){
                    textView13.setText("Mid Dementia");
                }else if(total > 35 && total < 100){
                    textView13.setText("No Dementia");
                }


                GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(Results.this);
// Adding signed in user.
                FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                DatabaseReference databaseReference = firebaseDatabase.getReference();
                if(acct != null) {
                    currentUser = acct.getId();

                    userRef = databaseReference.child("users/" + currentUser);
                    singleTaskRef = databaseReference.child("ComponentBasedResults/" + currentUser + "/VisualMemory");

                    VisualMemory visualMemory = new VisualMemory(textView12.getText().toString(),textView13.getText().toString());
                    singleTaskRef.setValue(visualMemory);
                }
            }
        }, SPLASH_DISPLAY_LENGTH);


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Results.this, MainActivity.class);
                startActivity(intent);
            }
        });

    }
}