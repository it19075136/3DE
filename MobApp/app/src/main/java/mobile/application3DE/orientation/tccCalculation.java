package mobile.application3DE.orientation;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import mobile.application3DE.R;

public class tccCalculation extends AppCompatActivity {

    Intent i;
    ArrayList<String> imageSet = new ArrayList<>();
    ArrayList<String> rptImageSet = new ArrayList<>();
    // we will get the default FirebaseDatabase instance
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

    // we will get a DatabaseReference for the database root node
    DatabaseReference databaseReference = firebaseDatabase.getReference();

    // Here "image" is the child node value we are getting
    // child node data in the getImage variable
    DatabaseReference getImages = databaseReference.child("TCCtestCases/test1/images"); // change this path when you have more tests
    DatabaseReference getImage,getRptImages;

    //  2 runs.. 6 rounds for each run. 12 distracter items and  8 target/repeating items.- 80 images.. Put 8 target items from run1 into distracter of the
    // 2 nd run , add 4 more from the distracter of run 1 and take the remaining 8 distracters from run 1 as targeted items
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tcc_layout);


        Thread thread = new Thread(){
            @Override
            public void run() {
                for (int i = 1; i < 5; i++) {
                    getImage = getImages.child("test".concat(String.valueOf(i)));
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

                // loop when you add more images

                getRptImages = getImages.child("rptImages/test");
                getRptImages.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        rptImageSet.add(snapshot.getValue(String.class));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
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
                i.putStringArrayListExtra("rptImageSet",rptImageSet);
                startActivity(i);
            }
        };

        thread1.start();

    }
}
