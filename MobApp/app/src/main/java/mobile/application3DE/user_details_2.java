package mobile.application3DE;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import mobile.application3DE.models.User;

public class user_details_2 extends AppCompatActivity {

    TextInputEditText g_name_,g_mobile_,g_mail_;
    User u;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference dbRef;
    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details_2);

        Intent i = getIntent();
        u = (User) i.getSerializableExtra("user");
        g_name_ = findViewById(R.id.gname);
        g_mobile_ = findViewById(R.id.gphone);
        g_mail_ = findViewById(R.id.gmail);

    }

    public void submit(View view) {

        boolean success = true;

        if(!g_name_.getText().toString().matches("") && g_mobile_.getText().toString().matches("") && g_mail_.getText().toString().matches("")) {
            g_mobile_.setError("Required");
            g_mail_.setError("Required");
            success = false;
        }
        if(success){
            u.setGuardianName(g_name_.getText().toString());
            u.setGuardianMob(g_mobile_.getText().toString());
            u.setGuardianMail(g_mail_.getText().toString());

            firebaseDatabase = FirebaseDatabase.getInstance("https://project-3de-eb7dd-default-rtdb.firebaseio.com/");
            dbRef = firebaseDatabase.getReference("users/".concat(u.getId()));

            dbRef.setValue(u);

            Resources resources = getResources();

            Snackbar.make(view,"Registration Successful!",Snackbar.LENGTH_SHORT).setBackgroundTint(resources.getColor(R.color.success)).show();

            Intent i = new Intent(this, activity_choice_landing.class);
            i.putExtra("fname",u.get_fName());
            Thread thread = new Thread(){
                @Override
                public void run() {
                    try {
                        Thread.sleep(500);
                        startActivity(i);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };

            thread.start();

        }

    }

}