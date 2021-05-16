package mobile.application3DE.orientation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import mobile.application3DE.R;

public class TccImageLayout extends AppCompatActivity {

    ImageView imageView;
    Button btnYes;
    TextView quest;
    Random random = new Random();
    ArrayList<String> usedInRound = new ArrayList<>();
    ArrayList<String> imageSet,targets,distractors,mergedArr;
    int counter = 0,runIdentifier = 1;
    int hits = 0,fps = 0;
    int round_counter = 0,i,n=0,m=0;
    String selectedImg, currentUser = "0d67e2b3-5a42-4663-b421-156cc9c32b83";
    SimpleDateFormat formatDate;

    DatabaseReference tccRef,userRef;
    FirebaseDatabase fb;
    //  2 runs.. 6 rounds for each run. 12 distracter items and  8 target/repeating items.- 80 images.. Put 8 target items from run1 into distracter of the
    // 2 nd run ,amd take the remaining 8 distracters from run 1 among distractors

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tcc_image_layout);

        formatDate = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

        imageView = findViewById(R.id.imgView);
        btnYes = findViewById(R.id.yesBtn);
        btnYes.setVisibility(View.INVISIBLE);
        quest = findViewById(R.id.question);
        quest.setVisibility(View.INVISIBLE);

        btnYes.setOnClickListener(v -> setResponse(selectedImg));

        fb = FirebaseDatabase.getInstance();
        tccRef = fb.getReference("ComponentBasedResults/"+currentUser+"/Orientation/TCC/");
        userRef = fb.getReference("users/"+currentUser);

        imageSet = getIntent().getStringArrayListExtra("imageSet");  // getting 80 images to the array
        runIdentifier = getIntent().getIntExtra("runIdentifier",1);

        targets = new ArrayList<>(); // targets arraylist
        distractors = new ArrayList<>(); // distractors arraylist
//        distractors2 = new ArrayList<>();
//        targets2 = new ArrayList<>();
        mergedArr = new ArrayList<>(); // arraylist per round. To merge distractors and targets

        // Setting images according to the run identidier
        switch (runIdentifier) {
            case 1:
            for (int i = 0; i < imageSet.size(); i++) {
                if (i < 8) {
                    targets.add(imageSet.get(i));
                } else {
                    distractors.add(imageSet.get(i));
                }
            }
            break;
            case 2:
                for (int i = 0; i < imageSet.size(); i++) {
                    if (i < 72) {
                        distractors.add(imageSet.get(i));
                    } else {
                        targets.add(imageSet.get(i));
                    }
                }
                break;
            default:
                break;
        }
        // RUN2
