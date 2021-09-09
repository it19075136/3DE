package mobile.application3DE.orientation;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.google.android.material.textview.MaterialTextView;

import mobile.application3DE.R;

public class AttentionResultsPage extends AppCompatActivity {

    MaterialTextView diff,results;
    String diffText,resultsText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attention_results_page);

        diff = findViewById(R.id.diff);
        results = findViewById(R.id.result);

        switch (getIntent().getStringExtra("originator")){
            case "walk":
                diffText = "Walk speed difference: ";
                resultsText = "Attention impairment: ";
                break;
            case "speech":
                diffText = "Speech rate difference: ";
                resultsText = "Attention impairment: ";
                break;
            default:
                break;
        }

        diff.setText(diffText+ getIntent().getStringExtra("diff"));
        results.setText(resultsText+getIntent().getStringExtra("result")+"%");

    }

}