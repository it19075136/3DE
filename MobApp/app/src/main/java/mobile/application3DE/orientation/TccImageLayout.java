package mobile.application3DE.orientation;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
    ArrayList<Integer> usedInRound = new ArrayList<>();
    ArrayList<String> imageSet,targets,distractors,distractors2,mergedArr;
    HashMap<String,ArrayList<String>> sortedImgs = new HashMap<>();
    int counter = 0,counter2 = 0;
    int run1_hits = 0,run2_hits = 0;
    int round_counter = 1,i,selectedImg;

    //  2 runs.. 6 rounds for each run. 12 distracter items and  8 target/repeating items.- 80 images.. Put 8 target items from run1 into distracter of the
    // 2 nd run ,amd take the remaining 8 distracters from run 1 among distractors

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tcc_image_layout);

        imageView = findViewById(R.id.imgView);
//        progressIndicator = findViewById(R.id.progress_circular);
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

        Toast.makeText(getApplicationContext(),"Images count: "+imageSet.size(),Toast.LENGTH_SHORT).show();
        // RUN1
        for (int i = 0; i < imageSet.size(); i++) {
            if(i < 8)
                targets.add(imageSet.get(i));
            else
                distractors.add(imageSet.get(i));
        }

        sortedImgs.put("targets",targets); // adding first 8 images as targets
        sortedImgs.put("distractors",distractors); // adding remaining 72 as distratcors for 1st run

        targets.clear(); // clearing 1st run targets

        // RUN2
        for (int i = 0; i < imageSet.size(); i++) {
            if(i < 72)
                distractors2.add(imageSet.get(i));
            else
                targets.add(imageSet.get(i));
        }

        sortedImgs.put("targets2",targets); // adding last 8 images as targets
        sortedImgs.put("distractors2",distractors); // adding first 72 images as distractors which include target items of 1st run

        Picasso.get().load(sortedImgs.get("targets").get(0)).into(imageView); //setting 1st run's 1st image from targets
        Snackbar.make(findViewById(android.R.id.content).getRootView(),"Starting round "+ round_counter,Snackbar.LENGTH_SHORT).show();
        usedInRound.add(0);
        selectedImg = 0;

        btnYes.setOnClickListener(v -> setNextImage(selectedImg,"yes"));

        btnNo.setOnClickListener(v -> setNextImage(selectedImg,"no"));

    }

    public void setNextImage(int sImg,String res){

            if(round_counter <= 6){ //1
            // run 1
                // check target hits
                if(res.equals("yes") && sImg < 8 && round_counter > 1)
                    run1_hits++; // increment

                if(counter == 19 || counter == 0) {
                    mergedArr.clear();
                if(round_counter > 1) {
                    Snackbar.make(findViewById(android.R.id.content).getRootView(), "Starting round " + round_counter, Snackbar.LENGTH_SHORT).show(); // handle error
                    usedInRound.clear();
                    counter = 0;
                    if(round_counter == 2){
                        quest.setVisibility(View.VISIBLE);
                        btnNo.setVisibility(View.VISIBLE);
                        btnYes.setText(R.string.yes);
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
            }while (usedInRound.contains(i));
            Picasso.get().load(mergedArr.get(i)).into(imageView);
            selectedImg = i;
            usedInRound.add(i);
            counter2++;
        }
        else{
            Snackbar.make(findViewById(android.R.id.content).getRootView(),"Test completed with results run 1 hits: "+run1_hits+", run 2 hits: "+run2_hits+", Come back in one hour to continue with 2nd test",Snackbar.LENGTH_LONG).show();
        }

    }

}