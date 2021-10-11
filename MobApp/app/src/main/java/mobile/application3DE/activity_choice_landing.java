package mobile.application3DE;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;

import mobile.application3DE.decisionMaking.Dec_making_task;
import mobile.application3DE.orientation.AttentionChoiceLanding;
import mobile.application3DE.orientation.AttentionResultChartView;
import mobile.application3DE.orientation.OrientationChoice;
import mobile.application3DE.utilities.BaseActivity;

public class activity_choice_landing extends BaseActivity {

    Button walking,visualMemoryBtn,orientationBtn,talking,logout;
    Intent i;
//    GoogleSignInClient googleSignInClient;
//    private static final int TIME_INTERVAL = 2000;
//    private long mBackPressed;
//    private DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choice_landing);


        talking = findViewById(R.id.btn2);
//        visualMemoryBtn = findViewById(R.id.btn4);
//        orientationBtn = findViewById(R.id.btn3);
        walking = findViewById(R.id.btn1);

//        Toolbar toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//
//        drawer = findViewById(R.id.drawer_layout);
//        NavigationView navigationView = findViewById(R.id.nav_view);
//        navigationView.setNavigationItemSelectedListener(this);
//
//        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
//        drawer.addDrawerListener(actionBarDrawerToggle);
//        actionBarDrawerToggle.syncState();
//
//        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                .requestEmail()
//                .build();
//
//        googleSignInClient = GoogleSignIn.getClient(this, gso);

        talking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                i = new Intent(getApplicationContext(), mobile.application3DE.verbalMemory.LevelMusicPlayActivity.class);
                startActivity(i);
            }
        });
        walking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                i = new Intent(getApplicationContext(), mobile.application3DE.Visual.MainActivity.class);
                startActivity(i);
            }
        });

//        decisionBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                i = new Intent(getApplicationContext(), Dec_making_task.class);
//                startActivity(i);
//            }
//        });

    }

//    @Override
//    public void onBackPressed() {
//
//        if (drawer.isDrawerOpen(GravityCompat.START)) {
//            drawer.closeDrawer(GravityCompat.START);
//        }
//
//        if (mBackPressed + TIME_INTERVAL > System.currentTimeMillis()) {
//            finish();
//            moveTaskToBack(true);
//        } else {
//            Toast.makeText(getBaseContext(), "Double tap back to exit", Toast.LENGTH_SHORT).show();
//        }
//        mBackPressed = System.currentTimeMillis();
//
//    }

//    public void directToOrientation(View view) {
//
//        i = new Intent(this, OrientationChoice.class);
//        startActivity(i);
//
//    }
//
//    @Override
//    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.nav_account:
//                i = new Intent(this, ProfileManagement.class);
//                startActivity(i);
//                break;
//            case R.id.nav_attemtion_test:
//                i = new Intent(this, AttentionChoiceLanding.class);
//                i.putExtra("type","once");
//                startActivity(i);
//                break;
//            case R.id.nav_testing:
//                i = new Intent(this, ChoiceLandingLegacy.class);
//                startActivity(i);
//                break;
//            case R.id.nav_settings:
//
//                break;
//            case R.id.nav_help:
//
//                break;
//            case R.id.nav_reports:
//                i = new Intent(this, AttentionResultChartView.class);
//                startActivity(i);
//                break;
//            case R.id.nav_logout:
//                googleSignInClient.signOut()
//                        .addOnCompleteListener(new OnCompleteListener<Void>() {
//                            @Override
//                            public void onComplete(@NonNull Task<Void> task) {
//                                i = new Intent(activity_choice_landing.this, SignInPage.class);
//                                startActivity(i);
//                            }
//                        });
//                break;
//            default:
//                break;
//        }
//        drawer.closeDrawer(GravityCompat.START);
//        return true;
//    }
}