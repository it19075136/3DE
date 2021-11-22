package mobile.application3DE;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.core.cartesian.series.Line;
import com.anychart.data.Mapping;
import com.anychart.data.Set;
import com.anychart.enums.Anchor;
import com.anychart.enums.MarkerType;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import mobile.application3DE.models.FinalResult;
import mobile.application3DE.models.Guardian;
import mobile.application3DE.utilities.FinalResultsAdapter;

public class FinalResultsStats extends AppCompatActivity {

    RecyclerView recyclerView;
    FinalResultsAdapter finalResultsAdapter;

    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

    // we will get a DatabaseReference for the database root node
    DatabaseReference databaseReference = firebaseDatabase.getReference();

    DatabaseReference finalResultsRef;
    String currentUser;
    List<FinalResult> finalResults;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_final_results_stats);
        recyclerView = findViewById(R.id.recycler_view);

        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
// Adding signed in user.

        if(acct != null)
            currentUser = acct.getId();

        finalResultsRef = databaseReference.child("FinalResults/"+currentUser);
        setRecyclerView();

    }

    private void setRecyclerView() {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        finalResultsRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                DataSnapshot dataSnapshot = task.getResult();
                finalResults = new ArrayList<>();
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    String timeStamp = ds.child("date").getValue(String.class);
                    String value = ds.child("status").getValue(String.class);
                    finalResults.add(new FinalResult(timeStamp,value));
                    finalResultsAdapter = new FinalResultsAdapter(FinalResultsStats.this,finalResults);
                    recyclerView.setAdapter(finalResultsAdapter);
                }
            }});

    }

//    private List<FinalResult> getList(){
//
//        finalResultsRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
//             @Override
//                    public void onComplete(@NonNull Task<DataSnapshot> task) {
//                        DataSnapshot dataSnapshot = task.getResult();
//                        for(DataSnapshot ds : dataSnapshot.getChildren()) {
//                            String timeStamp = ds.child("timestamp").getValue(String.class);
//                            String value = ds.child("status").getValue(String.class);
//                            finalResults.add(new FinalResult(timeStamp,value));
//                        }
//             }});
//    }

}