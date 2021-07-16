package mobile.application3DE.orientation;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import mobile.application3DE.R;
import mobile.application3DE.utilities.BaseActivity;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AttentionSpeechTest extends BaseActivity{

    ImageView speechBtn;
    MaterialTextView spokenWords;
    private ProgressBar progressBar;
    TextView counter,instruct;
    int count = 3,recordingTimer = 0,speechTime = 0;
    SpeechRecognizer speechRecognizer;
    MediaPlayer mediaPlayer;
    MediaRecorder mediaRecorder;
    CountDownTimer countDownTimer;
    AlertDialog dialog;
    Intent speechIntent,dualTask;
    String str,currentUser;
    DatabaseReference userRef,singleTaskRef;
    SimpleDateFormat formatDate;
    OkHttpClient client;
    Path path;

    // we will get the default FirebaseDatabase instance
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

    // we will get a DatabaseReference for the database root node
    DatabaseReference databaseReference = firebaseDatabase.getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attention_speech_test);

//        speechBtn = findViewById(R.id.recordBtn);
        spokenWords = (MaterialTextView)findViewById(R.id.header);
        spokenWords.setMovementMethod(new ScrollingMovementMethod());
        spokenWords.setVisibility(View.INVISIBLE);
        counter = findViewById(R.id.counter);
        counter.setVisibility(View.INVISIBLE);
        instruct = findViewById(R.id.instruct);
        instruct.setVisibility(View.INVISIBLE);
        progressBar =  findViewById(R.id.progressBar1);
        progressBar.setVisibility(View.INVISIBLE);

        str = new String();
        formatDate = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

        requestRecordAudioPermission();

        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
// Adding signed in user.

        if(acct != null)
            currentUser = acct.getId();

        userRef = databaseReference.child("users/"+currentUser);
        singleTaskRef = databaseReference.child("ComponentBasedResults/"+currentUser+"/Orientation/Attention/1/Talking");

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Do you confirm your recording?")
                .setTitle("Confirm recording");

        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
                Toast.makeText(getApplicationContext(),"Your speech rate is : "+getResult()+" wps",Toast.LENGTH_LONG).show(); //shows result
//                speechRecognizer.destroy();
                str = "";
                 //validate when you have more
                singleTaskRef.child("SingleTask").setValue(getResult()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        userRef.child("SingleTaskSpeech1Completed").setValue(formatDate.format(new Date())).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                dualTask = new Intent(getApplicationContext(), AttentionDualTaskStart.class);
                                dualTask.putExtra("singleTaskSpeechResult",getResult());
                                startActivity(dualTask);
                            }
                        });
                    }
                });
            }
        });
        builder.setNegativeButton(R.string.retry, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
//                spokenWords.setText("Your words will appear here");
//                speechRecognizer.destroy();
                startSpeechRecoginition();
            }
        });

        dialog = builder.create();
        startSpeechRecoginition();
    }

//    @Override
//    public void onResume() {
//        super.onResume();
//        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
//        if(SpeechRecognizer.isRecognitionAvailable(this))
//            speechRecognizer.setRecognitionListener(this);
//        else {
//            Snackbar.make(findViewById(android.R.id.content).getRootView(),"Speech recognition is unavaliable on your device",Snackbar.LENGTH_SHORT).show();
//            finish();
//        }
//        startSpeechRecoginition();
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        speechRecognizer.stopListening();
//    }
//
//    @Override
//    protected void onStop() {
//        super.onStop();
//        if (speechRecognizer != null) {
//            speechRecognizer.destroy();
//        }
//    }



    public void startSpeechRecoginition() {

        counter.setVisibility(View.VISIBLE);
        new CountDownTimer(3000, 1000) {

            public void onTick(long l) {
                counter.setText(String.valueOf(count--));
            }

            public void onFinish() {
                progressBar.setVisibility(View.VISIBLE);
                progressBar.setIndeterminate(true);
                speechTime = 0;
//                spokenWords.setText("Your words will appear here");
                str = "";
                instruct.setVisibility(View.VISIBLE);
                instruct.setText("Listening...");
//                speechBtn.setEnabled(false);
                counter.setVisibility(View.INVISIBLE);
                count = 3;
                counter.setText(String.valueOf(count));
//                speechIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
//                speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
//                if (getString(R.string.language).equals(getString(R.string.sinhala)))
//                    speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "si-LK");
//                else
//                    speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
//                speechIntent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speech to text");
//                speechIntent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 5000);
//                speechIntent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS, 5000);
//                speechIntent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 50000);
//                speechRecognizer = SpeechRecognizer.createSpeechRecognizer(AttentionSpeechTest.this); // creating a speech recognizer object
//                speechRecognizer.setRecognitionListener(AttentionSpeechTest.this); //setting the recognition listener
//                speechRecognizer.startListening(speechIntent); // start listening using the configured recognizer intent
                mediaRecorder = new MediaRecorder();
                mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                mediaRecorder.setOutputFile(getRecordingFilePath());
                mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                try {
                    mediaRecorder.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mediaRecorder.start();

//                AudioManager audioManager = (AudioManager)AttentionSpeechTest.this.getSystemService(Context.AUDIO_SERVICE);
//                audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                countDownTimer = new CountDownTimer(15000,1000){

                    @Override
                    public void onTick(long l) {
                        recordingTimer++;
                    }

                    @Override
                    public void onFinish() {

                        mediaRecorder.stop();
                        mediaRecorder.release();
                        mediaRecorder = null;
//                        AudioManager audioManager = (AudioManager)AttentionSpeechTest.this.getSystemService(Context.AUDIO_SERVICE);
//                        audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);

//                         To set full volume
//                        int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_RING);
//                        audioManager.setStreamVolume(AudioManager.STREAM_RING, maxVolume, AudioManager.FLAG_SHOW_UI + AudioManager.FLAG_PLAY_SOUND);
                        speechTime = 15;
//                        speechRecognizer.stopListening(); //COMMENT this and check
                        Toast.makeText(getApplicationContext(),String.valueOf(speechTime) + " seconds",Toast.LENGTH_SHORT).show();
                        playRecording();
                        recordingTimer = 0;
//                        dialog.show();
//                        instruct.setText("Tap to Start Recording");
                        instruct.setVisibility(View.INVISIBLE);
//                        speechBtn.setEnabled(true);
                    }
                }.start();
            }

        }.start();

    }

    private void playRecording() {

        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(getRecordingFilePath());
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaPlayer.start();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            path = Paths.get(getRecordingFilePath());
        }

        try {
            byte[] audioArray = new byte[0];
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                audioArray = Files.readAllBytes(path);
                String audioString = Base64.getEncoder().encodeToString(audioArray);

                //SEND THE HTTP REQUEST
                client = new OkHttpClient.Builder()
                        .connectTimeout(30, TimeUnit.SECONDS)
                        .build();

                String url = "https://three-de.herokuapp.com/speech/api";

//                JSONObject audioObj = new JSONObject();
//                try {
//                    audioObj.put("audio", audioString);
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }

                RequestBody requestBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("audio",audioString)
                        .build();

                Request req = new Request.Builder()
                        .url(url)
                        .post(requestBody)
                        .build();

                client.newCall(req).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {

                        AttentionSpeechTest.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Log.d("TRANSCRIPT", response.body().string());
                                    Toast.makeText(AttentionSpeechTest.this, response.body().string(), Toast.LENGTH_LONG).show();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                });
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private String getRecordingFilePath() {
        ContextWrapper contextWrapper = new ContextWrapper(getApplicationContext());
        File musicDir = contextWrapper.getExternalFilesDir(Environment.DIRECTORY_MUSIC);
        File file = new File(musicDir, "speechRecording"+".mp3");
        return  file.getPath();
    }

