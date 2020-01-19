package com.example.dvhacks;



import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


public class MainActivity extends AppCompatActivity {

    ListView list;

    private FirebaseFirestore db;

    ArrayList<String> date = new ArrayList<>();
    ArrayList<String> title = new ArrayList<>();
    ArrayList<String> mood = new ArrayList<>();

    boolean done = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = FirebaseFirestore.getInstance();
        buildArrays();

//        date.add("TUESDAY, JAN 12");
//        date.add("WEDNESDAY, JAN 13");
//        date.add("THURSDAY, JAN 14");
//        date.add("TUESDAY, JAN 12");
//        date.add("WEDNESDAY, JAN 13");
//        date.add("THURSDAY, JAN 14");
//
//
//        title.add("The president declared a state of national emergency.");
//        title.add("Half the class died this morning from the smell.");
//        title.add("The fart stayed in the air till today.");
//        title.add("The president declared a state of national emergency.");
//        title.add("Half the class died this morning from the smell.");
//        title.add("The fart stayed in the air till today.");
//
//        mood.add("MOOD DETECTED: HAPPY");
//        mood.add("MOOD DETECTED: SAD");
//        mood.add("MOOD DETECTED: HAPPY");
//        mood.add("MOOD DETECTED: HAPPY");
//        mood.add("MOOD DETECTED: SAD");
//        mood.add("MOOD DETECTED: HAPPY");

//        Log.d("YOOOOI", date.toString());



    }

    public void buildView() {
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

    public void buildArrays(){
        db.collection("entries")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                String dateStr = document.get("date").toString();
                                dateStr = dateStr.substring(0, dateStr.length()-9);

                                SimpleDateFormat format1 = new SimpleDateFormat("yyyy_MM_dd");
                                SimpleDateFormat format2 = new SimpleDateFormat("MMMM d, yyyy");
                                String date2 = "";
                                try {
                                    Date dateObj = format1.parse(dateStr);

                                    Calendar c = Calendar.getInstance();
                                    c.setTime(dateObj);
                                    int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);

                                    switch (dayOfWeek){
                                        case 1:
                                            date.add("Sunday");
                                            break;
                                        case 2:
                                            date.add("Monday");
                                            break;
                                        case 3:
                                            date.add("Tuesday");
                                            break;
                                        case 4:
                                            date.add("Wednesday");
                                            break;
                                        case 5:
                                            date.add("Thursday");
                                            break;
                                        case 6:
                                            date.add("Friday");
                                            break;
                                        case 7:
                                            date.add("Saturday");
                                            break;
                                    }
                                    
                                    date2 = format2.format(dateObj);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }

                                title.add(date2);
                                mood.add(document.get("entry_score").toString());

                                Log.d("YOOI", document.getData().toString());
                            }

                            buildView();
                        } else {
                            Log.d("YOOOI", "Error getting documents: ", task.getException());
                        }
                    }
                });

    }
}
