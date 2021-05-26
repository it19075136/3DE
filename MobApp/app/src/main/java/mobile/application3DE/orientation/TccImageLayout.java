package mobile.application3DE.orientation;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import mobile.application3DE.R;

public class TccImageLayout extends AppCompatActivity {

    ImageView imageView;
    Button btnYes,btnNo;
    TextView quest;
    Random random = new Random();
    ArrayList<String> usedInRound = new ArrayList<>();
    ArrayList<String> imageSet,targets,distractors,mergedArr;
    int counter = 0,runIdentifier = 1;
    int hits = 0,fps = 0;
    int round_counter = 0,i,n=0,m=0;
    String selectedImg, currentUser = "0e8f4183-cb31-4584-b870-e7869d93a46e";
    SimpleDateFormat formatDate;

    //  2 runs.. 6 rounds for each run. 12 distracter items and  8 target/repeating items.- 80 images.. Put 8 target items from run1 into distracter of the
    // 2 nd run ,amd take the remaining 8 distracters from run 1 among distractors

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tcc_image_layout);

        imageView = findViewById(R.id.imgView);
//        progressIndicator = findViewById(R.id.progress_circular);
        btnYes = findViewById(R.id.yesBtn);
        btnYes.setVisibility(View.INVISIBLE);
        btnNo = findViewById(R.id.noBtn);
        btnNo.setVisibility(View.INVISIBLE);
        quest = findViewById(R.id.question);
        quest.setVisibility(View.INVISIBLE);

        btnYes.setOnClickListener(v -> setResponse(selectedImg, "yes"));

        btnNo.setOnClickListener(v -> setResponse(selectedImg, "no"));

        fb = FirebaseDatabase.getInstance();
        tccRef = fb.getReference("ComponentBasedResults/"+currentUser+"/Orientation/TCC/");
        userRef = fb.getReference("users/"+currentUser);

        imageSet = getIntent().getStringArrayListExtra("imageSet");  // getting 80 images to the array
        targets = new ArrayList<>(); // targets arraylist
        distractors = new ArrayList<>(); // distractors arraylist
        distractors2 = new ArrayList<>();

        mergedArr = new ArrayList<>(); // arraylist per round. To merge distractors and targets

        Toast.makeText(getApplicationContext(),"Images count: "+imageSet.size(),Toast.LENGTH_SHORT).show();
        // RUN1
        for (int i = 0; i < imageSet.size(); i++) {
            if(i < 8)
                targets.add(imageSet.get(i));
            else
                distractors.add(imageSet.get(i));
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
                Log.d("ERROR","No image");
            }
        });

    }

    private void setResponse(String sImg,String res) {

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

            setNextImage();

    }

    public void setNextImage(int sImg,String res){

            if(round_counter <= 6){ //1
            // run 1
                // check target hits
                if(res.equals("yes") && sImg < 8 && round_counter > 1)
                    run1_hits++; // increment

                    Snackbar.make(findViewById(android.R.id.content).getRootView(), "Run " + runIdentifier + " completed with tcc hits: " + hits + " fps: " + fps, Snackbar.LENGTH_LONG).show();
                    btnYes.setVisibility(View.INVISIBLE);
                    btnNo.setVisibility(View.INVISIBLE);
                    quest.setVisibility(View.VISIBLE);
                    if(runIdentifier == 1)
                        quest.setText("Run completed, please come back in 1 hour for the 2nd run");
                    else
                        quest.setText("You've completed the TCC test case");
                    round_counter++;
                    mergedArr.clear();
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
                    counter = 0; //setting counter to 0
                }

                }
                else
                    counter++; //1
                targets = sortedImgs.get("targets");
                mergedArr.addAll(targets); //adding target items
                for (int j = 0; j < 12; j++)
                    mergedArr.add(distractors.remove(j)); // removing and adding the first 12 distractors to each round
//                    Toast.makeText(getApplicationContext(),"Size"+mergedArr.size(),Toast.LENGTH_SHORT).show();
                    Toast.makeText(getApplicationContext(),"distractors Size "+distractors.size()+"merged size "+mergedArr.size(),Toast.LENGTH_SHORT).show();
                    round_counter++;
                }
//                Toast.makeText(getApplicationContext(),"before loop",Toast.LENGTH_SHORT).show();
                do {
                i = random.nextInt(mergedArr.size());
            }while (usedInRound.contains(i));
//                Toast.makeText(getApplicationContext(),"SET"+i,Toast.LENGTH_SHORT).show();
                Picasso.get().load(mergedArr.get(i)).into(imageView);
            selectedImg = i;
            usedInRound.add(i);
            counter++;
            }
        else if(round_counter <= 12){
            Snackbar.make(findViewById(android.R.id.content).getRootView(),"Starting 2nd run",Snackbar.LENGTH_SHORT).show();
            //run 2
                //check target hits
                if(res.equals("yes") && sImg < 8)
                    run2_hits++; // increment

            if(counter2 == 19 || counter2 == 0) {
                mergedArr.clear();
                Snackbar.make(findViewById(android.R.id.content).getRootView(),"Starting round "+ (round_counter-6),Snackbar.LENGTH_SHORT).show();
                targets = sortedImgs.get("targets2");
                mergedArr.addAll(targets); //adding target items
                for (int j = 0; j < 12; j++)
                    mergedArr.add(distractors2.remove(j)); // removing and adding the first 12 distractors to each round
                Toast.makeText(getApplicationContext(),"Array Size "+mergedArr.size(),Toast.LENGTH_SHORT).show();
                counter2 = 0;
                usedInRound.clear();
                round_counter++;
            }
            do {
                i = random.nextInt(mergedArr.size());
            } while (usedInRound.contains(mergedArr.get(i)));

            selectedImg = mergedArr.get(i);

            Picasso.get().load(selectedImg).memoryPolicy(MemoryPolicy.NO_CACHE,MemoryPolicy.NO_STORE).into(imageView, new Callback() {
                @Override
                public void onSuccess() {
                    if(round_counter != 1) {
                        quest.setVisibility(View.VISIBLE);
                        btnYes.setEnabled(true);
                        btnNo.setEnabled(true);
                        btnYes.setVisibility(View.VISIBLE);
                        btnNo.setVisibility(View.VISIBLE);
                    }
                    imageView.setVisibility(View.VISIBLE);
                    usedInRound.add(selectedImg);
                    counter++;
                    handleResults();
                }

                @Override
                public void onError(Exception e) {
                        Log.d("ERROR","No image");

                }
            });

        }
        else{
            Snackbar.make(findViewById(android.R.id.content).getRootView(),"Test completed with results run 1 hits: "+run1_hits+", run 2 hits: "+run2_hits+", Come back in one hour to continue with 2nd test",Snackbar.LENGTH_LONG).show();
        }

    }

}