package mobile.application3DE.Visual;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import mobile.application3DE.R;
import mobile.application3DE.Visual.SQLite.Sqlitedb;
import mobile.application3DE.Visual.shapes.Shape10;
import mobile.application3DE.Visual.shapes.Shape2;
import mobile.application3DE.Visual.shapes.Shape3;
import mobile.application3DE.Visual.shapes.Shape5;
import mobile.application3DE.Visual.shapes.Shape6;
import mobile.application3DE.Visual.shapes.Shape7;
import mobile.application3DE.Visual.shapes.Shape8;
import mobile.application3DE.Visual.shapes.Shape9;
import mobile.application3DE.Visual.shapes.shape4;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Random;

import me.panavtec.drawableview.DrawableView;
import me.panavtec.drawableview.DrawableViewConfig;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class Third extends AppCompatActivity {

    private DrawableView drawableView;
    private DrawableViewConfig config = new DrawableViewConfig();
    Button button;
    Button button1;
    TextView textView1;
    private Integer datacount;
    String drawingLevel;
    String confidence;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_third);

        initUi();
    }

    private void initUi() {
        drawableView = (DrawableView) findViewById(R.id.paintView);
        Button strokeWidthMinusButton = (Button) findViewById(R.id.strokeWidthMinusButton);
        Button strokeWidthPlusButton = (Button) findViewById(R.id.strokeWidthPlusButton);
        Button changeColorButton = (Button) findViewById(R.id.changeColorButton);
        Button undoButton = (Button) findViewById(R.id.undoButton);
        button = (Button) findViewById(R.id.next);
        button1 = (Button) findViewById(R.id.result);
        textView1 = (TextView) findViewById(R.id.time);

        config.setStrokeColor(getResources().getColor(android.R.color.black));
        config.setShowCanvasBounds(true);
        config.setStrokeWidth(20.0f);
        config.setMinZoom(1.0f);
        config.setMaxZoom(3.0f);
        config.setCanvasHeight(1080);
        config.setCanvasWidth(1920);
        drawableView.setConfig(config);

        datacount = getIntent().getExtras().getInt("imgCount");
        System.out.println("Wtest"+datacount);

        if (datacount == 10){
            Toast.makeText(Third.this, "ffffffffffffff", Toast.LENGTH_SHORT).show();
        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            Intent intent = new Intent(Third.this, Results.class);
            startActivity(intent);
            }
        });

