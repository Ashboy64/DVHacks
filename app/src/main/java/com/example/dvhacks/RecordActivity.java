package com.example.dvhacks;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.speech.RecognizerIntent;


import android.os.Bundle;

import android.util.Base64;
import android.util.Log;
import android.view.Window;
import android.widget.TextView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.lang.reflect.Array;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class RecordActivity extends AppCompatActivity {

    private TextView txvResult;

    private FirebaseFirestore db;

    RequestQueue queue;
    String url = "https://api.us-south.natural-language-understanding.watson.cloud.ibm.com/instances/11054001-9e5a-43d7-96eb-520a992ee4b6/v1/analyze?version=2019-07-12";
    String flaskUrl = "http://9d31d837.ngrok.io/";
    double[] scores;
    String[] nouns;
    static Map<String, Double> scoreMap;
    boolean updated = false;
    String text;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        scoreMap = new HashMap<>();
        queue = Volley.newRequestQueue(this);

//        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_record);
        txvResult = (TextView) findViewById(R.id.txvResult);
        db = FirebaseFirestore.getInstance();
    }


    public void getSpeechInput(View view) {


        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, 10);
        } else {
            Toast.makeText(this, "Your Device Don't Support Speech Input", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case 10:
                if (resultCode == RESULT_OK && data != null) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    text = result.get(0);
                    Log.d("result.get(0)", "onActivityResult: " + result.get(0));
                    nouns = new String[]{"bees"};
                    run();

                    Log.d("text:", result.get(0));

                }
                break;
        }
    }


    public static Comparator<String> nounComparator = new Comparator<String>() {
        @Override
        public int compare(String s, String t1) {
            if(scoreMap.get(s) > scoreMap.get(t1)){
                return 1;
            }else if(scoreMap.get(s) < scoreMap.get(t1)){
                return -1;
            }
            return 0;
        }
    };

    public void run() {
        // Get the nouns from the flask server
        JSONObject nounParams = new JSONObject();
        //flaskUrl += "?text=" + text;

        JsonObjectRequest req = new JsonObjectRequest
                (Request.Method.POST, flaskUrl + "?text="+text, nounParams, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        Log.d("WATSON", "in on response: " + response.toString());

                        try {
                            JSONArray nounsResult = (JSONArray) response.get("output");
                            nouns = new String[nounsResult.length()];
                            Log.d("WATSON", "nouns.length: " + nouns.length+"");
                            Log.d("WATSON", "nouns result: " + nounsResult.length()+"");
                            Log.d("WATSON", "nouns: " + nouns);
                            Log.d("WATSON", "nounsResult: " + nounsResult);

                            for(int i = 0; i < nouns.length; i++){
                                nouns[i] = (String) nounsResult.get(i);
                            }

                            fillScoreMap();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error
                        System.out.println(error);
                        Log.d("WATSON", error.toString());
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

            /*Log.d("WATSON", "" + req.getTimeoutMs());

            req.setRetryPolicy(new DefaultRetryPolicy(
                    2500,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));*/

        queue.add(req);
    }


    public void updateText(double documentScore){
        txvResult.setText(text);

        ArrayList<String> keys = new ArrayList<>();
        for(String str: scoreMap.keySet()){
            keys.add(str);
        }

        Collections.sort(keys, nounComparator);

        Map<String, Double> lowest = new HashMap<>();

        if(scoreMap.size() >= 5){
            for(int i = 0; i < 5; i++){
                lowest.put(keys.get(i), scoreMap.get(keys.get(i)));
            }
        } else {
            for(int i = 0; i < keys.size(); i++){
                lowest.put(keys.get(i), scoreMap.get(keys.get(i)));
            }
        }

        Map<String, Object> entry = new HashMap<>();

        DateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
        Date date = new Date();

        String dateStr = dateFormat.format(date);

        entry.put("date", dateStr);
        entry.put("lowest", lowest);
        entry.put("entry_score", documentScore);

        Map<String, Object> discussion = new HashMap<>();
        discussion.put("question", "");
        discussion.put("answer", text);

        //text = "";
        //txvResult.setText("");


        db.collection("entries").document("e1 " + dateStr).set(entry);

        db.collection("entries").document("e1 " + dateStr).collection("conversation").document("discussion")
                .set(discussion)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("message", "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("message", "Error writing document", e);
                    }
                });

        scoreMap = new HashMap<>();


    }

    public void fillScoreMap() {
        JSONObject json = new JSONObject();
        scores = new double[nouns.length];

        try {
            json.put("text", text);

            JSONObject json2 = new JSONObject();
            JSONObject json3 = new JSONObject();
            JSONArray json4 = new JSONArray();
            JSONObject json5 = new JSONObject();

            json5.put("emotion", true);

            for(String str : nouns){
                json4.put(str);
            }

            json3.put("targets", json4);
            json2.put("sentiment", json3);

            json.put("features", json2);
            json.put("keywords", json5);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d("WATSON", json.toString());

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, url, json, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray targetsArray = ((JSONArray) ((JSONObject) response.get("sentiment")).get("targets"));
                            for(int i = 0; i < targetsArray.length(); i++){
                                JSONObject target = (JSONObject) targetsArray.get(i);
                                Log.d("target", "target: " + target.toString());
                                scoreMap.put(target.get("text").toString(), (Double) ((double) target.get("score")));
                            }
                            updated = true;

                            Log.d("WATSON", response.toString());

                            updateText((double) ((JSONObject) ((JSONObject) response.get("sentiment")).get("document")).get("score"));
                            //return updateText();
                        } catch (JSONException e) {

                        }
                        //textView.setText(scoreMap.toString());
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error
                        System.out.println(error);
                        Log.d("WATSON", error.toString());
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                String credentials = "apikey:21kYEPrdxlQNr_1V6R8_egYsV_CG-sdiO2E7e5U4kU4f";
                String auth = "Basic " + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", auth);
                return headers;
            }
        };

        queue.add(jsonObjectRequest);
    }
}

