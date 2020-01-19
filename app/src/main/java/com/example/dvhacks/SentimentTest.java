package com.example.dvhacks;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SentimentTest extends AppCompatActivity {

    TextView textView;
    Button button;
    EditText editText;
    String sentiment;
    RequestQueue queue;
    String url = "https://api.us-south.natural-language-understanding.watson.cloud.ibm.com/instances/11054001-9e5a-43d7-96eb-520a992ee4b6/v1/analyze?version=2019-07-12";
    double[] scores;
    String[] nouns;
    Map<String, Double> scoreMap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sentiment_test);

        textView = (TextView) findViewById(R.id.textView);
        editText = (EditText) findViewById(R.id.editText);
        button = (Button) findViewById(R.id.button);

        scoreMap = new HashMap<>();

        queue = Volley.newRequestQueue(this);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textView.setText("Checking sentiment of: " + editText.getText());
                nouns = new String[]{"apples", "math test"};
                fillScoreMap("I love apples! I hate math test.");
                textView.setText(scoreMap.toString());
            }
        });
    }

    public void fillScoreMap(String text) {
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

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, url, json, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray targetsArray = ((JSONArray) ((JSONObject) response.get("sentiment")).get("targets"));
                            for(int i = 0; i < targetsArray.length(); i++){
                                JSONObject target = (JSONObject) targetsArray.get(i);
                                scoreMap.put(target.get("text").toString(), (Double) target.get("score"));
                            }
                        } catch (JSONException e) {
                            textView.setText(e.toString());
                        }
//                        textView.setText(scoreMap.toString());
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error
                        System.out.println(error);
                        Log.d("WATSON", error.toString());
                        textView.setText(error.toString());
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

//        while(scoreMap.keySet().size() == 0){
//            try {
//                Thread.sleep(1000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
    }
}