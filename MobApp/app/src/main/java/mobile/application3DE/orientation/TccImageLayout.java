package mobile.application3DE.orientation;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import mobile.application3DE.R;

public class TccImageLayout extends AppCompatActivity {

    ImageView imageView;
    Button btnYes,btnNo;
    HashMap<Integer,String> imageLinks = new HashMap<>();
    Random random = new Random();
    ArrayList<Integer> used = new ArrayList<>();
    ArrayList<String> imageSet,rptImageSet;
    int selectedImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tcc_image_layout);

        imageView = findViewById(R.id.imgView);
//        progressIndicator = findViewById(R.id.progress_circular);
        btnYes = findViewById(R.id.yesBtn);
        btnNo = findViewById(R.id.noBtn);

        imageSet = getIntent().getStringArrayListExtra("imageSet");
        rptImageSet = getIntent().getStringArrayListExtra("rptImageSet");

        for (int i = 0; i < imageSet.size(); i++)
            imageLinks.put(i,imageSet.get(i));

        for (int j = 0; j < rptImageSet.size(); j++)
            imageLinks.put(imageSet.size(),rptImageSet.get(j));

//        progressIndicator.setVisibility(View.INVISIBLE);
        Picasso.get().load(imageLinks.get(0)).into(imageView);
        selectedImg = 0;
        used.add(0);

        Resources res = getResources();

        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(used.size() < 4)
                    setNextImage(selectedImg,"yes");
                else
                    Snackbar.make(v,"You've completed the test...",Snackbar.LENGTH_SHORT).setBackgroundTint(res.getColor(R.color.success)).show();
            }
        });

        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(used.size() < 4)
                    setNextImage(selectedImg,"no");
                else
                    Snackbar.make(v,"You've completed the test...",Snackbar.LENGTH_SHORT).setBackgroundTint(res.getColor(R.color.success)).show();
            }
        });

    }

    public void setNextImage(int id,String res){
        // do whatever
        used.add(id);
        int i;
        do {
            i = random.nextInt(imageSet.size());
        } while (used.contains(i));
        Picasso.get().load(imageLinks.get(i)).into(imageView);
        selectedImg = i;

    }

}