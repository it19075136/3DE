package mobile.application3DE.verbalMemory;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import mobile.application3DE.R;
import mobile.application3DE.verbalMemory.models.Words;


public class SinhalaTestActivity extends AppCompatActivity {
    MyCustomAdapter dataAdapter = null;
    Integer level;
    String testResult="";
    SharedPreferences pref ;
    SharedPreferences.Editor editor;
    private DatabaseReference mDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sinhala_test);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
        editor = pref.edit();
        Intent intent=getIntent();
        level = Integer.parseInt(pref.getString("level", null));
        displayListView();

        checkButtonClick();
        //

    }
    private void displayListView() {
        ArrayList<Words> wordList = new ArrayList<Words>();
        List<String> wordListOnly = null;
        List<String> wordTrueOnly = null;
        if(level==3){
            wordListOnly = Arrays.asList("ගම", "කමල්", "පන්සල", "යනවා", "කනවා");
            wordTrueOnly = Arrays.asList("0", "1", "1", "1", "0");
        }
        else if(level==2){
            wordListOnly = Arrays.asList("සමන්","ගාල්ලට","නිවාඩුවකට","කාර්යාලය","කාර්යබහුලයි");
            wordTrueOnly = Arrays.asList("1", "1", "1", "0", "0");
        }
        else if(level==1){
            wordListOnly = Arrays.asList("වැතිර සිටී","හිඟන්නෙක්","සුනිල්", "තරුණ","වෙලඳපොල");
            wordTrueOnly = Arrays.asList("1", "1", "0", "0", "1");
        }
        for(int i=0;i<wordListOnly.size();i++){
            Words word = new Words(wordTrueOnly.get(i), wordListOnly.get(i),false);
            wordList.add(word);
        }


        //create an ArrayAdaptar from the String Array
        dataAdapter = new MyCustomAdapter(this,
                R.layout.check_box_word_layout, wordList);
        ListView listView = (ListView) findViewById(R.id.listView);
        // Assign adapter to ListView
        listView.setAdapter(dataAdapter);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // When clicked, show a toast with the TextView text
                Words word = (Words) parent.getItemAtPosition(position);
                Toast.makeText(getApplicationContext(),
                        "Clicked on Row: " + word.getName(),
                        Toast.LENGTH_LONG).show();
            }
        });

    }
    private class MyCustomAdapter extends ArrayAdapter<Words> {

        private ArrayList<Words> wordList;

        public MyCustomAdapter(Context context, int textViewResourceId,
                               ArrayList<Words> wordList) {
            super(context, textViewResourceId, wordList);
            this.wordList = new ArrayList<Words>();
            this.wordList.addAll(wordList);
        }

        private class ViewHolder {
            TextView code;
            CheckBox name;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder holder = null;
            Log.v("ConvertView", String.valueOf(position));

            if (convertView == null) {
                LayoutInflater vi = (LayoutInflater)getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE);
                convertView = vi.inflate(R.layout.check_box_word_layout, null);

                holder = new ViewHolder();
                holder.code = (TextView) convertView.findViewById(R.id.code);
                holder.name = (CheckBox) convertView.findViewById(R.id.checkBox1);
                convertView.setTag(holder);

                holder.name.setOnClickListener( new View.OnClickListener() {
                    public void onClick(View v) {
                        CheckBox cb = (CheckBox) v ;
                        Words word = (Words) cb.getTag();
                        Toast.makeText(getApplicationContext(),
                                "Clicked on Checkbox: " + cb.getText() +
                                        " is " + cb.isChecked(),
                                Toast.LENGTH_LONG).show();
                        word.setSelected(cb.isChecked());
                    }
                });
            }
            else {
                holder = (ViewHolder) convertView.getTag();
            }

            Words word = wordList.get(position);
            holder.code.setText(" (" +  word.getCode() + ")");
            holder.name.setText(word.getName());
            holder.name.setChecked(word.isSelected());
            holder.name.setTag(word);

            return convertView;

        }

    }

    private void checkButtonClick() {

        Button myButton = (Button) findViewById(R.id.button);
        myButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                ArrayList<Words> wordList = dataAdapter.wordList;
                float count = 0;
                for(int i=0;i<wordList.size();i++){
                    Words word = wordList.get(i);
                    if(word.isSelected()){
                        if(word.getCode() == "1"){
                            count++;
                        }else{
                            count = (float) (count - 0.5);
                        }

                    }
                }
                String data ="";
                if(count <= 0){
                     data = "0";
                }else{
                    data = String.valueOf(((float)count/3));
                }

                if(pref.getBoolean("notComplete", true)){
                    editor.putString("IR",data); // Storing string
                    editor.commit();
                    mDatabase.child("Result").child(pref.getString("email","null")).child("IR").setValue(data);
                    Intent i1 = new Intent(getApplicationContext(), WaitingActivity.class);
                    i1.putExtra("value",data);
                    startActivity(i1);
                }else{
                    editor.putString("DR",data); // Storing string
                    editor.commit();
                    mDatabase.child("Result").child(pref.getString("email","null")).child("DR").setValue(data);
                    Intent i1 = new Intent(getApplicationContext(), ResultActivity.class);
                    i1.putExtra("value",data);
                    startActivity(i1);
                }



            }
        });

    }

}