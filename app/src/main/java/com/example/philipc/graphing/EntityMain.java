package com.example.philipc.graphing;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.parse.ParseAnalytics;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

/**
 * Created by philipc on 4/28/17.
 */

public class EntityMain extends AppCompatActivity {

    private ArrayList<Double> arrayList = new ArrayList<>(1);
    private ArrayList<String> arrayDate = new ArrayList<>(1);
    private ArrayList<String> arrayDateD = new ArrayList<>(1);

    private TemperatureReceiver temperatureReceiver;

    private Boolean start = false;
    private final Handler mHandler = new Handler();

    private Runnable mTimer2;

    private LineGraphSeries<DataPoint> mSeries2;

    private double graph2LastXValue = 0d;

    public GraphView graph;

    private String entity;

    private TextView temperature, pressure, humidity;

    private boolean trackAll, serviceUnregistered;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        entity = intent.getStringExtra("TPH");

        setTitle("Tracking " + entity);

        graph = (GraphView) findViewById(R.id.graph);
        mSeries2 = new LineGraphSeries<>();
        graph.addSeries(mSeries2);
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(0);
        graph.getViewport().setMaxX(40);

        NumberFormat nf = NumberFormat.getInstance();
        nf.setMaximumFractionDigits(1);
        nf.setMinimumIntegerDigits(3);

