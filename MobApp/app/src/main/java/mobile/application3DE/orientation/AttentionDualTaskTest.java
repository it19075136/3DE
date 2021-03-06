package mobile.application3DE.orientation;


import android.app.AlertDialog;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.StrictMode;
import android.text.method.ScrollingMovementMethod;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;

import com.gauravk.audiovisualizer.visualizer.WaveVisualizer;
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
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import mobile.application3DE.R;
import mobile.application3DE.models.Result;
import mobile.application3DE.utilities.BaseActivity;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AttentionDualTaskTest extends BaseActivity {

    private static final String LOG_TAG = "SPEECHTEST";
    MaterialTextView spokenWords;
    TextView counter,walking,listening,instruct;
    int count = 3,recordingTimer = 0,speechTime = 0;
    String type = "gen";

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
    private ProgressBar loading;
    AlertDialog dialog;
    Intent resultIntent;
    String str,currentUser,lang;
    DatabaseReference userRef,dualTaskRef,attentionRef;
    SimpleDateFormat formatDate;
    Float diff,Finalresult;
    OkHttpClient client;
    WaveVisualizer waveVisualizer;

    // we will get the default FirebaseDatabase instance
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

    // we will get a DatabaseReference for the database root node
    DatabaseReference databaseReference = firebaseDatabase.getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attention_dual_task_test);

        contextWrapper = new ContextWrapper(getApplicationContext());
        RAW_FILE_PATH = contextWrapper.getExternalFilesDir(Environment.DIRECTORY_MUSIC) + "/speech-recording.raw";
        WAV_FILE_PATH = contextWrapper.getExternalFilesDir(Environment.DIRECTORY_MUSIC) + "/speech-recording.wav";

        spokenWords = (MaterialTextView)findViewById(R.id.header);
        spokenWords.setMovementMethod(new ScrollingMovementMethod());
        spokenWords.setVisibility(View.INVISIBLE);
        counter = findViewById(R.id.counter);
        counter.setVisibility(View.INVISIBLE);
        instruct = findViewById(R.id.testInstruct);
        walking = findViewById(R.id.walking);
        listening = findViewById(R.id.listening);
        loading = findViewById(R.id.loadingPanel);
        loading.setVisibility(View.INVISIBLE);
        waveVisualizer = findViewById(R.id.wave);

        walking.setVisibility(View.INVISIBLE);
        listening.setVisibility(View.INVISIBLE);

        if (getString(R.string.language).equals(getString(R.string.sinhala)))
            lang = "si-LK";
        else
            lang = "en-US";

        str = new String();
        formatDate = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
