package com.example.philipc.graphing;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.parse.ParseAnalytics;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by philipc on 4/29/17.
 */

public class History extends AppCompatActivity {
    private ListView dates;
    private ArrayList<Double> arrayList = new ArrayList<>(1);
    private ArrayList<String> arrayDate = new ArrayList<>(1);
    private ArrayList<String> arrayDateD = new ArrayList<>(1);

    private String entity;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temperature_history);
        dates = (ListView) findViewById(R.id.Dates);
        Intent intent = getIntent();
        entity = intent.getStringExtra("TPH");
        setTitle(entity + " History");
        dates.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int i, long id) {
                Intent intent = new Intent(getApplicationContext(),HistoryView.class);

                intent.putExtra("Date",arrayDateD.get(i));
                intent.putExtra("TPH",entity);
                Log.i("Date",arrayDateD.get(i));
                startActivity(intent);
            }
        });

        query();

        for(int i=1;i<arrayDate.size();i++)
        {
            if(i==1)
                arrayDateD.add(arrayDate.get(i));
            if(!arrayDate.get(i-1).matches(arrayDate.get(i)))
            {
                arrayDateD.add(arrayDate.get(i));
            }
        }

        for(String e: arrayDateD)
        {
            Log.i("DatesAvailable",e);
        }
        Log.i("ParseQuery","Exited2");

        final ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, arrayDateD);
        dates.setAdapter(arrayAdapter);

        ParseAnalytics.trackAppOpenedInBackground(getIntent());


    }

    private void query()
    {
        arrayDate.clear();
        arrayDateD.clear();
        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("RaspberryPi");
        query.setLimit(1000);
        query.orderByDescending("createdAt");
//        query.whereEqualTo("Year",2017);
//        query.whereEqualTo("Month",4);
//        query.whereEqualTo("Day",22);
        //query.orderByAscending("createdAt");

        try{

            List<ParseObject> objects = query.find();

            if(objects.size() > 0)
            {
                Log.i("ParseQuery",String.valueOf(objects.size()));
                for(ParseObject parseObject : objects)
                {
                    //Log.i("ParseQuery",parseObject.get("Temperature").toString());
                    Log.i("ParseQuery",parseObject.get("Day").toString());
                    Log.i("ParseQuery",parseObject.get("Month").toString());
                    Log.i("ParseQuery",parseObject.get("Year").toString());
                    if(parseObject.get("Day").toString().length() == 1)
                        arrayDate.add("0"+parseObject.get("Day").toString()+ "/" + parseObject.get("Month").toString() + "/" + parseObject.get("Year").toString());
                    else
                        arrayDate.add(parseObject.get("Day").toString()+ "/" + parseObject.get("Month").toString() + "/" + parseObject.get("Year").toString());
                    arrayList.add(parseObject.getDouble("Temperature"));
                }

            }


        }
        catch (Exception e)
        {

        }

    }


}
