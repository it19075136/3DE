package mobile.application3DE.orientation;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.StrictMode;
import android.text.method.ScrollingMovementMethod;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import mobile.application3DE.R;
import mobile.application3DE.utilities.BaseActivity;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
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

    // [START recording_parameters]
    private static final int AUDIO_SOURCE = MediaRecorder.AudioSource.UNPROCESSED;
    private static final int SAMPLE_RATE_IN_HZ = 16000;
    private static final int CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO;
    private static final int AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT;
    // [END recording_parameters]
    private static final String TAG = "SPEECHTEST";

    private static final int BUFFER_SIZE = AudioRecord.getMinBufferSize(SAMPLE_RATE_IN_HZ, CHANNEL_CONFIG, AUDIO_FORMAT);
    ContextWrapper contextWrapper;
    private String RAW_FILE_PATH,WAV_FILE_PATH;

    private AudioRecord audioRecord;
    private boolean isRecording = false;
    private BufferedOutputStream outputStream;

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

        contextWrapper = new ContextWrapper(getApplicationContext());
        RAW_FILE_PATH = contextWrapper.getExternalFilesDir(Environment.DIRECTORY_MUSIC) + "/speech-recording.raw";
        WAV_FILE_PATH = contextWrapper.getExternalFilesDir(Environment.DIRECTORY_MUSIC) + "/speech-recording.wav";

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
                counter.setVisibility(View.INVISIBLE);
                count = 3;
                counter.setText(String.valueOf(count));
                startRecording(new RecordingListener() {
                    @Override
                    public void onRecordingSucceeded(File output) {
                        Log.d(TAG,"Recorded successfully");
                        translateRecording(output);
                    }

                    @Override
                    public void onRecordingFailed(Exception e) {
                        Log.d(TAG,"Recording failed");
                    }
                });
                countDownTimer = new CountDownTimer(15000,1000){

                    @Override
                    public void onTick(long l) {
                        recordingTimer++;
                    }

                    @Override
                    public void onFinish() {
                        stopRecording();
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

    private void translateRecording(File audioFile) {

        byte[] data = new byte[(int) audioFile.length()];
        DataInputStream input = null;
        int readBytes = 0;
        try {
            input = new DataInputStream(new FileInputStream(audioFile));
            readBytes = input.read(data);
            Log.i(TAG, readBytes + " read from input file.");
            input.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String audioString =  Base64.encodeToString(data, Base64.NO_WRAP);
        Log.d(LOG_TAG,"buffer string: "+audioString);

                //SEND THE HTTP REQUEST
                client = new OkHttpClient.Builder()
                        .connectTimeout(60, TimeUnit.SECONDS)
                        .build();


                String url = "https://three-de.herokuapp.com/speech/api";

                JSONObject audioObj = new JSONObject();
                try {
                    audioObj.put("audio", audioString);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

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

//    public void startRecording() {
//        if (mThread != null)
//            return;
//
//        mShouldContinue = true;
//        mThread = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                record();
//            }
//        });
//        mThread.start();
//    }

    public void startRecording(final RecordingListener recordingListener) {
        isRecording = true;

        new Thread(() -> {
            android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);

            byte data[] = new byte[BUFFER_SIZE];
            audioRecord = new AudioRecord(
                    AUDIO_SOURCE,
                    SAMPLE_RATE_IN_HZ,
                    CHANNEL_CONFIG,
                    AUDIO_FORMAT,
                    BUFFER_SIZE
            );

            audioRecord.startRecording();

            try {
                outputStream = new BufferedOutputStream(new FileOutputStream(RAW_FILE_PATH));
            } catch (FileNotFoundException e) {
                Log.e(TAG, "Couldn't find file: " + RAW_FILE_PATH, e);
                recordingListener.onRecordingFailed(e);
            }

            // This loop runs until the client calls stopRecording().
            while (isRecording) {
                int status = audioRecord.read(data, 0, data.length);

                if (status == AudioRecord.ERROR_INVALID_OPERATION || status == AudioRecord.ERROR_BAD_VALUE) {
                    Log.e(TAG, "Couldn't read data");
                    recordingListener.onRecordingFailed(new IOException());
                }

                try {
                    outputStream.write(data, 0, data.length);
                } catch (IOException e) {
                    Log.e(TAG, "Couldn't save data", e);
                    recordingListener.onRecordingFailed(e);
                }
            }

            // After the client calls stopRecording(), this method processes the recorded audio.
            try {
                outputStream.close();
                audioRecord.stop();
                audioRecord.release();

                Log.v(TAG, "Recording stopped");

                File rawFile = new File(RAW_FILE_PATH);
                File wavFile = new File(WAV_FILE_PATH);
                saveAsWave(rawFile, wavFile);
                recordingListener.onRecordingSucceeded(wavFile);
            } catch (IOException e) {
                Log.e(TAG, "File error", e);
                recordingListener.onRecordingFailed(e);
            }
        }).start();
    }


    public void stopRecording() {
        isRecording = false;
    }

    private void saveAsWave(final File rawFile, final File waveFile) throws IOException {
        byte[] rawData = new byte[(int) rawFile.length()];
        try (DataInputStream input = new DataInputStream(new FileInputStream(rawFile))) {
            int readBytes;
            do {
                readBytes = input.read(rawData);
            }
            while(readBytes != -1);
        }
        try (DataOutputStream output = new DataOutputStream(new FileOutputStream(waveFile))) {
            // WAVE specification
            Charset asciiCharset = Charset.forName("US-ASCII");
            // Chunk ID: "RIFF" string in US-ASCII charset—4 bytes Big Endian
            output.write("RIFF".getBytes(asciiCharset));
            // Chunk size: The size of the actual sound data plus the rest
            //             of this header (36 bytes)—4 bytes Little Endian
            output.write(convertToLittleEndian(36 + rawData.length));
            // Format: "WAVE" string in US-ASCII charset—4 bytes Big Endian
            output.write("WAVE".getBytes(asciiCharset));
            // Subchunk 1 ID: "fmt " string in US-ASCII charset—4 bytes Big Endian
            output.write("fmt ".getBytes(asciiCharset));
            // Subchunk 1 size: The size of the subchunk.
            //                  It must be 16 for PCM—4 bytes Little Endian
            output.write(convertToLittleEndian(16));
            // Audio format: Use 1 for PCM—2 bytes Little Endian
            output.write(convertToLittleEndian((short)1));
            // Number of channels: This sample only supports one channel—2 bytes Little Endian
            output.write(convertToLittleEndian((short)1));
            // Sample rate: The sample rate in hertz—4 bytes Little Endian
            output.write(convertToLittleEndian(SAMPLE_RATE_IN_HZ));
            // Bit rate: SampleRate * NumChannels * BitsPerSample/8—4 bytes Little Endian
            output.write(convertToLittleEndian(SAMPLE_RATE_IN_HZ * 2));
            // Block align: NumChannels * BitsPerSample/8—2 bytes Little Endian
            output.write(convertToLittleEndian((short)2));
            // Bits per sample: 16 bits—2 bytes Little Endian
            output.write(convertToLittleEndian((short)16));
            // Subchunk 2 ID: "fmt " string in US-ASCII charset—4 bytes Big Endian
            output.write("data".getBytes(asciiCharset));
            // Subchunk 2 size: The size of the actual audio data—4 bytes Little Endian
            output.write(convertToLittleEndian(rawData.length));

            // Audio data:  Sound data bytes—Little Endian
            short[] rawShorts = new short[rawData.length / 2];
            ByteBuffer.wrap(rawData).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(rawShorts);
            ByteBuffer bytes = ByteBuffer.allocate(rawData.length);
            for (short s : rawShorts) {
                bytes.putShort(s);
            }

            output.write(readFile(rawFile));
        }
    }

    private byte[] readFile(File f) throws IOException {
        int size = (int) f.length();
        byte bytes[] = new byte[size];
        byte tmpBuff[] = new byte[size];
        try (FileInputStream fis = new FileInputStream(f)) {
            int read = fis.read(bytes, 0, size);
            if (read < size) {
                int remain = size - read;
                while (remain > 0) {
                    read = fis.read(tmpBuff, 0, remain);
                    System.arraycopy(tmpBuff, 0, bytes, size - remain, read);
                    remain -= read;
                }
            }
        }
        return bytes;
    }

    private byte[] convertToLittleEndian(Object value) {
        int size;
        if(value.getClass().equals(Integer.class)) {
            size = 4;
        } else if (value.getClass().equals(Short.class)) {
            size = 2;
        } else {
            throw new IllegalArgumentException("Only int and short types are supported");
        }

        byte[] littleEndianBytes = new byte[size];
        ByteBuffer byteBuffer = ByteBuffer.allocate(size);
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN);

        if(value.getClass().equals(Integer.class)) {
            byteBuffer.putInt((int)value);
        } else if (value.getClass().equals(Short.class)) {
            byteBuffer.putShort((short)value);
        }

        byteBuffer.flip();
        byteBuffer.get(littleEndianBytes);

        return littleEndianBytes;
    }

//    public void stopRecording() {
//        if (mThread == null)
//            return;
//
//        mShouldContinue = false;
//        mThread = null;
//    }

//    private void record() {
//        Log.v(LOG_TAG, "Start");
//        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_AUDIO);
//
//        // buffer size in bytes
//        int bufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE,
//                AudioFormat.CHANNEL_IN_MONO,
//                AudioFormat.ENCODING_PCM_16BIT);
//
//        if (bufferSize == AudioRecord.ERROR || bufferSize == AudioRecord.ERROR_BAD_VALUE) {
//            bufferSize = SAMPLE_RATE * 2;
//        }
//
//        byte[] audioBuffer = new byte[bufferSize];
//
//        AudioRecord record = new AudioRecord(MediaRecorder.AudioSource.UNPROCESSED,
//                SAMPLE_RATE,
//                AudioFormat.CHANNEL_IN_MONO,
//                AudioFormat.ENCODING_PCM_16BIT,
//                bufferSize);
//
//        if (record.getState() != AudioRecord.STATE_INITIALIZED) {
//            Log.e(LOG_TAG, "Audio Record can't initialize!");
//            return;
//        }
//        record.startRecording();
//
//        Log.v(LOG_TAG, "Start recording");
//
//        long shortsRead = 0;
//
//        while (mShouldContinue) {
//            int numberOfShort = record.read(audioBuffer, 0, audioBuffer.length);
//            shortsRead += numberOfShort;
//        }
//
//        translateRecording(audioBuffer);
//        record.stop();
//        record.release();
//
//        Log.v(LOG_TAG, String.format("Recording stopped. Samples read: %d", shortsRead));
//    }


//    private void requestRecordAudioPermission() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            String requiredPermission = Manifest.permission.RECORD_AUDIO;
//            String requiredPermissionStorage = Manifest.permission.MANAGE_EXTERNAL_STORAGE;
//
//            // If the user previously denied this permission then show a message explaining why
//            // this permission is needed
//            if (checkCallingOrSelfPermission(requiredPermission) == PackageManager.PERMISSION_DENIED) {
//                requestPermissions(new String[]{requiredPermission,requiredPermissionStorage}, 101);
//            }
//        }
//    }

    private String getResult() {

        String spokenText = spokenWords.getText().toString();
        String[] words = spokenText.split(" ");
        Log.d("WORD_COUNT",String.valueOf(words.length));
        float result = (float)(words.length)/speechTime;
        return String.format("%.4f",result);
    }

    public interface RecordingListener {
        void onRecordingSucceeded(File output);
        void onRecordingFailed(Exception e);
    }
}