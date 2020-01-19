package com.example.dvhacks;



import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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

    ImageView profile;
    FloatingActionButton create;

    private FirebaseFirestore db;

    ArrayList<String> date = new ArrayList<>();
    ArrayList<String> title = new ArrayList<>();
    ArrayList<String> mood = new ArrayList<>();
    ArrayList<String> conversations = new ArrayList<>();

    boolean done = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = FirebaseFirestore.getInstance();
        buildArrays();
        profile = (ImageView) findViewById(R.id.imageView);

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AnalyticsActivity.class);
                startActivity(intent);
            }
        });
        create = (FloatingActionButton) findViewById(R.id.floatingActionButton);
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), RecordActivity.class);
                startActivity(intent);
            }
        });

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
                Intent intent = new Intent(getBaseContext(), ViewEntry.class);
                intent.putExtra("DATE", date.get(position));
                intent.putExtra("TITLE", title.get(position));
                intent.putExtra("MOOD", mood.get(position));
                intent.putExtra("CONVO", conversations.get(position));
                startActivity(intent);


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

                                // Add conversations into conversations
                                DocumentReference docRef = db.collection("entries").document(document.getId()).collection("conversation").document("discussion");
                                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            DocumentSnapshot document = task.getResult();
                                            if (document.exists()) {
                                                conversations.add(document.get("answer").toString());
                                                buildView();
                                            } else {
                                                Log.d("OH NO", "No such document");
                                            }
                                        } else {
                                            Log.d("OH NO", "get failed with ", task.getException());
                                        }
                                    }
                                });

                                Log.d("YOOI", document.getData().toString());
                            }
                        } else {
                            Log.d("YOOOI", "Error getting documents: ", task.getException());
                        }
                    }
                });

    }
}