//    @Override
//    public void onReadyForSpeech(Bundle bundle) {
//        Log.d("TAG", "onReadyForSpeech");
//    }
//
//    @Override
//    public void onBeginningOfSpeech() {
//        Log.d("TAG", "onBeginningOfSpeech");
//        progressBar.setIndeterminate(false);
//        progressBar.setMax(10);
//    }
//
//    @Override
//    public void onRmsChanged(float v) {
//        Log.d("TAG", "onRmsChanged "+v);
//        progressBar.setProgress((int) v);
//
//    }
//
//    @Override
//    public void onBufferReceived(byte[] bytes) {
//        Log.d("TAG", "onBufferReceived");
//    }
//
//    @Override
//    public void onEndOfSpeech() {
//        Log.d("TAG", "onEndofSpeech");
//        progressBar.setIndeterminate(true);
//        speechRecognizer.stopListening();
//    }
//
//    @Override
//    public void onError(int i) {
//        Log.d("TAG",  "error " +  i);
//        if(i == SpeechRecognizer.ERROR_NO_MATCH) {
//            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(AttentionSpeechTest.this);
//            speechRecognizer.setRecognitionListener(AttentionSpeechTest.this);
//            speechRecognizer.startListening(speechIntent);
//        }
//        else {
////            Toast.makeText(this, "Recording failed,please try again", Toast.LENGTH_SHORT).show();
//            countDownTimer.cancel();
//            recordingTimer = 0;
////            spokenWords.setText("Your words will appear here");
////            instruct.setText("Tap to Start Recording");
//            instruct.setVisibility(View.INVISIBLE);
////            speechBtn.setEnabled(true);
//        }
//    }
//
//    @Override
//    public void onResults(Bundle bundle) {
//
//        Log.d("TAG", "onResults " + bundle);
//        ArrayList data = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
//        for (int i = 0; i < data.size(); i++)
//        {
//            Log.d("TAG", "result " + data.get(i));
//            str += data.get(i);
//        }
//        str = str + " ";
//        spokenWords.setText(str);
////        if(speechTime == 20) {
////            speechRecognizer.destroy();
////        }
////        else
//            speechRecognizer.startListening(speechIntent);
//
//    }
//
//    @Override
//    public void onPartialResults(Bundle bundle) {
//        Log.d("TAG", "onPartialResults");
//        ArrayList data = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
//        for (int i = 0; i < data.size(); i++)
//        {
//            Log.d("TAG", "result " + data.get(i));
//            str += data.get(i);
//        }
//        str = str + " ";
//        spokenWords.setText(str);
//    }
//
//    @Override
//    public void onEvent(int i, Bundle bundle) {
//        Log.d("TAG", "onEvent " + i);
//    }

    private void requestRecordAudioPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String requiredPermission = Manifest.permission.RECORD_AUDIO;
            String requiredPermissionStorage = Manifest.permission.MANAGE_EXTERNAL_STORAGE;

            // If the user previously denied this permission then show a message explaining why
            // this permission is needed
            if (checkCallingOrSelfPermission(requiredPermission) == PackageManager.PERMISSION_DENIED) {
                requestPermissions(new String[]{requiredPermission,requiredPermissionStorage}, 101);
            }
        }
    }

    private String getResult() {

        String spokenText = spokenWords.getText().toString();
        String[] words = spokenText.split(" ");
        Log.d("WORD_COUNT",String.valueOf(words.length));
        float result = (float)(words.length)/speechTime;
        return String.format("%.4f",result);
    }
}