package mobile.application3DE;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import mobile.application3DE.utilities.BaseActivity;
import mobile.application3DE.utilities.LocaleManager;

public class Landing extends BaseActivity {

    TextView selected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);

    }

    public boolean onLangSelected(View view) {
        switch (view.getId()) {
            case R.id.sinhala:
                setNewLocale(this, LocaleManager.SINHALA);
                return true;
            case R.id.english:
                setNewLocale(this, LocaleManager.ENGLISH);
                return true;
        }

        return true;
    }

    private void setNewLocale(AppCompatActivity mContext, @LocaleManager.LocaleDef String language) {
        LocaleManager.setNewLocale(getApplicationContext(), language);
        Intent intent = new Intent(this,MainActivity.class);
        intent.putExtra("lang",language);
        intent.putExtra("email",getIntent().getStringExtra("email"));
        intent.putExtra("id",getIntent().getStringExtra("id"));
        startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));

    }

}