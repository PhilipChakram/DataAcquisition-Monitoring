package com.example.philipc.graphing;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by philipc on 4/29/17.
 */

public class EntityService extends Service {

    private GraphThread graphThread;
    public final static String ACTION = "ACTION";

    private ArrayList<Double> temperature = new ArrayList<>(1);

    private double temp = 0, prevTemp = 0, press = 0, humd = 0;

    private String entity;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        entity = intent.getStringExtra("TPH");

        Log.i("Service","Service Started");
        graphThread = new GraphThread();
        graphThread.start();
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        stopSelf();
        super.onDestroy();
    }

    public class GraphThread extends Thread
    {
        @Override
        public void run() {

            while (true) {
                try{
//                    double mLastRandom = 2;
//                    Random mRand = new Random();
//                    mLastRandom += mRand.nextDouble() * 0.5 - 0.25;
                    Calendar c = Calendar.getInstance();
                    int day = c.get(Calendar.DAY_OF_MONTH);
                    int month=c.get(Calendar.MONTH) + 1;
                    int year=c.get(Calendar.YEAR);
                    int hrs=c.get(Calendar.HOUR_OF_DAY);
                    int min=c.get(Calendar.MINUTE);
                    int sec=c.get(Calendar.SECOND);
                    Log.i("Service",Integer.toString(day)+Integer.toString(month)+Integer.toString(year)+" Time:"+Integer.toString(hrs)+":"+Integer.toString(min)+":"+Integer.toString(sec));
                    ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("RaspberryPi");
                    query.whereEqualTo("Day",day);
                    query.whereEqualTo("Month",month);
                    query.whereEqualTo("Year",year);
                    query.whereEqualTo("Hr",hrs);
                    query.whereEqualTo("Min",min);
                    //query.whereGreaterThanOrEqualTo("Sec",sec);
                    query.orderByDescending("Sec");
                    query.setLimit(1);

                    try
                    {
                        List<ParseObject> parseObjects = query.find();
                        if(parseObjects.size() > 1)
                            Log.i("Service","More than one object retrieved");
                        if(parseObjects == null)
                            Log.i("Service","Query returned null");
                        for(ParseObject parseObject:parseObjects)
                        {
                            temp = parseObject.getDouble("Temperature");
                            Log.i("Service Temperature",Double.toString(temp));
                            press = parseObject.getDouble("Pressure");
                            Log.i("Service Pressure",Double.toString(press));
                            humd = parseObject.getDouble("Humidity");
                            Log.i("Service Humidity",Double.toString(humd));
                        }
                    }
                    catch (Exception e)
                    {

                    }
                    if(temp != 0 )
                    {
                        Intent intent = new Intent();
                        intent.setAction(ACTION);
                        intent.putExtra("DATAPASSED", temp);
                        intent.putExtra("DATAPASSEDPRESS",press);
                        intent.putExtra("DATAPASSEDHUMD",humd);
                        intent.putExtra("TPH",entity);
                        sendBroadcast(intent);
                        prevTemp = temp;
                    }
                    Thread.sleep(4000);
                }

                catch(Exception e)
                {

                }
            }
        }
    }

}
