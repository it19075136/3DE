package mobile.application3DE.verbalMemory;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import mobile.application3DE.R;
import pl.droidsonroids.gif.GifImageView;

public class LevelMusicPlayActivity extends AppCompatActivity {
    MediaPlayer music;
    GifImageView gifImageView;
    Integer level = 0;
    Button submitButton,start;
    SharedPreferences pref ;
    SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level_music_play);
        gifImageView=findViewById(R.id.animgif);
        pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
        editor = pref.edit();
        editor.putBoolean("notComplete",true);
        editor.putString("IR",null);
        editor.putString("DR",null); // Storing string
        editor.commit();
        submitButton = findViewById(R.id.submitButton);
        start =  findViewById(R.id.start);
        submitButton.setVisibility(View.INVISIBLE);
        start.setVisibility(View.VISIBLE);
        submitButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                showDialog();
            }
        });
        initUI();
    }
    private void initUI()
    {
        //UI reference of textView
        final AutoCompleteTextView LevelsAutoTV = findViewById(R.id.customerTextView);

        // create list of Levels
        ArrayList<String> LevelsList = getLevelsList();

        //Create adapter
        ArrayAdapter<String> adapter = new ArrayAdapter<>(LevelMusicPlayActivity.this, android.R.layout.simple_spinner_item, LevelsList);

        //Set adapter
        LevelsAutoTV.setAdapter(adapter);

        //submit button click event registration
        start.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                String value =LevelsAutoTV.getText().toString();
                if(value.contentEquals("O/L Not Passed")){
                    music = MediaPlayer.create(LevelMusicPlayActivity.this, R.raw.three);
                    level=3;
                }

                else if(value.contentEquals("O/L Passed")){
                    music = MediaPlayer.create(LevelMusicPlayActivity.this, R.raw.two);
                    level=2;
                }
                else if(value.contentEquals("A/L Passed")){
                    music = MediaPlayer.create(LevelMusicPlayActivity.this, R.raw.one);
                    level=1;
                }
                else{
                    return;
                }
                music.start();
                submitButton.setVisibility(View.VISIBLE);
                start.setVisibility(View.INVISIBLE);
            }
        });
    }

    private ArrayList<String> getLevelsList()
    {
        ArrayList<String> levels = new ArrayList<>();
        levels.add("O/L Not Passed");
        levels.add("O/L Passed");
        levels.add("A/L Passed");
        return levels;
    }
    public void showDialog(){

        new MaterialAlertDialogBuilder(LevelMusicPlayActivity.this)
                .setTitle("Is Listening Complete? ")
                .setMessage("If You are ok to go then Press OK")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        Intent i1 = new Intent(getApplicationContext(), SpeechTestActivity.class);
                        editor.putString("level",level.toString()); // Storing string
                        editor.commit();
                        i1.putExtra("level",level.toString());
                        startActivity(i1);
                    }
                })
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .show();

    }
}