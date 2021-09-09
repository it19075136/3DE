package mobile.application3DE;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import mobile.application3DE.utilities.LocaleManager;

public class UserDetails extends Fragment {

    AutoCompleteTextView language;
    ArrayList<String> langObjs;
    String userId,lang;
    HashMap<String,String> user;
    TextInputEditText fName,lName,mail;
    Button submitBtn;

    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference = firebaseDatabase.getReference();
    DatabaseReference userRef;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile_management,container,false);
        language = view.findViewById(R.id.lang);
        fName = view.findViewById(R.id.fName);
        lName = view.findViewById(R.id.lName);
        mail = view.findViewById(R.id.mail);
        mail.setEnabled(false);
        fName.setEnabled(false);
        lName.setEnabled(false);
        submitBtn = view.findViewById(R.id.submitBtn);
        submitBtn.setEnabled(false);

        langObjs = new ArrayList();

        langObjs.add((String) getText(R.string.sinhala));
        langObjs.add((String) getText(R.string.english));

        ArrayAdapter adapter = new ArrayAdapter(getContext(),R.layout.lang_item,langObjs);
        language.setAdapter(adapter);

        if (getString(R.string.language).equals(getString(R.string.sinhala))) {
                    language.setText((CharSequence) adapter.getItem(0),false);
        } else
                    language.setText((CharSequence) adapter.getItem(1),false);

        userId = GoogleSignIn.getLastSignedInAccount(getContext()).getId();
        userRef = databaseReference.child("users/"+userId);

        userRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                user = (HashMap<String, String>) task.getResult().getValue();
//                Snackbar.make(view.findViewById(android.R.id.content).getRootView(),"Hi " + user.get("_fName"),Snackbar.LENGTH_SHORT).show();
                fName.setText(user.get("_fName"));
                lName.setText(user.get("_lName"));
                mail.setText(user.get("email"));
                fName.setEnabled(true);
                lName.setEnabled(true);
                submitBtn.setEnabled(true);
            }
        });

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (language.getText().toString().equals(getText(R.string.sinhala))) {
                    lang = "si";
                    LocaleManager.setNewLocale(getActivity(), LocaleManager.SINHALA);
                }
                else {
                    lang = "en";
                    LocaleManager.setNewLocale(getActivity(), LocaleManager.ENGLISH);
                }

                user.clear();

                user.put("_fName",fName.getText().toString());
                user.put("_lName",lName.getText().toString());
                user.put("language",lang);
                userRef.updateChildren((Map) user, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
//                        Snackbar.make(view.findViewById(android.R.id.content).getRootView(),"Update successful",Snackbar.LENGTH_SHORT).show();
                        Toast.makeText(getContext(),"Update successful",Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(getContext(),ProfileManagement.class);
                        startActivity(i);
                    }
                });
            }
            }
        );

        return view;
    }

}
