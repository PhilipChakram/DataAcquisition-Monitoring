package com.example.philipc.graphing;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

/**
 * Created by philipc on 4/28/17.
 */

public class Temperature extends AppCompatActivity {
    private String entity;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temperature);
        Intent intent = getIntent();
        entity = intent.getStringExtra("TPH");
        setTitle("Track " + entity);

    }

    public void TemperatureTrack(View v)
    {
        Intent intent = new Intent(this,EntityMain.class);
        intent.putExtra("TPH",entity);
        startActivity(intent);

    }

    public void TemperatureHistory(View v)
    {
        Intent intent = new Intent(this,History.class);
        intent.putExtra("TPH",entity);
        startActivity(intent);
    }



}