// Adding signed in user.

        if(acct != null)
            currentUser = acct.getId();

        if (getIntent().getStringExtra("type") != null)
            type = "once";

        userRef = databaseReference.child("users/"+currentUser);
        dualTaskRef = databaseReference.child("ComponentBasedResults/"+currentUser+"/Orientation/Attention/1/Talking");
        attentionRef = databaseReference.child("AttentionResults/"+currentUser+"/Orientation/Attention");

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Do you confirm your recording?")
                .setTitle("Confirm recording");

        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
                Toast.makeText(getApplicationContext(),"Your speech rate is : "+getResult()+" wps, Previous speech rate:"+getIntent().getStringExtra("singleTaskResult")+" wps",Toast.LENGTH_LONG).show(); //shows result
                str = "";
                diff = Float.parseFloat(getIntent().getStringExtra("singleTaskResult")) - Float.parseFloat(getResult());
                if(diff < 0)
                    diff = (float)0;
                // add diff and result to firebase,add timestamps to user
                //validate when you have more

                attentionRef.child(formatDate.format(new Date())).setValue(new Result(type,formatDate.format(new Date()),getIntent().getStringExtra("singleTaskResult"),getResult(),getFinalResult(),String.format("%.4f",diff))).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        resultIntent = new Intent(getApplicationContext(),AttentionResultsPage.class);
                        resultIntent.putExtra("result",getFinalResult());
                        resultIntent.putExtra("originator","speech");
                        resultIntent.putExtra("diff",String.format("%.4f",diff));
                        resultIntent.putExtra("type",type);
                        if (type.equals("gen"))
                        dualTaskRef.child("dualTask").setValue(getResult()).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                dualTaskRef.child("impairment").setValue(getFinalResult());
                                userRef.child("DualTask1Completed").setValue(formatDate.format(new Date())).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        dualTaskRef.child("difference").setValue(String.format("%.4f",diff)).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                startActivity(resultIntent);
                                            }
                                        });
                                    }
                                });
                            }
                        });
                        else
                            startActivity(resultIntent);
                    }
                });
            }
        });
        builder.setNegativeButton(R.string.retry, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
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
                speechTime = 0;
                str = "";
                walking.setVisibility(View.VISIBLE);
                listening.setVisibility(View.VISIBLE);
                counter.setVisibility(View.INVISIBLE);
                count = 3;
                counter.setText(String.valueOf(count));
                startRecording(new RecordingListener() {
                    @Override
                    public void onRecordingSucceeded(File output) {
                        Log.d(TAG,"Recorded successfully");
                        try {
                            translateRecording(output);
                        } catch (SocketException e) {
                            Toast.makeText(AttentionDualTaskTest.this,"Please wait...", Toast.LENGTH_LONG).show();
                            try {
                                translateRecording(output);
                            } catch (SocketException socketException) {
                                Toast.makeText(AttentionDualTaskTest.this,"Network error, Please check your network connection", Toast.LENGTH_LONG).show();
                            }
                        }
                    }

                    @Override
                    public void onRecordingFailed(Exception e) {
                        Log.d(TAG,"Recording failed");
                    }
                });
                countDownTimer = new CountDownTimer(25000,1000){

                    @Override
                    public void onTick(long l) {
                        recordingTimer++;
                    }

                    @Override
                    public void onFinish() {
                        ToneGenerator toneGen = new ToneGenerator(AudioManager.STREAM_MUSIC, ToneGenerator.MAX_VOLUME);
                        toneGen.startTone(ToneGenerator.TONE_PROP_BEEP,3000);
                        stopRecording();
                        speechTime = 25;
                        Toast.makeText(getApplicationContext(),String.valueOf(speechTime) + " seconds",Toast.LENGTH_SHORT).show();
                        recordingTimer = 0;
                        instruct.setText(R.string.pleaseWait);
                        loading.setVisibility(View.VISIBLE);
                    }
                }.start();
            }

        }.start();

    }

    private void translateRecording(File audioFile) throws SocketException {

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

        //CREATING HTTP CLIENT
        client = new OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60,TimeUnit.SECONDS)
                .writeTimeout(20,TimeUnit.SECONDS)
                .build();

        String url = "https://three-de.herokuapp.com/speech/api";

        JSONObject audioObj = new JSONObject();
        try {
            audioObj.put("audio", audioString);
            audioObj.put("lang", lang);
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
                call.cancel();
                AttentionDualTaskTest.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(AttentionDualTaskTest.this, e.getMessage()+", Poor network connection,Please try again",Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) {

                if(response.isSuccessful())
                    AttentionDualTaskTest.this.runOnUiThread(new Runnable() {
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
                            Toast.makeText(AttentionDualTaskTest.this,res, Toast.LENGTH_LONG).show();
                            spokenWords.setText(res);
                            loading.setVisibility(View.INVISIBLE);
                            instruct.setVisibility(View.INVISIBLE);
                            dialog.show();
                        }
                    });
                else
                    AttentionDualTaskTest.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "Failed,Please try again", Toast.LENGTH_LONG).show();
                        }
                    });
            }
        });
    }

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
                waveVisualizer.setRawAudioBytes(data);

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
                if(waveVisualizer != null)
                    waveVisualizer.release();

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
            // Chunk ID: "RIFF" string in US-ASCII charset???4 bytes Big Endian
            output.write("RIFF".getBytes(asciiCharset));
            // Chunk size: The size of the actual sound data plus the rest
            //             of this header (36 bytes)???4 bytes Little Endian
            output.write(convertToLittleEndian(36 + rawData.length));
            // Format: "WAVE" string in US-ASCII charset???4 bytes Big Endian
            output.write("WAVE".getBytes(asciiCharset));
            // Subchunk 1 ID: "fmt " string in US-ASCII charset???4 bytes Big Endian
            output.write("fmt ".getBytes(asciiCharset));
            // Subchunk 1 size: The size of the subchunk.
            //                  It must be 16 for PCM???4 bytes Little Endian
            output.write(convertToLittleEndian(16));
            // Audio format: Use 1 for PCM???2 bytes Little Endian
            output.write(convertToLittleEndian((short)1));
            // Number of channels: This sample only supports one channel???2 bytes Little Endian
            output.write(convertToLittleEndian((short)1));
            // Sample rate: The sample rate in hertz???4 bytes Little Endian
            output.write(convertToLittleEndian(SAMPLE_RATE_IN_HZ));
            // Bit rate: SampleRate * NumChannels * BitsPerSample/8???4 bytes Little Endian
            output.write(convertToLittleEndian(SAMPLE_RATE_IN_HZ * 2));
            // Block align: NumChannels * BitsPerSample/8???2 bytes Little Endian
            output.write(convertToLittleEndian((short)2));
            // Bits per sample: 16 bits???2 bytes Little Endian
            output.write(convertToLittleEndian((short)16));
            // Subchunk 2 ID: "fmt " string in US-ASCII charset???4 bytes Big Endian
            output.write("data".getBytes(asciiCharset));
            // Subchunk 2 size: The size of the actual audio data???4 bytes Little Endian
            output.write(convertToLittleEndian(rawData.length));

            // Audio data:  Sound data bytes???Little Endian
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

    private String getResult() {

        String spokenText = spokenWords.getText().toString();
        String[] words = spokenText.split(" ");
        Log.d("WORD_COUNT",String.valueOf(words.length));
        float result = (float)(words.length)/speechTime;
        return String.format("%.4f",result);
    }

    private String getFinalResult() {

        Finalresult = (diff/Float.parseFloat(getIntent().getStringExtra("singleTaskResult"))) * 100;
        return String.format("%.2f",Finalresult);
    }

    public interface RecordingListener {
        void onRecordingSucceeded(File output);
        void onRecordingFailed(Exception e);
    }

}