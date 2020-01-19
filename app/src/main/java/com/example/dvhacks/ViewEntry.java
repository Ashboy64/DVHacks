package com.example.dvhacks;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class ViewEntry extends AppCompatActivity {

    TextView dateField, titleField, bodyField;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_entry);

        dateField = (TextView) findViewById(R.id.date_view);
        titleField = (TextView) findViewById(R.id.title_view);
        bodyField = (TextView) findViewById(R.id.body_view);

        String date = getIntent().getStringExtra("DATE");
        String title = getIntent().getStringExtra("TITLE");
        String body = getIntent().getStringExtra("CONVO");

        dateField.setText(date);
        titleField.setText(title);
        bodyField.setText(body);





    }
}