//        button1.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                APIcall();
//                new AsyncTaskExample2().execute();
//            }
//        });

        if (datacount == 1){

            new CountDownTimer(10000, 1000) {

                public void onTick(long millisUntilFinished) {

                    textView1.setText("seconds remaining: " + millisUntilFinished / 1000);
                }

                public void onFinish() {
                    new AsyncTaskExample2().execute();
                    Intent intent = new Intent(Third.this, Shape2.class);
                    intent.putExtra("correctCount", datacount);
                    startActivity(intent);
                }
            }.start();

        }else if(datacount == 2){
            new CountDownTimer(10000, 1000) {

                public void onTick(long millisUntilFinished) {

                    textView1.setText("seconds remaining: " + millisUntilFinished / 1000);
                }

                public void onFinish() {
                    new AsyncTaskExample2().execute();
                    Intent intent = new Intent(Third.this, Shape3.class);
                    intent.putExtra("correctCount", datacount);
                    startActivity(intent);
                }
            }.start();
        }
        else if(datacount == 3){
            new CountDownTimer(10000, 1000) {

                public void onTick(long millisUntilFinished) {
                    textView1.setText("seconds remaining: " + millisUntilFinished / 1000);
                }

                public void onFinish() {
                    new AsyncTaskExample2().execute();
                    Intent intent = new Intent(Third.this, shape4.class);
                    intent.putExtra("correctCount", datacount);
                    startActivity(intent);
                }
            }.start();
        }
        else if(datacount == 4){
            new CountDownTimer(10000, 1000) {

                public void onTick(long millisUntilFinished) {
                    textView1.setText("seconds remaining: " + millisUntilFinished / 1000);
                }

                public void onFinish() {
                    new AsyncTaskExample2().execute();
                    Intent intent = new Intent(Third.this, Shape5.class);
                    intent.putExtra("correctCount", datacount);
                    startActivity(intent);
                }
            }.start();
        }
        else if(datacount == 5){
            new CountDownTimer(15000, 1000) {

                public void onTick(long millisUntilFinished) {
                    textView1.setText("seconds remaining: " + millisUntilFinished / 1000);
                }

                public void onFinish() {
                    new AsyncTaskExample2().execute();
                    Intent intent = new Intent(Third.this, Shape6.class);
                    intent.putExtra("correctCount", datacount);
                    startActivity(intent);
                }
            }.start();
        }
        else if(datacount == 6){
            new CountDownTimer(10000, 1000) {

                public void onTick(long millisUntilFinished) {
                    textView1.setText("seconds remaining: " + millisUntilFinished / 1000);
                }

                public void onFinish() {
                    new AsyncTaskExample2().execute();
                    Intent intent = new Intent(Third.this, Shape7.class);
                    intent.putExtra("correctCount", datacount);
                    startActivity(intent);
                }
            }.start();
        }

        else if(datacount == 7){
            new CountDownTimer(10000, 1000) {

                public void onTick(long millisUntilFinished) {
                    textView1.setText("seconds remaining: " + millisUntilFinished / 1000);
                }

                public void onFinish() {
                    new AsyncTaskExample2().execute();
                    Intent intent = new Intent(Third.this, Shape8.class);
                    intent.putExtra("correctCount", datacount);
                    startActivity(intent);
                }
            }.start();
        }

        else if(datacount == 8){
            new CountDownTimer(10000, 1000) {

                public void onTick(long millisUntilFinished) {

                    textView1.setText("seconds remaining: " + millisUntilFinished / 1000);
                }

                public void onFinish() {
                    new AsyncTaskExample2().execute();
                    Intent intent = new Intent(Third.this, Shape9.class);
                    intent.putExtra("correctCount", datacount);
                    startActivity(intent);
                }
            }.start();
        }

        else if(datacount == 9){
            new CountDownTimer(10000, 1000) {

                public void onTick(long millisUntilFinished) {

                    textView1.setText("seconds remaining: " + millisUntilFinished / 1000);
                }

                public void onFinish() {
                    new AsyncTaskExample2().execute();
                    Intent intent = new Intent(Third.this, Shape10.class);
                    intent.putExtra("correctCount", datacount);
                    startActivity(intent);
                }
            }.start();
        }
        else if(datacount == 10){

            new CountDownTimer(10000, 1000) {

                public void onTick(long millisUntilFinished) {

                    textView1.setText("seconds remaining: " + millisUntilFinished / 1000);
                }

                public void onFinish() {
                    new AsyncTaskExample2().execute();
                    Intent intent = new Intent(Third.this, Results.class);
                    intent.putExtra("correctCount", datacount);
                    startActivity(intent);
                }
            }.start();
        }

        strokeWidthPlusButton.setOnClickListener(new View.OnClickListener() {

            @Override public void onClick(View v) {
                config.setStrokeWidth(config.getStrokeWidth() + 10);
            }
        });
        strokeWidthMinusButton.setOnClickListener(new View.OnClickListener() {

            @Override public void onClick(View v) {
                config.setStrokeWidth(config.getStrokeWidth() - 10);
            }
        });
        changeColorButton.setOnClickListener(new View.OnClickListener() {

            @Override public void onClick(View v) {
                Random random = new Random();
                config.setStrokeColor(
                        Color.argb(255, random.nextInt(256), random.nextInt(256), random.nextInt(256)));
            }
        });
        undoButton.setOnClickListener(new View.OnClickListener() {

            @Override public void onClick(View v) {
                drawableView.undo();
            }
        });



    }


    private class AsyncTaskExample2 extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected void onPostExecute(String s) {


        }

        @Override
        protected String doInBackground(String... strings) {

            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss", Locale.ENGLISH);
            String filename = sdf.format(new Date());

            try {
                String path = getApplicationContext().getFilesDir().getPath();
                OutputStream fOut = null;
                File file = new File(path, "MYFile");
                if (!file.exists()) {
                    file.mkdirs();
                }

                File file2 = new File(file, filename + ".png");
                fOut = new FileOutputStream(file2);


                Bitmap test = drawableView.obtainBitmap();
                test.compress(Bitmap.CompressFormat.PNG, 100, fOut);
                fOut.flush();
                fOut.close();

                String BASE_URL = "http://192.168.8.101:5000/";
                OkHttpClient client = new OkHttpClient();

                RequestBody requestBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("input_code", datacount.toString())
                        .addFormDataPart(
                                "image", file2.getName(),
                                RequestBody.create(MediaType.parse("multipart/form-data"), file2)
                        )
                        .build();

                System.out.println("dddddddddddddddddddddddd" + requestBody);

                Request request = new Request.Builder()
                        .url(BASE_URL)
                        .post(requestBody)
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    ResponseBody body = response.body();
                    JSONObject json = new JSONObject(body.string());
                    System.out.println(json);
                    JSONArray array = (JSONArray) json.get("data");
                    System.out.println("aaaaaaaaaaaaaaaa" + array);

                    for (int xx = 0; xx < array.length(); xx++) {
                        JSONObject joo = array.getJSONObject(xx);

                        HashMap<String, String> stringStringHashMap = new HashMap<>();
                        drawingLevel = joo.getString("drawing_level");
                        confidence = joo.getString("model_confident");
                        System.out.println(drawingLevel);
                        System.out.println(confidence);

                        Sqlitedb.InsertData(Third.this, "Predict_Data",
                                "drawing_level,model_confident",
                                "'" + drawingLevel + "','"
                                        + confidence + "'");
                    }

//                JSONObject firstElement = (JSONObject) array.get(0);
//                queries = String.valueOf(firstElement.get("rating"));
//                System.out.println("qqqqqqqqqqqqqqqqqqqqqqqqqq"+queries);
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }


            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}