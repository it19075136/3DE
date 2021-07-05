package mobile.application3DE.orientation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import mobile.application3DE.R;
import mobile.application3DE.utilities.BaseActivity;

public class TccImageLayout extends BaseActivity {

    ImageView imageView;
    Button btnYes,btnNo;
    TextView quest,RoundHeader,fistRdHd;
    Random random = new Random();
    ArrayList<String> usedInRound = new ArrayList<>();
    ArrayList<String> imageSet,targets,distractors,mergedArr;
    int counter = 0,runIdentifier = 1;
    int hits = 0,fps = 0;
    int round_counter = 0,i,n=0,m=0;
    String selectedImg, currentUser = "";
    SimpleDateFormat formatDate;
    Intent intent;

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
        btnYes.setEnabled(false);
        btnNo = findViewById(R.id.noBtn);
        btnNo.setVisibility(View.INVISIBLE);
        RoundHeader = findViewById(R.id.roundHeader);
        RoundHeader.setVisibility(View.INVISIBLE);
        fistRdHd = findViewById(R.id.firstRdHd);
        btnNo.setEnabled(false);
        quest = findViewById(R.id.question);
        quest.setVisibility(View.INVISIBLE);

        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
        
// Adding signed in user.
        if(acct != null)
            currentUser = acct.getId();

        btnYes.setOnClickListener(v -> setResponse(selectedImg, "yes"));

        btnNo.setOnClickListener(v -> setResponse(selectedImg, "no"));

        fb = FirebaseDatabase.getInstance();
        tccRef = fb.getReference("ComponentBasedResults/"+currentUser+"/Orientation/TCC");
        userRef = fb.getReference("users/"+currentUser);

        imageSet = getIntent().getStringArrayListExtra("imageSet");  // getting 80 images to the array
        runIdentifier = getIntent().getIntExtra("runIdentifier",1);

        targets = new ArrayList<>(); // targets arraylist
        distractors = new ArrayList<>(); // distractors arraylist

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

