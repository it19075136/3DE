package mobile.application3DE.orientation;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
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
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

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

    DatabaseReference userRef;
    String currentUser = "0e8f4183-cb31-4584-b870-e7869d93a46e";
    Date run1Date = null;
    int runIdentifier = 1;
    boolean execute = true;

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
        userRef = databaseReference.child("users/"+currentUser);

        userRef.child("TCC1completed").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    Snackbar.make(findViewById(android.R.id.content).getRootView(), "You have completed tcc test case 1", Snackbar.LENGTH_LONG).show();
                    startBtn.setText("Completed");
                    startBtn.setEnabled(false);
                    execute = false;
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        SimpleDateFormat dateFormat=new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        userRef.child("CompletedTCC1Run1").get().addOnCompleteListener(task -> {
            try {
                if(task.getResult().getValue() != null) {
                    run1Date = dateFormat.parse(task.getResult().getValue().toString());
                    runIdentifier = 2;
                }

            } catch (ParseException e) {
                e.printStackTrace();
            }
            if (run1Date != null){
                try {
                    if (((dateFormat.parse(dateFormat.format(new Date())).getTime() - (run1Date.getTime()))/(1000*60*60))%24 < 1){
                        Snackbar.make(findViewById(android.R.id.content).getRootView(), "Make sure you start this run after 1 hour from the completion of 1st run", Snackbar.LENGTH_LONG).show();
                        startBtn.setText("Not allowed");
                        startBtn.setEnabled(false);
                        execute = false;
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            if(execute) {
                getImages.runTransaction(new Transaction.Handler() {
                    @NonNull
                    @Override
                    public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                        for (int i = 0; i < currentData.getChildrenCount(); i++) {
                            if (!imageSet.contains(currentData.child(String.valueOf(i)).getValue().toString()))
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
        });

    }

    public void startTCC(View view) {

        intent = new Intent(this,TccImageLayout.class);
        intent.putStringArrayListExtra("imageSet", imageSet);
        intent.putExtra("runIdentifier",runIdentifier);
        startActivity(intent);

    }

}
