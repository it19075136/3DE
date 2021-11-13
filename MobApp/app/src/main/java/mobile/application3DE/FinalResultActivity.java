package mobile.application3DE;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.google.gson.JsonArray;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

import mobile.application3DE.models.Guardian;
import mobile.application3DE.models.User;
import mobile.application3DE.orientation.AttentionResultsPage;
import mobile.application3DE.utilities.BaseActivity;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class FinalResultActivity extends BaseActivity {

    // we will get the default FirebaseDatabase instance
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

    // we will get a DatabaseReference for the database root node
    DatabaseReference databaseReference = firebaseDatabase.getReference();

    DatabaseReference finalResultRef,userRef;
    SimpleDateFormat formatDate;
    String currentUser;
    MaterialTextView emails,result,date,user;
    HashMap<String,String> u;
    MaterialButton btnHome;
    OkHttpClient client;
    Guardian g1,g2;
    JSONObject mailObj,userObj;
    boolean isGuardianExist = true;
    Intent intent;

    private static final String URL = "https://three-de.herokuapp.com/mail/api";

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_final_result);

        user = findViewById(R.id.user);
        date = findViewById(R.id.date);
        result = findViewById(R.id.result);
        emails = findViewById(R.id.emails);
        btnHome = findViewById(R.id.btnHome);
        btnHome.setEnabled(false);

        u = (HashMap<String, String>) getIntent().getSerializableExtra("user");

        formatDate = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

        user.setText(getString(R.string.resultsFor) + " " + u.get("_fName") + " " +u.get("_lName"));
        date.setText(getString(R.string.examDate) + formatDate.format(new Date()));
        result.setText(getString(R.string.demetiaStatus)+getIntent().getStringExtra("result"));
        emails.setText(getString(R.string.resultsSent)+"\n"+ u.get("email") + ",\n" +u.get("guardianMail"));

        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
// Adding signed in user.

        if(acct != null)
            currentUser = acct.getId();

        finalResultRef = databaseReference.child("FinalResults/"+currentUser);
        userRef = databaseReference.child("users/"+currentUser);

        finalResultRef.child(formatDate.format(new Date())).setValue(getIntent().getStringExtra("result")).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Toast.makeText(getApplicationContext(), "You've successfully completed the test!", Toast.LENGTH_LONG).show();
                    createRequestPayload();
                }

            });

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
                FinalResultActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(FinalResultActivity.this, e.getMessage()+", Poor network connection,Please try again",Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) {

                if(response.isSuccessful()) {
                    FinalResultActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Snackbar.make(findViewById(android.R.id.content).getRootView(), "Results have been sent to the guardians!", Snackbar.LENGTH_LONG).setBackgroundTint(getResources().getColor(R.color.success)).show();
                        }
                    });
                    btnHome.setEnabled(true);
                }
                else
                    FinalResultActivity.this.runOnUiThread(new Runnable() {
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
                u = (HashMap<String, String>) task.getResult().getValue();
                g1 = new Guardian(u.get("guardianName"),u.get("guardianMail"),u.get("guardianMob"));
                g2 = new Guardian(u.get("guardianName2"),u.get("guardianMail2"),u.get("guardianMob2"));

                if((Objects.equals(g1.getGuardianMail(), "") || g1.getGuardianMail() == null) && (Objects.equals(g2.getGuardianMail(), "") || g2.getGuardianMail() == null)) {
                    Toast.makeText(FinalResultActivity.this, "No Guardian Mails Available,Update your guardians", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(FinalResultActivity.this, ProfileManagement.class);
                    isGuardianExist = false;
                    startActivityForResult(intent,200);
                }

                ArrayList<String> recipients = new ArrayList<>();
                if ((!Objects.equals(g1.getGuardianMail(), "") && g1.getGuardianMail() != null))
                    recipients.add(g1.getGuardianMail());
                if ((!Objects.equals(g2.getGuardianMail(), "") && g2.getGuardianMail() != null))
                    recipients.add(g2.getGuardianMail());
                recipients.add(u.get("email"));

                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

                userObj = new JSONObject();

                try {
                    userObj.put("name",u.get("_fName")+" "+u.get("_lName"));
//                    userObj.put("age",((dateFormat.parse(dateFormat.format(new Date())).getTime() - (dateFormat.parse(user.get("_dob")).getTime())) / (1000 * 60 * 60 * 24)));
                    userObj.put("age",u.get("_dob"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                JsonArray recipientsJSON = new Gson().toJsonTree(recipients).getAsJsonArray();

                try {
                    mailObj.put("recipients",recipientsJSON);
                    mailObj.put("patient", userObj);
                    mailObj.put("results", "Dimentia status: ,"+result);
                    mailObj.put("suggestions", "");
                    mailObj.put("type", "final");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Toast.makeText(FinalResultActivity.this,String.valueOf(mailObj),Toast.LENGTH_LONG).show();
                if(isGuardianExist)
                    sendMailRequest(); //sending request and handling response
            }
        });
    }

    public void navigateHome(View view) {
        Intent intent = new Intent(this, StartFullTest.class);
        startActivity(intent);
    }

}