        imageView.setVisibility(View.INVISIBLE);
        selectedImg = targets.get(0); //getting 1st image from targets
        // setting 1st image
        Picasso.get().load(selectedImg).memoryPolicy(MemoryPolicy.NO_CACHE,MemoryPolicy.NO_STORE).into(imageView, new Callback() {
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
                            if(round_counter == 1)
                                setNextImage();
                        }
                    },2000);
            }

            @Override
            public void onError(Exception e) {
                Snackbar.make(findViewById(android.R.id.content).getRootView(), "Couldn't fetch the image, trying again...", Snackbar.LENGTH_LONG).show();
                Picasso.get().load(selectedImg).memoryPolicy(MemoryPolicy.NO_CACHE,MemoryPolicy.NO_STORE).into(imageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        imageView.setVisibility(View.VISIBLE);
                        Snackbar.make(findViewById(android.R.id.content).getRootView(), "Starting round 1", Snackbar.LENGTH_SHORT).show();
                        usedInRound.add(selectedImg);
                        round_counter++;
                        counter++;
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                imageView.setVisibility(View.INVISIBLE);
                                if (round_counter == 1)
                                    setNextImage();
                            }
                        }, 2000);
                    }

                    @Override
                    public void onError(Exception e) {
                        Log.d("ERROR","No image: " + selectedImg);
                        Snackbar.make(findViewById(android.R.id.content).getRootView(), "Couldn't fetch the image, please check your network connection and restart", Snackbar.LENGTH_LONG).show();
                    }
                });
            }
        });

    }

    @Override
    public void onBackPressed() {
        Snackbar.make(findViewById(android.R.id.content).getRootView(), "Can't go back while the activity is in progress", Snackbar.LENGTH_SHORT).show();
    }

    private void setResponse(String sImg, String res) {

        btnYes.setEnabled(false);
        btnNo.setEnabled(false);
        btnYes.setVisibility(View.INVISIBLE);
        btnNo.setVisibility(View.INVISIBLE);
        quest.setVisibility(View.INVISIBLE);


        if (round_counter < 7 && round_counter > 1 && res.equals("yes")){
                if(targets.contains(sImg))
                    hits++; // increment hits
                else
                    fps++; // else increment false positives
            }

        if (counter == 20 && round_counter == 6) { //handling last image
            if (hits != 0)
                tccRef.child("run".concat(String.valueOf(runIdentifier))).setValue((double) fps / hits);
            else
                tccRef.child("run".concat(String.valueOf(runIdentifier))).setValue("Infinite"); // check this 1st

            userRef.child("CompletedTCC1Run" + String.valueOf(runIdentifier)).setValue(formatDate.format(new Date()));

            final String[] tccValue = new String[1];

            if (runIdentifier == 2) {
                userRef.child("TCC1completed").setValue(formatDate.format(new Date()));
                tccRef.child("run1").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Log.d("IN IN", snapshot.getValue().toString());
                        if (snapshot.getValue().toString().equals("Infinite") || hits == 0) {
                            Log.d("IN IF", snapshot.getValue().toString());
                            tccValue[0] = "Infinite";
                            tccRef.child("TCC1Result").setValue(tccValue[0]);
                        }
                        else {
                            Log.d("IN else", snapshot.getValue().toString());
                            tccValue[0] = String.valueOf(((double) fps / hits) - (double) snapshot.getValue());
                            tccRef.child("TCC1Result").setValue(((double) fps / hits) - (double) snapshot.getValue());
                        }
                        btnYes.setVisibility(View.INVISIBLE);
                        btnNo.setVisibility(View.INVISIBLE);
                        quest.setVisibility(View.VISIBLE);
                        RoundHeader.setVisibility(View.INVISIBLE);
                        Toast.makeText(getApplicationContext(), "Run " + runIdentifier + " completed with tcc hits: " + hits + " ,false positives: " + fps + "Your tcc value is "+ tccValue[0], Toast.LENGTH_LONG).show();
                        quest.setText("You've completed the TCC test case, your tcc value is "+tccValue[0]);
                        round_counter++;
                        mergedArr.clear();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
            else{
                btnYes.setVisibility(View.INVISIBLE);
                btnNo.setVisibility(View.INVISIBLE);
                quest.setVisibility(View.VISIBLE);
                Toast.makeText(getApplicationContext(), "Run " + runIdentifier + " completed with tcc hits: " + hits + " ,false positives: " + fps, Toast.LENGTH_LONG).show();
                quest.setText("Run completed, please come back in 1 hour for the 2nd run.");
                round_counter++;
                mergedArr.clear();
            }
        }
        else
            setNextImage();

    }

    public void handleLoadedImage(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(round_counter != 1) {
                    imageView.setVisibility(View.INVISIBLE);
                    btnYes.setEnabled(true);
                    btnNo.setEnabled(true);
                }
                if(round_counter == 1)
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
                    RoundHeader.setVisibility(View.VISIBLE);
                    RoundHeader.setText("Round " + round_counter);
                    counter = 0; //setting counter to 0
                }

                mergedArr.addAll(targets); //adding targets to each array
                switch (round_counter) {
                    case 1:
                        n = 60;
                        m = 6;
                        break;
                    case 2:
                        fistRdHd.setVisibility(View.INVISIBLE);
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

            Picasso.get().load(selectedImg).memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE).into(imageView, new Callback() {
                @Override
                public void onSuccess() {
                    if (round_counter != 1) {
                        quest.setVisibility(View.VISIBLE);
                        btnYes.setVisibility(View.VISIBLE);
                        btnNo.setVisibility(View.VISIBLE);
                    }
                    imageView.setVisibility(View.VISIBLE);
                    usedInRound.add(selectedImg);
                    counter++;
                    handleLoadedImage();
                }

                @Override
                public void onError(Exception e) {
                    Snackbar.make(findViewById(android.R.id.content).getRootView(), "Couldn't fetch the image, trying again", Snackbar.LENGTH_LONG).show();
                    Picasso.get().load(selectedImg).memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE).into(imageView, new Callback() {
                        @Override
                        public void onSuccess() {
                            if (round_counter != 1) {
                                quest.setVisibility(View.VISIBLE);
                                btnYes.setVisibility(View.VISIBLE);
                                btnNo.setVisibility(View.VISIBLE);
                            }
                            imageView.setVisibility(View.VISIBLE);
                            usedInRound.add(selectedImg);
                            counter++;
                            handleLoadedImage();
                        }

                        @Override
                        public void onError(Exception e) {
                            Log.d("ERROR", "No image: " + selectedImg);
                            Snackbar.make(findViewById(android.R.id.content).getRootView(), "Couldn't fetch the image, please check your network connection and restart", Snackbar.LENGTH_LONG).show();
                        }
                    });
                }
            });

        }
    }

    public void navigateBack(View view) {
        intent = new Intent(this,OrientationChoice.class);
        startActivity(intent);
    }
}