package mobile.application3DE;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.google.android.material.textfield.TextInputEditText;

public class activity_choice_landing extends AppCompatActivity {

    TextInputEditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choice_landing);

        editText = findViewById(R.id.firstName);

        editText.setText(getIntent().getStringExtra("fname"));

    }
}