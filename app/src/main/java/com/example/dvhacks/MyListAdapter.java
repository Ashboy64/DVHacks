package com.example.dvhacks;

import android.app.Activity;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

public class MyListAdapter extends ArrayAdapter<String>{

    private final Activity context;
    private final ArrayList<String> date;
    private final ArrayList<String> title;
    private final ArrayList<String> moodScore;


    public MyListAdapter(Activity context, ArrayList<String> date,ArrayList<String> title, ArrayList<String> moodScore) {
        super(context, R.layout.row, title);
        // TODO Auto-generated constructor stub

        this.context=context;
        this.date=date;
        this.title=title;
        this.moodScore=moodScore;



    }


    public View getView(int position,View view,ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.row, null,true);

        TextView date_text = (TextView) rowView.findViewById(R.id.textView);
        TextView title_text = (TextView) rowView.findViewById(R.id.message_body);
        TextView mood_text = (TextView) rowView.findViewById(R.id.textView2);

        date_text.setText(date.get(position));
        Log.d(TAG, date.get(position));
        title_text.setText(title.get(position));
        Log.d(TAG, title.get(position));
        mood_text.setText(moodScore.get(position));
        Log.d(TAG, moodScore.get(position));



        return rowView;

    };




}
