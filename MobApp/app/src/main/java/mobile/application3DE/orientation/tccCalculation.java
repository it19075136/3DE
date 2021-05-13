package mobile.application3DE.orientation;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;

import java.util.ArrayList;
import mobile.application3DE.R;

public class tccCalculation extends AppCompatActivity {

    Intent intent = null;
    Button startBtn;
    ArrayList<String> imageSet = new ArrayList<>();
    // we will get the default FirebaseDatabase instance
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

    // we will get a DatabaseReference for the database root node
    DatabaseReference databaseReference = firebaseDatabase.getReference();

    DatabaseReference getImages = databaseReference.child("TCCtestCases/test1/images");

    //  2 runs.. 6 rounds for each run. 12 distracter items and  8 target/repeating items.- 80 images.. Put 8 target items from run1 into distracter of the
    // 2 nd run , add 4 more from the distracter of run 1 and take the remaining 8 distracters from run 1 as targeted items
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.tcc_layout);

        startBtn = findViewById(R.id.btnStart);
        startBtn.setText("Please wait..");
        startBtn.setEnabled(false);

        // check if the test1 is complete **to-do
//        getImages = databaseReference.child("TCCtestCases/test2/images");

        getImages.runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                for (int i = 0; i < currentData.getChildrenCount(); i++) {
                    if(!imageSet.contains(currentData.child(String.valueOf(i)).getValue().toString()))
                        imageSet.add(currentData.child(String.valueOf(i)).getValue().toString());
                }

                    return Transaction.success(currentData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {
                startBtn.setText(R.string.start);
                startBtn.setEnabled(true);
                Snackbar.make(findViewById(android.R.id.content).getRootView(), "Selected imageset size: " + imageSet.size(), Snackbar.LENGTH_SHORT).show();
            }
        });

    }

    public void startTCC(View view) {

        intent = new Intent(this,TccImageLayout.class);
        intent.putStringArrayListExtra("imageSet", imageSet);
        startActivity(intent);

    }

}