        graph.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter(nf, nf));


        trackAll = false;
        serviceUnregistered = true;

        Calendar c = Calendar.getInstance();
        int day = c.get(Calendar.DAY_OF_MONTH);
        int month=c.get(Calendar.MONTH);
        int year=c.get(Calendar.YEAR);
        int hrs=c.get(Calendar.HOUR);
        int min=c.get(Calendar.MINUTE);
        int sec=c.get(Calendar.SECOND);
        Log.i("onCreate",Integer.toString(day)+Integer.toString(month)+Integer.toString(year)+" Time:"+Integer.toString(hrs)+":"+Integer.toString(min)+":"+Integer.toString(sec));
        ParseAnalytics.trackAppOpenedInBackground(getIntent());

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu,menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public void onBackPressed() {
        if(!serviceUnregistered)
            unregisterReceiver(temperatureReceiver);
        trackAll = false;
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("RaspberryPiControl");
        query.orderByDescending("createdAt");
        query.setLimit(1);
        if(item.getItemId() == R.id.start)
        {

            try
            {
                List<ParseObject> objects = query.find();
                if(objects.size() == 1)
                {
                    ParseObject parseObject = objects.get(0);
                    parseObject.put("Start",true);
                    parseObject.save();
                    start = true;
                }
                Log.i("Chat Activity"," OnStart");
                temperatureReceiver = new TemperatureReceiver();
                IntentFilter intentFilter = new IntentFilter();
                intentFilter.addAction(EntityService.ACTION);

                registerReceiver(temperatureReceiver, intentFilter);   //Registering the Broadcast receiver with the intent filter


                Intent intent = new Intent(this, EntityService.class);
                intent.putExtra("TPH",entity);
                //intent.putExtra("ACTIVEUSER",activeUser);
                startService(intent);   //Starting the Service with the intent

            }
            catch (Exception e)
            {

            }

        }
        else if(item.getItemId() == R.id.stop)
        {
            try
            {
                List<ParseObject> objects = query.find();
                if(objects.size() == 1)
                {
                    ParseObject parseObject = objects.get(0);
                    parseObject.put("Start",false);
                    parseObject.save();
                    start = false;

                }
                unregisterReceiver(temperatureReceiver);
                serviceUnregistered = true;

            }
            catch (Exception e)
            {

            }
        }

        else if(item.getItemId() == R.id.trackAll)
        {
            try
            {
                List<ParseObject> objects = query.find();
                if(objects.size() == 1)
                {
                    ParseObject parseObject = objects.get(0);
                    parseObject.put("Start",true);
                    parseObject.save();
                    start = true;
                }

            }
            catch (Exception e)
            {

            }
            temperatureReceiver = new TemperatureReceiver();
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(EntityService.ACTION);

            registerReceiver(temperatureReceiver, intentFilter);   //Registering the Broadcast receiver with the intent filter

            setContentView(R.layout.activity_track_all);
            temperature = (TextView) findViewById(R.id.tempView);
            pressure = (TextView) findViewById(R.id.pressView);
            humidity = (TextView) findViewById(R.id.humdView);
            trackAll = true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {


        super.onStart();
    }

    @Override
    protected void onPause() {
        if(!serviceUnregistered)
            unregisterReceiver(temperatureReceiver);
        super.onPause();
    }

    private DataPoint[] generateData() {
        int count = 30;
        DataPoint[] values = new DataPoint[count];
        for (int i=0; i<count; i++) {
            double x = i;
            double f = mRand.nextDouble()*0.15+0.3;
            double y = Math.sin(i*f+2) + mRand.nextDouble()*0.3;
            DataPoint v = new DataPoint(x, y);
            values[i] = v;
        }
        return values;
    }

    double mLastRandom = 2;
    Random mRand = new Random();

    private double getRandom()
    {

        return mLastRandom += mRand.nextDouble()*0.5 - 0.25;
    }

    private class TemperatureReceiver extends BroadcastReceiver
    {
        private String entity;
        private double prevx=0,prevy=0,prevz=0;
        @Override
        public void onReceive(Context context, Intent intent)
        {

            Log.i("BroadCast","BroadCast Received");
            double y =intent.getDoubleExtra("DATAPASSED",0);
            double x =intent.getDoubleExtra("DATAPASSEDPRESS",0);
            double z =intent.getDoubleExtra("DATAPASSEDHUMD",0);
            entity = intent.getStringExtra("TPH");
            if(entity.matches("Temperature") && trackAll == false && prevy != y)
            {
                Log.i("Entity","Temperature");
                Log.i("BroadCast",Double.toString(y));

                prevy = y;
                Log.i("BroadCast",Double.toString(y) +" "+Double.toString(graph2LastXValue));
                NumberFormat nf = NumberFormat.getInstance();
                nf.setMaximumFractionDigits(2);
                nf.setMaximumIntegerDigits(2);
                graph.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter(nf, nf));
                y =  Math.floor(y * 100) / 100;
                Log.i("Temperature", Double.toString(y));
                graph.setTitle(entity + "Celsius vs Time in Seconds");
                graph.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter(nf, nf));
                mSeries2.appendData(new DataPoint(graph2LastXValue, y), true, 40);
                graph2LastXValue += 10d;
            }
            else if(entity.matches("Pressure") && trackAll == false && prevx!=x)
            {
                prevx = x;
                Log.i("Entity","Pressure");
                Log.i("BroadCast",Double.toString(x));
                Log.i("BroadCast",Double.toString(x) +" "+Double.toString(graph2LastXValue));
                NumberFormat nf = NumberFormat.getInstance();
                nf.setMaximumFractionDigits(3);
                nf.setMinimumIntegerDigits(1);
                graph.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter(nf, nf));

                x =  Math.floor(x * 100) / 100;
                x=x*0.000986923;
                graph.setTitle(entity + " Atms vs Time in Seconds");
                graph.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter(nf, nf));
                mSeries2.appendData(new DataPoint(graph2LastXValue, x),true, 40);
                graph2LastXValue += 10d;

            }
            else if(entity.matches("Humidity") && trackAll == false && prevz!=z)
            {
                prevz = z;
                Log.i("Entity","Humidity");
                Log.i("BroadCast",Double.toString(z));
                NumberFormat nf = NumberFormat.getInstance();
                nf.setMaximumFractionDigits(2);
                nf.setMaximumIntegerDigits(2);
                graph.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter(nf, nf));

                z =  Math.floor(z * 100) / 100;
                Log.i("BroadCast",Double.toString(z) +" "+Double.toString(graph2LastXValue));
                graph.setTitle(entity + " %rH vs Time in Seconds");
                mSeries2.appendData(new DataPoint(graph2LastXValue, z), true, 40);
                graph2LastXValue += 10d;

            }
            if(trackAll == true)
            {
                Log.i("Entity","TrackAll");
                temperature.setText(String.valueOf(y));
                pressure.setText(String.valueOf(x));
                humidity.setText(String.valueOf(z));
            }

        }

    }

}
