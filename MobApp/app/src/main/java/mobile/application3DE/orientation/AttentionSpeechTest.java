package mobile.application3DE.orientation;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.StrictMode;
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

    private static final String LOG_TAG = "SPEECHTEST";
    ImageView speechBtn;
    MaterialTextView spokenWords;
    private ProgressBar progressBar;
    TextView counter,instruct;
    int count = 3,recordingTimer = 0,speechTime = 0;
    SpeechRecognizer speechRecognizer;
    MediaPlayer mediaPlayer;
    MediaRecorder mediaRecorder;
    private static final int SAMPLE_RATE = 44100;
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

        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT > 8) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

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

//                mediaRecorder = new MediaRecorder();
//                mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
//                mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
//                mediaRecorder.setOutputFile(getRecordingFilePath());
//                mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
//                try {
//                    mediaRecorder.prepare();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                mediaRecorder.start();

                 startRecording();
//                AudioManager audioManager = (AudioManager)AttentionSpeechTest.this.getSystemService(Context.AUDIO_SERVICE);
//                audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                countDownTimer = new CountDownTimer(15000,1000){

                    @Override
                    public void onTick(long l) {
                        recordingTimer++;
                    }

                    @Override
                    public void onFinish() {

//                        mediaRecorder.stop();
//                        mediaRecorder.release();
//                        mediaRecorder = null;

                         stopRecording();
//                        AudioManager audioManager = (AudioManager)AttentionSpeechTest.this.getSystemService(Context.AUDIO_SERVICE);
//                        audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);

//                         To set full volume
//                        int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_RING);
//                        audioManager.setStreamVolume(AudioManager.STREAM_RING, maxVolume, AudioManager.FLAG_SHOW_UI + AudioManager.FLAG_PLAY_SOUND);
                        speechTime = 15;
//                        speechRecognizer.stopListening(); //COMMENT this and check
                        Toast.makeText(getApplicationContext(),String.valueOf(speechTime) + " seconds",Toast.LENGTH_SHORT).show();
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

    private void translateRecording(byte[] audioArray) {

//        mediaPlayer = new MediaPlayer();
//        try {
//            mediaPlayer.setDataSource(getRecordingFilePath());
//            mediaPlayer.prepare();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        mediaPlayer.start();
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            path = Paths.get(getRecordingFilePath());
//        }
//
//        try {
//            byte[] audioArray = new byte[0];
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                audioArray = Files.readAllBytes(path);
        String audioString = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            audioString = Base64.getEncoder().encodeToString(audioArray);
        Log.d(LOG_TAG,"buffer string: "+audioString);

                //SEND THE HTTP REQUEST
                client = new OkHttpClient.Builder()
                        .connectTimeout(30, TimeUnit.SECONDS)
                        .build();

//                client = new OkHttpClient();

                String url = "https://three-de.herokuapp.com/speech/api";

                JSONObject audioObj = new JSONObject();
                try {
                    audioObj.put("audio", audioString);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

//                RequestBody requestBody = new MultipartBody.Builder()
//                        .setType(MultipartBody.FORM)
//                        .addFormDataPart("audio",audioString)
//                        .build();

                Request req = new Request.Builder()
                        .url(url)
                        .post(RequestBody.create(MediaType.parse("application/json"), String.valueOf(audioObj)))
                        .build();

            String finalAudioString = audioString;
            client.newCall(req).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {

                        if(response.isSuccessful())
                        AttentionSpeechTest.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                String res = null;
                                try {
                                    res = response.body().string();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                Log.d("TRANSCRIPT",res );
                                Log.d("REQBODY", finalAudioString);
                                Toast.makeText(AttentionSpeechTest.this,res, Toast.LENGTH_LONG).show();
                            }
                        });
                        else
                            AttentionSpeechTest.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_LONG).show();
                                }
                            });
                    }
                });
        }
        else
            Toast.makeText(this,"Translation not supported!",Toast.LENGTH_LONG).show();
    }

    private String getRecordingFilePath() {
        ContextWrapper contextWrapper = new ContextWrapper(getApplicationContext());
        File musicDir = contextWrapper.getExternalFilesDir(Environment.DIRECTORY_MUSIC);
        File file = new File(musicDir, "speechRecording"+".mp3");
        return  file.getPath();
    }

    private boolean mShouldContinue;
    private Thread mThread;

    public boolean recording() {
        return mThread != null;
    }

    public void startRecording() {
        if (mThread != null)
            return;

        mShouldContinue = true;
        mThread = new Thread(new Runnable() {
            @Override
            public void run() {
                record();
            }
        });
        mThread.start();
    }

    public void stopRecording() {
        if (mThread == null)
            return;

        mShouldContinue = false;
        mThread = null;
    }

    private void record() {
        Log.v(LOG_TAG, "Start");
        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_AUDIO);

        // buffer size in bytes
        int bufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT);

        if (bufferSize == AudioRecord.ERROR || bufferSize == AudioRecord.ERROR_BAD_VALUE) {
            bufferSize = SAMPLE_RATE * 2;
        }

        byte[] audioBuffer = new byte[bufferSize];

        AudioRecord record = new AudioRecord(MediaRecorder.AudioSource.DEFAULT,
                SAMPLE_RATE,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                bufferSize);

        if (record.getState() != AudioRecord.STATE_INITIALIZED) {
            Log.e(LOG_TAG, "Audio Record can't initialize!");
            return;
        }
        record.startRecording();

        Log.v(LOG_TAG, "Start recording");

        long shortsRead = 0;

        while (mShouldContinue) {
            int numberOfShort = record.read(audioBuffer, 0, audioBuffer.length);
            shortsRead += numberOfShort;
        }

        translateRecording(audioBuffer);
        record.stop();
        record.release();

        Log.v(LOG_TAG, String.format("Recording stopped. Samples read: %d", shortsRead));
    }

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