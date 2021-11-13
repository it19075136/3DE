package mobile.application3DE.orientation;


import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import mobile.application3DE.FinalResultActivity;
import mobile.application3DE.ProfileManagement;
import mobile.application3DE.R;
import mobile.application3DE.models.Guardian;
import mobile.application3DE.models.User;
import mobile.application3DE.utilities.BaseActivity;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AttentionResultsPage extends BaseActivity {

    MaterialTextView diff,results;
    String diffText,resultsText;
    DatabaseReference userRef,finalResultRef;
    String currentUser;
    OkHttpClient client;
    HashMap<String,String> user;
    Guardian g1,g2;
    JSONObject mailObj,userObj;
    boolean isGuardianExist = true;
    Intent intent;

    // we will get the default FirebaseDatabase instance
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

    // we will get a DatabaseReference for the database root node
    DatabaseReference databaseReference = firebaseDatabase.getReference();

    private static final String URL = "https://three-de.herokuapp.com/mail/api";


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

        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
// Adding signed in user.

        if(acct != null)
            currentUser = acct.getId();

        userRef = databaseReference.child("users/"+currentUser);

        //CREATING HTTP CLIENT
        client = new OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(20, TimeUnit.SECONDS)
                .build();

        mailObj = new JSONObject();

        createRequestPayload(); // creating mail object for request payload

    }

    private void sendMailRequest() {

        Request req = new Request.Builder()
                .url(URL)
                .post(RequestBody.create(MediaType.parse("application/json"), String.valueOf(mailObj)))
                .build();

        client.newCall(req).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                call.cancel();
                AttentionResultsPage.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(AttentionResultsPage.this, e.getMessage()+", Poor network connection,Please try again",Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) {

                if(response.isSuccessful()) {
                    AttentionResultsPage.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Snackbar.make(findViewById(android.R.id.content).getRootView(), "Results have been sent to the guardians!", Snackbar.LENGTH_LONG).setBackgroundTint(getResources().getColor(R.color.success)).show();
                        }
                    });
                    String type = getIntent().getStringExtra("type");
                    if (type.equals("gen")) {
                        intent = new Intent(AttentionResultsPage.this, FinalResultActivity.class);
                        intent.putExtra("user", user);
                        String res = "Mild";
                        if (Double.parseDouble(getIntent().getStringExtra("result")) < 37.00)
                            res = "No";
                        intent.putExtra("result", res);
                        startActivity(intent);
                    }
                }
                else
                    AttentionResultsPage.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Snackbar.make(findViewById(android.R.id.content).getRootView(), "Failed to send results to the guardians", Snackbar.LENGTH_LONG).setBackgroundTint(getResources().getColor(R.color.error)).show();
                        }
                    });
            }
        });
    }

    private void createRequestPayload() {

        userRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                user = (HashMap<String, String>) task.getResult().getValue();
                g1 = new Guardian(user.get("guardianName"),user.get("guardianMail"),user.get("guardianMob"));
                g2 = new Guardian(user.get("guardianName2"),user.get("guardianMail2"),user.get("guardianMob2"));

                if((Objects.equals(g1.getGuardianMail(), "") || g1.getGuardianMail() == null) && (Objects.equals(g2.getGuardianMail(), "") || g2.getGuardianMail() == null)) {
                    Toast.makeText(AttentionResultsPage.this, "No Guardian Mails Available,Update your guardians", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(AttentionResultsPage.this, ProfileManagement.class);
                    isGuardianExist = false;
                    startActivityForResult(intent,200);
                }

                ArrayList<String> recipients = new ArrayList<>();
                if ((!Objects.equals(g1.getGuardianMail(), "") && g1.getGuardianMail() != null))
                    recipients.add(g1.getGuardianMail());
                if ((!Objects.equals(g2.getGuardianMail(), "") && g2.getGuardianMail() != null))
                    recipients.add(g2.getGuardianMail());
                recipients.add(user.get("email"));

                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

                userObj = new JSONObject();

                try {
                    userObj.put("name",user.get("_fName")+" "+user.get("_lName"));
//                    userObj.put("age",((dateFormat.parse(dateFormat.format(new Date())).getTime() - (dateFormat.parse(user.get("_dob")).getTime())) / (1000 * 60 * 60 * 24)));
                    userObj.put("age",user.get("_dob"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                JsonArray recipientsJSON = new Gson().toJsonTree(recipients).getAsJsonArray();

                try {
                    mailObj.put("recipients",recipientsJSON);
                    mailObj.put("patient", userObj);
                    mailObj.put("results", diff.getText()+" ,"+results.getText());
                    mailObj.put("suggestions", "");
                    mailObj.put("type", "attention");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Toast.makeText(AttentionResultsPage.this,String.valueOf(mailObj),Toast.LENGTH_LONG).show();
                if(isGuardianExist)
                    sendMailRequest(); //sending request and handling response
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 200) {
            isGuardianExist = true;
            createRequestPayload();
        }
    }
}