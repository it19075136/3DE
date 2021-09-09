package mobile.application3DE;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

import mobile.application3DE.models.Guardian;

public class GuardianDetails extends Fragment {

    String userId;
    HashMap<String,String> user;
    TextInputEditText guardianName,guardianMail,guardianPhone,guardianName2,guardianMail2,guardianPhone2;
    Button submitBtn,addGuard;
    LinearLayout guard1,guard2;
    Guardian g1,g2;
    ImageView delete2;

    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference = firebaseDatabase.getReference();
    DatabaseReference userRef;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_guardian_details,container,false);

        guard1 = view.findViewById(R.id.guard1);
        guard2 = view.findViewById(R.id.guard2);
            guardianName = view.findViewById(R.id.guardianName);
            guardianName.setEnabled(false);
            guardianMail = view.findViewById(R.id.guardianMail);
            guardianMail.setEnabled(false);
            guardianPhone = view.findViewById(R.id.guardianPhone);
            guardianPhone.setEnabled(false);
            guardianName2 = view.findViewById(R.id.guardianName2);
            guardianName2.setEnabled(false);
            guardianMail2 = view.findViewById(R.id.guardianMail2);
            guardianMail2.setEnabled(false);
            guardianPhone2 = view.findViewById(R.id.guardianPhone2);
            guardianPhone2.setEnabled(false);
            submitBtn = view.findViewById(R.id.submitBtn);
            submitBtn.setEnabled(false);
            addGuard = view.findViewById(R.id.addGuard);
            delete2 = view.findViewById(R.id.delete2);

            userId = GoogleSignIn.getLastSignedInAccount(getContext()).getId();
            userRef = databaseReference.child("users/"+userId);

            userRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    user = (HashMap<String, String>) task.getResult().getValue();
                    g1 = new Guardian(user.get("guardianName"),user.get("guardianMail"),user.get("guardianMob"));
                    g2 = new Guardian(user.get("guardianName2"),user.get("guardianMail2"),user.get("guardianMob2"));
                    if (g2.getGuardianName() != null && !g2.getGuardianName().equals("")) {
                        guardianName2.setText(user.get("guardianName2"));
                        guardianMail2.setText(user.get("guardianMail2"));
                        guardianPhone2.setText(user.get("guardianMob2"));
                        guardianName2.setEnabled(true);
                        guardianMail2.setEnabled(true);
                        guardianPhone2.setEnabled(true);
                        addGuard.setEnabled(false);
                    }
                    else {
                        guard2.setVisibility(View.INVISIBLE);
                    }

                    guardianName.setText(user.get("guardianName"));
                    guardianMail.setText(user.get("guardianMail"));
                    guardianPhone.setText(user.get("guardianMob"));
                    guardianName.setEnabled(true);
                    guardianMail.setEnabled(true);
                    guardianPhone.setEnabled(true);
                    submitBtn.setEnabled(true);
                }
            });

            submitBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    user.clear();

                    user.put("guardianMail",guardianMail.getText().toString());
                    user.put("guardianMob",guardianPhone.getText().toString());
                    user.put("guardianName",guardianName.getText().toString());
                    user.put("guardianMail2", guardianMail2.getText().toString());
                    user.put("guardianMob2", guardianPhone2.getText().toString());
                    user.put("guardianName2", guardianName2.getText().toString());

                    userRef.updateChildren((Map) user, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
//                            Snackbar.make(view.findViewById(android.R.id.content).getRootView(),"Update successful",Snackbar.LENGTH_SHORT).show();
                            Toast.makeText(getContext(),"Update successful",Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });

            addGuard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    guard2.setVisibility(View.VISIBLE);
                    addGuard.setEnabled(false);
                    guardianName2.setEnabled(true);
                    guardianMail2.setEnabled(true);
                    guardianPhone2.setEnabled(true);
                    Toast.makeText(getContext(),"You can only add maximum of 2 guardians",Toast.LENGTH_SHORT).show();
                }
            });

            delete2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    guard2.setVisibility(View.INVISIBLE);
                    guardianName2.setText("");
                    guardianMail2.setText("");
                    guardianPhone2.setText("");
                    addGuard.setEnabled(true);
                }
            });

        return view;
    }

}
