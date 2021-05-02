package mobile.application3DE;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Calendar;

import mobile.application3DE.models.User;

public class MainActivity extends AppCompatActivity {

    TextInputEditText dateInput, fName, lName;
    RadioGroup gender;
    RadioButton gBtn;
    DatePickerDialog datePickerDialog;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_registration);

        dateInput = findViewById(R.id.bdayInput);
        fName = findViewById(R.id.fName);
        lName = findViewById(R.id.lName);
        gender = findViewById(R.id.genderGroup);

        dateInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // calender class's instance and get current date , month and year from calender
                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR); // current year
                int mMonth = c.get(Calendar.MONTH); // current month
                int mDay = c.get(Calendar.DAY_OF_MONTH); // current day
                // date picker dialog
                datePickerDialog = new DatePickerDialog(MainActivity.this,
                        new DatePickerDialog.OnDateSetListener() {


                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                // set day of month , month and year value in the edit text
                                dateInput.setText(dayOfMonth + "/"
                                        + (monthOfYear + 1) + "/" + year);

                            }
                        }, mYear, mMonth, mDay);

                datePickerDialog.show();


            }
        });
    }

    public void directToNext(View view) {

        // getting selected gender
        gBtn = findViewById(gender.getCheckedRadioButtonId());

        if(Validate_Inputs()) {
            Intent i = new Intent(this, user_details_2.class);
            user = new User(fName.getText().toString(), lName.getText().toString(), dateInput.getText().toString(), gBtn.getText().toString());
            i.putExtra("user", user);
            startActivity(i);
        }
        else // Showing error message when the field values are not valid
            Snackbar.make(view,"Please fill all the required fields",Snackbar.LENGTH_SHORT).show();

    }

    private boolean Validate_Inputs() {

        // Checking if there are empty fields
        if(fName.getText().toString().matches("") || lName.getText().toString().matches("") || dateInput.getText().toString().matches("") || gBtn.getText().toString().matches(""))
            return false;
        else
            return true;

    }

}