package com.example.philipc.graphing;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by philipc on 4/29/17.
 */

public class HistoryView extends AppCompatActivity {
    private String date;
    private StringBuilder dateFinal;
    private int mon,day,year;


    private DataPoint[] dataPoints;

    public GraphView graph1;
    private ArrayList<Double> temperature = new ArrayList<>(1);
    private ArrayList<Double> pressure = new ArrayList<>(1);
    private ArrayList<Double> humidity = new ArrayList<>(1);

    private ArrayList<String> tempTime = new ArrayList<>(1);
    private ArrayList<String> pressTime = new ArrayList<>(1);
    private ArrayList<String> humdTime = new ArrayList<>(1);



    private String entity;


    private LineGraphSeries<DataPoint> mSeries1;
    private BarGraphSeries<DataPoint> mSeries2;

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_resource,menu);
        return super.onCreateOptionsMenu(menu);
    }





    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temperature_history_view);

        graph1 = (GraphView) findViewById(R.id.graph1);

        Intent intent = getIntent();
        date = intent.getStringExtra("Date");
        entity = intent.getStringExtra("TPH");
        if(entity.matches("Temperature"))
            graph1.setTitle(entity + " in Celsius vs Sample number");
        else if(entity.matches("Pressure"))
            graph1.setTitle(entity + " in MilliBars vs Sample number");
        else if(entity.matches("Humidity"))
            graph1.setTitle(entity + " in %rH vs Sample number");
        dateFinal = new StringBuilder(date);
        dateFinal.deleteCharAt(2);
        dateFinal.deleteCharAt(3);
        date=dateFinal.toString();

        Log.i("Date",date);

        dateFinal = new StringBuilder(date);
        day = Integer.parseInt(dateFinal.delete(2,7).toString());

        dateFinal = new StringBuilder(date);
        dateFinal.delete(0,2);
        mon = Integer.parseInt(dateFinal.delete(1,5).toString());


        dateFinal = new StringBuilder(date);
        year = Integer.parseInt(dateFinal.delete(0,3).toString());

        setTitle(entity + " on " + day +"/"+mon+"/"+year);

        ParseQuery<ParseObject> query = new ParseQuery<>("RaspberryPi");
        query.whereEqualTo("Day",day);
        query.whereEqualTo("Month",mon);
        query.whereEqualTo("Year",year);

        try
        {
            List<ParseObject> objects = query.find();
            for(ParseObject object:objects)
            {
                Log.i("Temperature",object.get("Temperature").toString());
                temperature.add(Double.parseDouble(object.get("Temperature").toString()));
                pressure.add(Double.parseDouble(object.get("Pressure").toString()));
                humidity.add(Double.parseDouble(object.get("Humidity").toString()));

                tempTime.add(object.get("Hr").toString()+":"+object.get("Min").toString()+":"+object.get("Sec").toString()+" - "+object.get("Temperature").toString()+" C");
                pressTime.add(object.get("Hr").toString()+":"+object.get("Min").toString()+":"+object.get("Sec").toString()+" - "+object.get("Pressure").toString()+" MilliBars");
                humdTime.add(object.get("Hr").toString()+":"+object.get("Min").toString()+":"+object.get("Sec").toString()+" - "+object.get("Humidity").toString()+" %rH");

            }
        }

        catch(Exception e)
        {

        }

//        for(int i=0; i<temperature.size();i++)
//        {
//            dataPoints = new DataPoint[temperature.size()];
//            DataPoint v = new DataPoint(i,temperature.get(i));
//            dataPoints[i] = v;
//        }


        mSeries1 = new LineGraphSeries<>(generateData());


//        graph1.getViewport().setYAxisBoundsManual(true);
//        graph1.getViewport().setMinY(0);
//        graph1.getViewport().setMaxY(50);

        graph1.getViewport().setXAxisBoundsManual(true);
        graph1.getViewport().setMinX(0);
        graph1.getViewport().setMaxX(40);

        // enable scaling and scrolling
        graph1.getViewport().setScalable(true);
        graph1.getViewport().setScalableY(true);



        NumberFormat nf = NumberFormat.getInstance();
        nf.setMaximumFractionDigits(1);
        nf.setMinimumIntegerDigits(2);

        graph1.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter(nf, nf));

        graph1.addSeries(mSeries1);

        mSeries2 = new BarGraphSeries<>(generateData());


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.bar)
        {
            graph1.removeAllSeries();
            mSeries2 = new BarGraphSeries<>(generateData());


            graph1.getViewport().setXAxisBoundsManual(true);
            graph1.getViewport().setMinX(0);
            graph1.getViewport().setMaxX(40);

            // enable scaling and scrolling
            graph1.getViewport().setScalable(true);
            graph1.getViewport().setScalableY(true);
            graph1.addSeries(mSeries2);
        }
        else if(item.getItemId() == R.id.line)
        {
            graph1.removeAllSeries();
            mSeries1 = new LineGraphSeries<>(generateData());


            graph1.getViewport().setXAxisBoundsManual(true);
            graph1.getViewport().setMinX(0);
            graph1.getViewport().setMaxX(40);

            // enable scaling and scrolling
            graph1.getViewport().setScalable(true);
            graph1.getViewport().setScalableY(true);
            graph1.addSeries(mSeries1);
        }
        else if(item.getItemId() == R.id.list)
        {
            setContentView(R.layout.activity_temperature_history_list);
            ListView list = (ListView) findViewById(R.id.values);
            if(entity.matches("Temperature"))
            {
                final ArrayAdapter arrayAdaptert = new ArrayAdapter(this, android.R.layout.simple_list_item_1, tempTime);
                list.setAdapter(arrayAdaptert);
            }
            else if(entity.matches("Pressure"))
            {
                final ArrayAdapter arrayAdapterp = new ArrayAdapter(this, android.R.layout.simple_list_item_1, pressTime);
                list.setAdapter(arrayAdapterp);
            }
            else if(entity.matches("Humidity"))
            {
                final ArrayAdapter arrayAdapterh = new ArrayAdapter(this, android.R.layout.simple_list_item_1, humdTime);
                list.setAdapter(arrayAdapterh);
            }

        }

        return super.onOptionsItemSelected(item);
    }


    Random mRand = new Random();

    private DataPoint[] generateData() {

        if(entity.matches("Temperature"))
        {
            int count = temperature.size();
            DataPoint[] values = new DataPoint[count];
            for (int i=0; i<count; i++) {
                double x = i;
                double y = temperature.get(i);
                DataPoint v = new DataPoint(x, y);
                values[i] = v;
            }
            return values;

        }
        else if(entity.matches("Pressure"))
        {
            int count = pressure.size();
            DataPoint[] values = new DataPoint[count];
            for (int i=0; i<count; i++) {
                double x = i;
                double y = pressure.get(i);
                DataPoint v = new DataPoint(x, y);
                values[i] = v;
            }
            return values;
        }

        else
        {
            int count = humidity.size();
            DataPoint[] values = new DataPoint[count];
            for (int i=0; i<count; i++) {
                double x = i;
                double y = humidity.get(i);
                DataPoint v = new DataPoint(x, y);
                values[i] = v;
            }
            return values;
        }

    }


}
