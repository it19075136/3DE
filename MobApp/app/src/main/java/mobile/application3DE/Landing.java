package mobile.application3DE;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import mobile.application3DE.utilities.BaseActivity;
import mobile.application3DE.utilities.LocaleManager;

public class Landing extends BaseActivity {

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
        LocaleManager.setNewLocale(this, language);
        Intent intent = new Intent(this,SignInPage.class);
        startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));

    }

}