package mobile.application3DE.orientation;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
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
    ArrayList<String> imageSet,targets,distractors,distractors2,mergedArr;
    HashMap<String,ArrayList<String>> sortedImgs = new HashMap<>();
    int counter = 0,counter2 = 0;
    int run1_hits = 0,run2_hits = 0;
    int round_counter = 0,i,n=0;
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
        mergedArr = new ArrayList<>(); // arraylist per round. To merge distractors and targets

        // RUN1
        for (int i = 0; i < imageSet.size(); i++) {
            if(i < 8)
                targets.add(imageSet.get(i));
            else
                distractors.add(imageSet.get(i));
        }

        sortedImgs.put("targets",targets); // adding first 8 images as targets
        sortedImgs.put("distractors",distractors); // adding remaining 72 as distratcors for 1st run

        Snackbar.make(findViewById(android.R.id.content).getRootView(),"Distractors count:"+distractors.size(),Snackbar.LENGTH_SHORT).show();
        targets.clear(); // clearing 1st run targets

        // RUN2
        for (int i = 0; i < imageSet.size(); i++) {
            if(i < 72)
                distractors2.add(imageSet.get(i));
            else
                targets.add(imageSet.get(i));
        }

        sortedImgs.put("targets2",targets); // adding last 8 images as targets
        sortedImgs.put("distractors2",distractors2); // adding first 72 images as distractors which include target items of 1st run

        selectedImg = sortedImgs.get("targets").get(0); //getting 1st run's 1st image from targets
        Picasso.get().load(selectedImg).into(imageView); //setting 1st run's 1st image from targets
//        Snackbar.make(findViewById(android.R.id.content).getRootView(),"Starting round 1",Snackbar.LENGTH_SHORT).show();
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
                if(res.equals("yes") && sortedImgs.get("targets").contains(sImg) && round_counter > 1)
                    run1_hits++; // increment

                if(counter == 20 || (counter == 1 && round_counter == 1)) { // checking start of rounds or end of rounds

                    mergedArr.clear(); //clearing previous image set

                    if(round_counter >= 1 && counter == 20) { // Checking for rounds after 1st round
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

                targets = sortedImgs.get("targets"); //1st run targets
                mergedArr.addAll(targets); //adding target items
                    switch (round_counter){
                        case 1:
                            n = 0;
                            break;
                        case 2:
                            n = 12;
                            break;
                        case 3:
                            n = 24;
                            break;
                        case 4:
                            n = 36;
                            break;
                        case 5:
                            n = 48;
                            break;
                        case 6:
                            n = 60;
                            break;
                        default:
                            break;
                    }
                for (int j = n; j < (12*round_counter); j++) {
                        mergedArr.add(distractors.get(j));
                }// removing and adding the first 12 distractors to each round.
                    Snackbar.make(findViewById(android.R.id.content).getRootView(), "round: " + round_counter+", merged size:"+mergedArr.size(), Snackbar.LENGTH_SHORT).show();
                }

            do { // select a unique random image from the selected 20 images of the round
                i = random.nextInt(mergedArr.size());
            }while (usedInRound.contains(mergedArr.get(i)));

            selectedImg = mergedArr.get(i);

            Picasso.get().load(selectedImg).placeholder(R.drawable.progress_circular).into(imageView);
            usedInRound.add(selectedImg);
            counter++;
//            Snackbar.make(findViewById(android.R.id.content).getRootView(), "round: " + round_counter+", count: "+counter, Snackbar.LENGTH_SHORT).show();

            if(counter == 20 && round_counter == 6)
                round_counter++;

            }
        else if(round_counter <= 12){
            if(round_counter == 7)
                Snackbar.make(findViewById(android.R.id.content).getRootView(),"Starting 2nd run",Snackbar.LENGTH_SHORT).show();
            //run 2
                //check target hits
                if(res.equals("yes") && sortedImgs.get("targets2").contains(sImg))
                    run2_hits++; // increment

            if(counter2 == 20 || counter2 == 0) {
                mergedArr.clear();
                if(round_counter >= 7 && counter2 == 20)
                    round_counter++;
                Snackbar.make(findViewById(android.R.id.content).getRootView(),"Starting round "+ (round_counter-6),Snackbar.LENGTH_SHORT).show();
                targets = sortedImgs.get("targets2");
                mergedArr.addAll(targets); //adding target items
                for (int j = 0; j < 12; j++)
                    mergedArr.add(distractors2.remove(j)); // removing and adding the first 12 distractors to each round
                counter2 = 0;
                usedInRound.clear();
            }
            do {
                i = random.nextInt(mergedArr.size());
            }while (usedInRound.contains(mergedArr.get(i)));

            selectedImg = mergedArr.get(i);
            Picasso.get().load(selectedImg).into(imageView);
            usedInRound.add(selectedImg);
            counter2++;
        }
        else{
            Snackbar.make(findViewById(android.R.id.content).getRootView(),"Test completed with results run 1 hits: "+run1_hits+", run 2 hits: "+run2_hits+", Come back in one hour to continue with 2nd test",Snackbar.LENGTH_LONG).show();
        }

    }

}