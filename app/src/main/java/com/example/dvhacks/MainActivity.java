package com.example.dvhacks;



import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    ListView list;

    ArrayList<String> date = new ArrayList<>();
    ArrayList<String> title = new ArrayList<>();
    ArrayList<String> mood = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        date.add("TUESDAY, JAN 12");
        date.add("WEDNESDAY, JAN 13");
        date.add("THURSDAY, JAN 14");
        date.add("TUESDAY, JAN 12");
        date.add("WEDNESDAY, JAN 13");
        date.add("THURSDAY, JAN 14");


        title.add("The president declared a state of national emergency.");
        title.add("Half the class died this morning from the smell.");
        title.add("The fart stayed in the air till today.");
        title.add("The president declared a state of national emergency.");
        title.add("Half the class died this morning from the smell.");
        title.add("The fart stayed in the air till today.");

        mood.add("MOOD DETECTED: HAPPY");
        mood.add("MOOD DETECTED: SAD");
        mood.add("MOOD DETECTED: HAPPY");
        mood.add("MOOD DETECTED: HAPPY");
        mood.add("MOOD DETECTED: SAD");
        mood.add("MOOD DETECTED: HAPPY");

        MyListAdapter listAdapter = new MyListAdapter(this, date, title, mood);
        list=(ListView)findViewById(R.id.list);
        list.setAdapter(listAdapter);
        Toast.makeText(getApplicationContext(),listAdapter.getCount() + "",Toast.LENGTH_SHORT).show();



        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
                // TODO Auto-generated method stub
                if(position == 0) {
                    //code specific to first list item
                    Toast.makeText(getApplicationContext(),"Place Your First Option Code",Toast.LENGTH_SHORT).show();
                }

                else if(position == 1) {
                    //code specific to 2nd list item
                    Toast.makeText(getApplicationContext(),"Place Your Second Option Code",Toast.LENGTH_SHORT).show();
                }

                else if(position == 2) {

                    Toast.makeText(getApplicationContext(),"Place Your Third Option Code",Toast.LENGTH_SHORT).show();
                }
                else if(position == 3) {

                    Toast.makeText(getApplicationContext(),"Place Your Forth Option Code",Toast.LENGTH_SHORT).show();
                }
                else if(position == 4) {

                    Toast.makeText(getApplicationContext(),"Place Your Fifth Option Code",Toast.LENGTH_SHORT).show();
                }

            }
        });



    }
}
