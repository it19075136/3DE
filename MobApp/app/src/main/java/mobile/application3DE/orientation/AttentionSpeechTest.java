package mobile.application3DE.orientation;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;
import java.util.Locale;

import mobile.application3DE.R;
import mobile.application3DE.utilities.BaseActivity;

public class AttentionSpeechTest extends BaseActivity implements  RecognitionListener{

    ImageView speechBtn;
    MaterialTextView spokenWords;
    TextView counter,instruct;
    int count = 3,recordingTimer = 0,speechTime = 0;
    SpeechRecognizer speechRecognizer;
    CountDownTimer countDownTimer;
    AlertDialog dialog;

    private final static int RECOGNIZER_RESULT = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attention_speech_test);

        speechBtn = findViewById(R.id.recordBtn);
        spokenWords = (MaterialTextView)findViewById(R.id.header);
        counter = findViewById(R.id.counter);
        counter.setVisibility(View.INVISIBLE);
        instruct = findViewById(R.id.instruct);

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(AttentionSpeechTest.this);
        requestRecordAudioPermission();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Do you confirm your recording?")
                .setTitle("Confirm recording");

        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
                Toast.makeText(getApplicationContext(),"Your speech rate is : "+getResult()+" wps",Toast.LENGTH_LONG).show(); //shows result
            }
        });
        builder.setNegativeButton(R.string.retry, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                spokenWords.setText("Your words will appear here");
                speechBtn.performClick();
            }
        });

        dialog = builder.create();

    }

    public void onTap(View view) {

        counter.setVisibility(View.VISIBLE);
        instruct.setVisibility(View.INVISIBLE);
        new CountDownTimer(3000, 1000) {

            public void onTick(long l) {
                counter.setText(String.valueOf(count--));
            }

            public void onFinish() {
                spokenWords.setText("Your words will appear here");
                instruct.setVisibility(View.VISIBLE);
                instruct.setText("Listening...");
                speechBtn.setEnabled(false);
                counter.setVisibility(View.INVISIBLE);
                count = 3;
                counter.setText(String.valueOf(count));
                Intent speechIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                if (getString(R.string.language).equals(getString(R.string.sinhala)))
                    speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "si-LK");
                else
                    speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
                speechIntent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speech to text");
                speechIntent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 5000);
                speechIntent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS, 5000);
                speechIntent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 50000);
                speechRecognizer.setRecognitionListener(AttentionSpeechTest.this);
                speechRecognizer.startListening(speechIntent);
                countDownTimer = new CountDownTimer(20000,1000){

                    @Override
                    public void onTick(long l) {
                        recordingTimer++;
                    }

                    @Override
                    public void onFinish() {
                        speechRecognizer.stopListening();
                    }
                }.start();
            }

        }.start();

    }

    @Override
    public void onReadyForSpeech(Bundle bundle) {
        Log.d("TAG", "onReadyForSpeech");
    }

    @Override
    public void onBeginningOfSpeech() {
        Log.d("TAG", "onBeginningOfSpeech");
    }

    @Override
    public void onRmsChanged(float v) {
        Log.d("TAG", "onRmsChanged "+v);
    }

    @Override
    public void onBufferReceived(byte[] bytes) {
        Log.d("TAG", "onBufferReceived");
    }

    @Override
    public void onEndOfSpeech() {
        Log.d("TAG", "onEndofSpeech");
    }

    @Override
    public void onError(int i) {
        Log.d("TAG",  "error " +  i);
        Toast.makeText(this,"Recording failed,please try again",Toast.LENGTH_SHORT).show();
        countDownTimer.cancel();
        recordingTimer = 0;
        spokenWords.setText("Your words will appear here");
        instruct.setText("Tap to Start Recording");
        speechBtn.setEnabled(true);
    }

    @Override
    public void onResults(Bundle bundle) {

        countDownTimer.cancel();
        String str = new String();
        Log.d("TAG", "onResults " + bundle);
        ArrayList data = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        for (int i = 0; i < data.size(); i++)
        {
            Log.d("TAG", "result " + data.get(i));
            str += data.get(i);
        }
        spokenWords.setText(str);
        speechTime = recordingTimer;
        Toast.makeText(this,String.valueOf(speechTime) + " seconds",Toast.LENGTH_SHORT).show();
        recordingTimer = 0;
        dialog.show();
        instruct.setText("Tap to Start Recording");
        speechBtn.setEnabled(true);
    }

    @Override
    public void onPartialResults(Bundle bundle) {
        Log.d("TAG", "onPartialResults");
        String str = new String();
        Log.d("TAG", "onResults " + bundle);
        ArrayList data = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        for (int i = 0; i < data.size(); i++)
        {
            Log.d("TAG", "result " + data.get(i));
            str += data.get(i);
        }
        spokenWords.setText(str);
    }

    @Override
    public void onEvent(int i, Bundle bundle) {
        Log.d("TAG", "onEvent " + i);
    }

    private void requestRecordAudioPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String requiredPermission = Manifest.permission.RECORD_AUDIO;

            // If the user previously denied this permission then show a message explaining why
            // this permission is needed
            if (checkCallingOrSelfPermission(requiredPermission) == PackageManager.PERMISSION_DENIED) {
                requestPermissions(new String[]{requiredPermission}, 101);
            }
        }
    }

    private String getResult() {

        String spokenText = spokenWords.getText().toString();
        String[] words = spokenText.split(" ");
        Log.d("WORD_COUNT",String.valueOf(words.length));
        float result = (float)words.length/speechTime;
        return String.format("%.4f",result);
    }
}