package mobile.application3DE.orientation;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
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
    ArrayList<String> imageSet,targets,targets2,distractors,distractors2,mergedArr;
    int counter = 0,counter2 = 0;
    int run1_hits = 0,run2_hits = 0;
    int round_counter = 0,i,n=0,m=0;
    String selectedImg;

    //  2 runs.. 6 rounds for each run. 12 distracter items and  8 target/repeating items.- 80 images.. Put 8 target items from run1 into distracter of the
    // 2 nd run ,amd take the remaining 8 distracters from run 1 among distractors

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tcc_image_layout);

        imageView = findViewById(R.id.imgView);
        btnYes = findViewById(R.id.yesBtn);
        btnYes.setText(R.string.next);
        btnNo = findViewById(R.id.noBtn);
        btnNo.setVisibility(View.INVISIBLE);
        quest = findViewById(R.id.question);
        quest.setVisibility(View.INVISIBLE);

        imageSet = getIntent().getStringArrayListExtra("imageSet");  // getting 80 images to the array

        targets = new ArrayList<>(); // targets arraylist
        distractors = new ArrayList<>(); // distractors arraylist

        distractors2 = new ArrayList<>();
        targets2 = new ArrayList<>();

        mergedArr = new ArrayList<>(); // arraylist per round. To merge distractors and targets

        // RUN1
        for (int i = 0; i < imageSet.size(); i++) {
            if(i < 8) {
                targets.add(imageSet.get(i));
            }
            else {
                distractors.add(imageSet.get(i));
            }
        }

        // RUN2
        for (int i = 0; i < imageSet.size(); i++) {
            if(i < 72)
                distractors2.add(imageSet.get(i));
            else
                targets2.add(imageSet.get(i));
        }

        selectedImg = targets.get(0); //getting 1st run's 1st image from targets
        Picasso.get().load(selectedImg).into(imageView); //setting 1st run's 1st image from targets
        Snackbar.make(findViewById(android.R.id.content).getRootView(),"Starting round 1",Snackbar.LENGTH_SHORT).show();
        usedInRound.add(selectedImg);
        round_counter++;
        counter++;

        btnYes.setOnClickListener(v -> setNextImage(selectedImg,"yes"));

        btnNo.setOnClickListener(v -> setNextImage(selectedImg,"no"));

    }

    public void setNextImage(String sImg,String res){

            if(round_counter <= 6){
            // run 1
                // check target hits
                if(res.equals("yes") && targets.contains(sImg) && round_counter > 1)
                    run1_hits++; // increment

                if(counter == 20 || (counter == 1 && round_counter == 1)) { // checking start of rounds or end of rounds

                    if(round_counter >= 1 && counter == 20) { // Checking for rounds after 1st round
                        mergedArr.clear(); //clearing previous image set
                        usedInRound.clear(); // clearing the used images array for the new round
                        round_counter++; // incrementing the round count
                        Snackbar.make(findViewById(android.R.id.content).getRootView(), "Starting round " + round_counter, Snackbar.LENGTH_SHORT).show();
                        counter = 0; //setting counter to 0
                        if (round_counter == 2) { // setting the buttons and question appropriately after 1st round
                            quest.setVisibility(View.VISIBLE);
                            btnNo.setVisibility(View.VISIBLE);
                            btnYes.setText(R.string.yes);
                        }
                    }

                    mergedArr.addAll(targets); //adding targets to each array
                    switch (round_counter){
                        case 1:
                            n = 60;
                            m=6;
                            break;
                        case 2:
                            n = 48;
                            m=5;
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
                for (int j = n; j < (12*m); j++) {
                    if(mergedArr.contains(distractors.get(j)))
                        Log.d("FUCKED",distractors.get(j)+" index: "+String.valueOf(j));
                    mergedArr.add(distractors.get(j));
                }// removing and adding the first 12 distractors to each round.
                }

            do { // select a unique random image from the selected 20 images of the round
                i = random.nextInt(mergedArr.size());
            }while (usedInRound.contains(mergedArr.get(i)));

            selectedImg = mergedArr.get(i);

            Picasso.get().load(selectedImg).placeholder(R.drawable.progress_circular).into(imageView);
            usedInRound.add(selectedImg);
            counter++;

            if(counter == 20 && round_counter == 6) {
                round_counter++;
                mergedArr.clear();
            }

            }
        else if(round_counter <= 12){
            if(round_counter == 7 && counter2 == 0) { //
                Snackbar.make(findViewById(android.R.id.content).getRootView(), "Starting 2nd run round 1", Snackbar.LENGTH_SHORT).show();
                if (res.equals("yes") && targets.contains(sImg))
                    run1_hits++; // increment
                //run 2
                //check target hits
            }
             else if(res.equals("yes") && targets2.contains(sImg))
                    run2_hits++; // increment

            if(counter2 == 20 || counter2 == 0) {
                if(round_counter >= 7 && counter2 == 20) {
                    mergedArr.clear();
                    round_counter++;
                    Snackbar.make(findViewById(android.R.id.content).getRootView(), "Starting round " + (round_counter - 6), Snackbar.LENGTH_SHORT).show();
                }
                mergedArr.addAll(targets2); //adding targets to each array
                switch (round_counter){
                    case 1:
                        n = 60;
                        m=6;
                        break;
                    case 2:
                        n = 48;
                        m=5;
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
                for (int j = n; j < (12*m); j++) {
                    mergedArr.add(distractors2.get(j));
                }
                counter2 = 0;
                usedInRound.clear();
            }
            do {
                i = random.nextInt(mergedArr.size());
            }while (usedInRound.contains(mergedArr.get(i)));

            selectedImg = mergedArr.get(i);
            Picasso.get().load(selectedImg).placeholder(R.drawable.progress_circular).into(imageView);
            usedInRound.add(selectedImg);
            counter2++;

            if(counter2 == 20 && round_counter == 12)
                round_counter++;
        }
        else{
           if(res.equals("yes") && targets2.contains(sImg))
                    run2_hits++; // increment
            Snackbar.make(findViewById(android.R.id.content).getRootView(),"Test completed with results run 1 hits: "+run1_hits+", run 2 hits: "+run2_hits+", Come back in one hour to continue with 2nd test",Snackbar.LENGTH_LONG).show();
        }

    }

}