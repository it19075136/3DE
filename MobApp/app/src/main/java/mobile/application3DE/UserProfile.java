package mobile.application3DE;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mobile.application3DE.models.User;
import mobile.application3DE.utilities.LocaleManager;

public class UserProfile extends AppCompatActivity {

    AutoCompleteTextView language;
    ArrayList<String> langObjs;
    String userId,lang;
    HashMap<String,String> user;
    TextInputEditText fName,lName,mail;
    MaterialTextView guardianName;

    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference = firebaseDatabase.getReference();
    DatabaseReference userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        language = findViewById(R.id.lang);
        fName = findViewById(R.id.fName);
        lName = findViewById(R.id.lName);
        guardianName = findViewById(R.id.guardianName);
        mail = findViewById(R.id.mail);
        mail.setEnabled(false);
        fName.setEnabled(false);
        lName.setEnabled(false);

        langObjs = new ArrayList();

        langObjs.add((String) getText(R.string.sinhala));
        langObjs.add((String) getText(R.string.english));

        ArrayAdapter adapter = new ArrayAdapter(this,R.layout.lang_item,langObjs);
        language.setAdapter(adapter);

//        if (getString(R.string.language).equals(getString(R.string.sinhala)))
//            language.setText(getText(R.string.sinhala));
//        else
//            language.setText(getText(R.string.english));

        userId = GoogleSignIn.getLastSignedInAccount(this).getId();
        userRef = databaseReference.child("users/"+userId);

        userRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                user = (HashMap<String, String>) task.getResult().getValue();
                Snackbar.make(findViewById(android.R.id.content).getRootView(),"Hi " + user.get("_fName"),Snackbar.LENGTH_SHORT).show();
                fName.setText(user.get("_fName"));
                lName.setText(user.get("_lName"));
                mail.setText(user.get("email"));
                guardianName.setText(user.get("guardianName"));
                fName.setEnabled(true);
                lName.setEnabled(true);
            }
        });
    }

    public void update(View view) {

        if (language.getText().toString().equals(getText(R.string.sinhala))) {
            lang = "si";
            LocaleManager.setNewLocale(this, LocaleManager.SINHALA);
        }
        else {
            lang = "en";
            LocaleManager.setNewLocale(this, LocaleManager.ENGLISH);
        }

        user.clear();

        user.put("_fName",fName.getText().toString());
        user.put("_lName",lName.getText().toString());
        user.put("language",lang);
        userRef.updateChildren((Map) user, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                Snackbar.make(findViewById(android.R.id.content).getRootView(),"Update successful",Snackbar.LENGTH_SHORT).show();
            }
        });
    }

}