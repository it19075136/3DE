package mobile.application3DE;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileWriter;

import mobile.application3DE.utilities.BaseActivity;

public class SignInPage extends BaseActivity {

    GoogleSignInClient mGoogleSignInClient;
    SignInButton gSignIn;
    private static final int RECORD_PERMISSIONS_REQUEST_CODE = 15623;

    // we will get the default FirebaseDatabase instance
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

    // we will get a DatabaseReference for the database root node
    DatabaseReference databaseReference = firebaseDatabase.getReference();
    DatabaseReference userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in_page);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        gSignIn = findViewById(R.id.gSignIn);
        gSignIn.setSize(SignInButton.SIZE_WIDE);

        gSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn(null);
            }
        });
    }

    private void signIn(GoogleSignInAccount account) {
        final Intent[] intent = {null};
        if(account != null) {
            userRef = databaseReference.child("users/"+account.getId());
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists())
                        intent[0] = new Intent(getApplicationContext(), StartFullTest.class);
                    else if(intent[0] == null)
                        intent[0] = new Intent(getApplicationContext(), Landing.class);
                    intent[0].putExtra("email",account.getEmail());
                    intent[0].putExtra("id",account.getId());
                    if (!hasRequiredPermissions(getApplicationContext()))
                        requestRequiredPermissions(SignInPage.this);
                    writeFileOnInternalStorage(getApplicationContext(),"user.txt",account.getId());
                    startActivity(intent[0]);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        else{
                intent[0] = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(intent[0],1);
            }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == 1) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> task) {
        try {
            GoogleSignInAccount account = task.getResult(ApiException.class);

            // Signed in successfully, show authenticated UI.
            signIn(account);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            signIn(null);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        signIn(account);

    }

    public void writeFileOnInternalStorage(Context mcoContext, String sFileName, String sBody){

        try {
            if (!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
                Toast.makeText(getApplicationContext(), Environment.getExternalStorageState(),Toast.LENGTH_LONG).show();
                return;
            }
            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), sFileName);
            FileWriter writer = new FileWriter(file);
            writer.append(sBody);
            writer.flush();
            writer.close();
        } catch (Exception e){
            e.printStackTrace();
        }
    }


    public void requestRequiredPermissions(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            activity.requestPermissions(
                    new String[]{
                            Manifest.permission.MANAGE_EXTERNAL_STORAGE
                    },
                    RECORD_PERMISSIONS_REQUEST_CODE
            );
        }
    }

    public boolean hasRequiredPermissions(Context context) {
        int writeExternalStoragePermissionCheck = ContextCompat.checkSelfPermission(
                context, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int manageExternalStoragePermissionCheck = ContextCompat.checkSelfPermission(
                context, Manifest.permission.MANAGE_EXTERNAL_STORAGE);
        return  writeExternalStoragePermissionCheck == PackageManager.PERMISSION_GRANTED &&
                manageExternalStoragePermissionCheck == PackageManager.PERMISSION_GRANTED;
    }

}