//        for (int i = 0; i < imageSet.size(); i++) {
//            if(i < 72)
//                distractors2.add(imageSet.get(i));
//            else
//                targets2.add(imageSet.get(i));
//        }

        imageView.setVisibility(View.INVISIBLE);
        // validate for each run
        for(int i = 0;i < imageSet.size(); i++) {
            Picasso.get().load(imageSet.get(i)).into(imageView);
        }

        selectedImg = targets.get(0); //getting 1st image from targets
        // setting 1st image
        Picasso.get().load(selectedImg).networkPolicy(NetworkPolicy.OFFLINE).into(imageView, new Callback() {
            @Override
            public void onSuccess() {
                    imageView.setVisibility(View.VISIBLE);
                    Snackbar.make(findViewById(android.R.id.content).getRootView(),"Starting round 1",Snackbar.LENGTH_SHORT).show();
                    usedInRound.add(selectedImg);
                    round_counter++;
                    counter++;
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            imageView.setVisibility(View.INVISIBLE);
                            setNextImage();
                        }
                    },2000);
            }

            @Override
            public void onError(Exception e) {
                Picasso.get().load(selectedImg).into(imageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        imageView.setVisibility(View.VISIBLE);
                        Snackbar.make(findViewById(android.R.id.content).getRootView(),"Starting round 1",Snackbar.LENGTH_SHORT).show();
                        usedInRound.add(selectedImg);
                        round_counter++;
                        counter++;
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                imageView.setVisibility(View.INVISIBLE);
                                setNextImage();
                            }
                        },2000);
                    }

                    @Override
                    public void onError(Exception e) {

                    }
                });
            }
        });

    }

    private void setResponse(String sImg) {

        btnYes.setEnabled(false);

            if (round_counter < 7 && round_counter > 1){
                if(targets.contains(sImg))
                    hits++; // increment hits
                else
                    fps++; // else increment false positives
            }

    }

    public void handleResults(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                imageView.setVisibility(View.INVISIBLE);
                if (counter == 20 && round_counter == 6) { //handling last image
                    if (hits != 0)
                        tccRef.child("run".concat(String.valueOf(runIdentifier))).setValue((float) fps / hits);
                    else
                        tccRef.child("run".concat(String.valueOf(runIdentifier))).setValue("Infinite"); // check this 1st

                    userRef.child("CompletedTCC1Run" + String.valueOf(runIdentifier)).setValue(formatDate.format(new Date()));

                    if (runIdentifier == 2) {
                        userRef.child("TCC1completed").setValue(formatDate.format(new Date()));
                        tccRef.child("run1").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.getValue().toString().equals("Infinite") || hits == 0)
                                    tccRef.child("TCC1Result").setValue("Infinite");
                                else
                                    tccRef.child("TCC1Result").setValue((float) (fps / hits) - (float) snapshot.getValue());
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }

                    Snackbar.make(findViewById(android.R.id.content).getRootView(), "Run " + runIdentifier + " completed with tcc hits: " + hits + " fps: " + fps, Snackbar.LENGTH_LONG).show();
                    btnYes.setVisibility(View.INVISIBLE);
                    if(runIdentifier == 1)
                        quest.setText("Run completed, please come back in 1 hour for the 2nd run");
                    else
                        quest.setText("You've completed the TCC test case");
                    round_counter++;
                    mergedArr.clear();
                }
                setNextImage();
            }
        }, 2000);
    }

    public void setNextImage() {

        if (round_counter <= 6) {

            if (counter == 20 || (counter == 1 && round_counter == 1)) { // checking start of rounds or end of rounds

                if (round_counter >= 1 && counter == 20) { // Checking for rounds after 1st round
                    mergedArr.clear(); //clearing previous image set
                    usedInRound.clear(); // clearing the used images array for the new round
                    round_counter++; // incrementing the round count
                    Snackbar.make(findViewById(android.R.id.content).getRootView(), "Starting round " + round_counter, Snackbar.LENGTH_SHORT).show();
                    counter = 0; //setting counter to 0
                    if (round_counter == 2) { // setting the buttons and question appropriately after 1st round
                        quest.setVisibility(View.VISIBLE);
                        btnYes.setVisibility(View.VISIBLE);
                    }
                }

                mergedArr.addAll(targets); //adding targets to each array
                switch (round_counter) {
                    case 1:
                        n = 60;
                        m = 6;
                        break;
                    case 2:
                        n = 48;
                        m = 5;
                        break;
                    case 3:
                        n = 36;
                        m = 4;
                        break;
                    case 4:
                        n = 24;
                        m = 3;
                        break;
                    case 5:
                        n = 12;
                        m = 2;
                        break;
                    case 6:
                        n = 0;
                        m = 1;
                        break;
                    default:
                        break;
                }
                for (int j = n; j < (12 * m); j++) {
                    mergedArr.add(distractors.get(j));
                }// removing and adding the first 12 distractors to each round.
            }

            do { // select a unique random image from the selected 20 images of the round
                i = random.nextInt(mergedArr.size());
            } while (usedInRound.contains(mergedArr.get(i)));

            selectedImg = mergedArr.get(i);

            Picasso.get().load(selectedImg).networkPolicy(NetworkPolicy.OFFLINE).into(imageView, new Callback() {
                @Override
                public void onSuccess() {
                    btnYes.setEnabled(true);
                    imageView.setVisibility(View.VISIBLE);
                    usedInRound.add(selectedImg);
                    counter++;
                    handleResults();
                }

                @Override
                public void onError(Exception e) {
                    Picasso.get().load(selectedImg).into(imageView, new Callback() {
                        @Override
                        public void onSuccess() {
                            btnYes.setEnabled(true);
                            imageView.setVisibility(View.VISIBLE);
                            usedInRound.add(selectedImg);
                            counter++;
                            handleResults();
                        }

                        @Override
                        public void onError(Exception e) {

                        }
                    });
                }
            });

        }
//        else if(round_counter <= 12){
//
//                //run 2
//                if(round_counter == 7 && counter2 == 0) {
//                btnYes.setVisibility(View.INVISIBLE);
//                quest.setVisibility(View.INVISIBLE);
////                Snackbar.make(findViewById(android.R.id.content).getRootView(), "Starting 2nd run round 1", Snackbar.LENGTH_SHORT).show();
//                Snackbar.make(findViewById(android.R.id.content).getRootView(),"Run 1 completed with hits : "+run1_hits+" fps:"+fp1,Snackbar.LENGTH_LONG).show();
//
//                if(run1_hits != 0)
//                        dbRef.child("run1Value").setValue((float)fp1/run1_hits);
//                    else
//                        dbRef.child("run1Value").setValue("infinite");
//                }
//
//            if(counter2 == 20 || counter2 == 0) {
//                if(round_counter >= 7 && counter2 == 20) {
//                    mergedArr.clear();
//                    round_counter++;
//                    Snackbar.make(findViewById(android.R.id.content).getRootView(), "Starting round " + (round_counter - 6), Snackbar.LENGTH_SHORT).show();
//                    if(round_counter == 8){
//                        quest.setVisibility(View.VISIBLE);
//                        btnYes.setVisibility(View.VISIBLE);
//                    }
//                }
//                mergedArr.addAll(targets2); //adding targets to each array
//                switch (round_counter){
//                    case 1:
//                        n = 60;
//                        m=6;
//                        break;
//                    case 2:
//                        n = 48;
//                        m=5;
//                        break;
//                    case 3:
//                        n = 36;
//                        m = 4;
//                        break;
//                    case 4:
//                        n = 24;
//                        m = 3;
//                        break;
//                    case 5:
//                        n = 12;
//                        m = 2;
//                        break;
//                    case 6:
//                        n = 0;
//                        m = 1;
//                        break;
//                    default:
//                        break;
//                }
//                for (int j = n; j < (12*m); j++) {
//                    mergedArr.add(distractors2.get(j));
//                }
//                counter2 = 0;
//                usedInRound.clear();
//            }
//            do {
//                i = random.nextInt(mergedArr.size());
//            }while (usedInRound.contains(mergedArr.get(i)));
//
//            selectedImg = mergedArr.get(i);
//            Picasso.get().load(selectedImg).networkPolicy(NetworkPolicy.OFFLINE).into(imageView, new Callback() {
//                    @Override
//                    public void onSuccess() {
//                        btnYes.setEnabled(true);
//                        imageView.setVisibility(View.VISIBLE);
//                        usedInRound.add(selectedImg);
//                        counter2++;
//
//                        if(counter2 == 20 && round_counter == 12)
//                            round_counter++;
//
//                        new Handler().postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
//                                imageView.setVisibility(View.INVISIBLE);
//                                setNextImage();
//                            }
//                        },2000);;
//                    }
//
//                    @Override
//                    public void onError(Exception e) {
//                        Picasso.get().load(selectedImg).into(imageView, new Callback() {
//                            @Override
//                            public void onSuccess() {
//                                btnYes.setEnabled(true);
//                                imageView.setVisibility(View.VISIBLE);
//                                usedInRound.add(selectedImg);
//                                counter2++;
//
//                                if(counter2 == 20 && round_counter == 12)
//                                    round_counter++;
//
//                                new Handler().postDelayed(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        imageView.setVisibility(View.INVISIBLE);
//                                        setNextImage();
//                                    }
//                                },2000);
//                            }
//
//                            @Override
//                            public void onError(Exception e) {
//
//                            }
//                        });
//                    }
//                });
//        }
//                dbRef = fb.getReference("ComponentBasedResults/Orientation/TCC/");
//                if(run2_hits != 0)
//                    dbRef.child("run2Value").setValue((float)fp2/run2_hits);
//                else
//                    dbRef.child("run2Value").setValue("infinite");
//
//                Snackbar.make(findViewById(android.R.id.content).getRootView(),"Run 2 completed with tcc hits: "+run2_hits+" fps: "+fp2,Snackbar.LENGTH_LONG).show();
//        }
    }
}