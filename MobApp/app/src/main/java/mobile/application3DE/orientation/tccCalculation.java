package mobile.application3DE.orientation;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import mobile.application3DE.R;

public class tccCalculation extends AppCompatActivity {

    Intent i;
    int imgCount = 0;
    ArrayList<String> imageSet = new ArrayList<>();
    // we will get the default FirebaseDatabase instance
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

    // we will get a DatabaseReference for the database root node
    DatabaseReference databaseReference = firebaseDatabase.getReference();

    DatabaseReference getImages = databaseReference.child("TCCtestCases/test1/images");
    DatabaseReference getImage;

    //  2 runs.. 6 rounds for each run. 12 distracter items and  8 target/repeating items.- 80 images.. Put 8 target items from run1 into distracter of the
    // 2 nd run , add 4 more from the distracter of run 1 and take the remaining 8 distracters from run 1 as targeted items
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tcc_layout);

        // check if the test1 is complete **to-do
//        getImages = databaseReference.child("TCCtestCases/test2/images");

        Thread thread = new Thread(){
            @Override
            public void run() {
                for (int i = 0; i < 80; i++) {
                    getImage = getImages.child(String.valueOf(i));
                    getImage.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            imageSet.add(snapshot.getValue(String.class));
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                }

            }
        };

        thread.start();

        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void startTCC(View view) {

        Thread thread1 = new Thread() {
            @Override
            public void run() {
                i = new Intent(view.getContext(),TccImageLayout .class);
                i.putStringArrayListExtra("imageSet",imageSet);
                startActivity(i);
            }
        };

        thread1.start();

    }
}
