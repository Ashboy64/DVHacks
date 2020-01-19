package com.example.dvhacks;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AnalyticsActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    GraphView graph;
    static Map<String, Integer> nounMap;
    double totalScore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analytics);

        db = FirebaseFirestore.getInstance();
        nounMap = new HashMap<>();
        totalScore = 0.0;
        buildGraph();
        buildMap();
    }

    public static Comparator<String> comparator = new Comparator<String>() {
        @Override
        public int compare(String s, String t1) {
            if(nounMap.get(s) > nounMap.get(t1)){
                return -1;
            }else if(nounMap.get(s) < nounMap.get(t1)){
                return 1;
            }
            return 0;
        }
    };

    public void buildMap(){
        db.collection("entries")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Map<String, Double> map = (Map<String, Double>) document.get("lowest");
                                for(String key : map.keySet()){
                                    if(nounMap.keySet().contains(key)){
                                        nounMap.put(key, nounMap.get(key) + 1);
                                    } else {
                                        nounMap.put(key, 0);
                                    }
                                }
                            }

                            int min = Math.min(5, nounMap.keySet().size());
                            ArrayList<String> keys = new ArrayList<>();

                            for (String noun : nounMap.keySet()){
                                keys.add(noun);
                            }

                            Collections.sort(keys, comparator);

                            for(int i = 0; i < min; i++){
                                switch(i){
                                    case 0:
                                        ((TextView) findViewById(R.id.noun1)).setText(keys.get(i) + ": " + nounMap.get(keys.get(i)));
                                        break;
                                    case 1:
                                        ((TextView) findViewById(R.id.noun2)).setText(keys.get(i) + ": " + nounMap.get(keys.get(i)));
                                        break;
                                    case 2:
                                        ((TextView) findViewById(R.id.noun3)).setText(keys.get(i) + ": " + nounMap.get(keys.get(i)));
                                        break;
                                    case 3:
                                        ((TextView) findViewById(R.id.noun4)).setText(keys.get(i) + ": " + nounMap.get(keys.get(i)));
                                        break;
                                    case 4:
                                        ((TextView) findViewById(R.id.noun5)).setText(keys.get(i) + ": " + nounMap.get(keys.get(i)));
                                        break;
                                }
                            }

                        } else {
                            Log.d("YOOOI", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    public void buildGraph(){
        graph = (GraphView) findViewById(R.id.graph);
        final ArrayList<DataPoint> arrList = new ArrayList<>();

        db.collection("entries")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            int currIndex = 0;

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                arrList.add(new DataPoint(currIndex, Double.parseDouble(document.get("entry_score").toString())));
                                currIndex++;

                                totalScore+=Double.parseDouble(document.get("entry_score").toString());
                            }
                            totalScore /= task.getResult().size();

                            DataPoint[] dataPoints = new DataPoint[arrList.size()];

                            int idx = 0;
                            for(DataPoint dp : arrList){
                                dataPoints[idx] = dp;
                                idx++;
                            }

                            Log.d("YOOOOOI", arrList.toString());

                            LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(dataPoints);
                            graph.addSeries(series);

                            ((TextView) findViewById(R.id.textView23)).setText(totalScore + "");

                            if(totalScore > 0.7){
                                ((TextView) findViewById(R.id.textView23)).setText("Great");
                            } else if (totalScore > 0.2){
                                ((TextView) findViewById(R.id.textView23)).setText("Good");
                            } else if (totalScore > 0){
                                ((TextView) findViewById(R.id.textView23)).setText("Decent");
                            } else if (totalScore > -0.2){
                                ((TextView) findViewById(R.id.textView23)).setText("Meh");
                            } else if (totalScore > -0.7){
                                ((TextView) findViewById(R.id.textView23)).setText("Not Great");
                            } else if (totalScore > -1){
                                ((TextView) findViewById(R.id.textView23)).setText("Bad");
                            }

                        } else {
                            Log.d("YOOOI", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }
}
