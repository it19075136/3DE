package mobile.application3DE.verbalMemory;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import mobile.application3DE.R;
import mobile.application3DE.utilities.BaseActivity;
import mobile.application3DE.verbalMemory.ResultActivity;

public class ProgressActivity extends BaseActivity {
    ImageView xx,xxx;
    private DatabaseReference mDatabase;
    SharedPreferences pref ;
    SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress);
        xx=findViewById(R.id.xx);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        xxx=findViewById(R.id.xxx);
        pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
        editor = pref.edit();
        final Animation anim = AnimationUtils.loadAnimation(this, R.anim.animationrotate);
        xx.startAnimation(anim);
        xxx.startAnimation(anim);
        mDatabase.child("Test").child("Result").child("result").addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String data =(String) dataSnapshot.getValue(String.class);
                        if(!(data.contentEquals("x"))){
                            mDatabase.child("Test").child("Result").child("result").setValue("x");
                            if(pref.getBoolean("notComplete", true)){
                                editor.putString("IR",data); // Storing string
                                editor.commit();
                                mDatabase.child("Result").child(pref.getString("email","null")).child("IR").setValue(data);
                                Intent i1 = new Intent(getApplicationContext(), WaitingActivity.class);
                                i1.putExtra("value",data);
                                startActivity(i1);
                            }
                            else{
                                editor.putString("DR",data); // Storing string
                                editor.commit();
                                mDatabase.child("Result").child(pref.getString("email","null")).child("DR").setValue(data);
                                Intent i1 = new Intent(getApplicationContext(), ResultActivity.class);
                                i1.putExtra("value",data);
                                startActivity(i1);
                            }

                        }
                        //Log.d("dd", data);
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Getting Post failed, log a message
                        Log.w("dd", "loadPost:onCancelled", databaseError.toException());
                        // ...
                    }
                });
    }
}