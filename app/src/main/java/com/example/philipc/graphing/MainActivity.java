package com.example.philipc.graphing;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity {




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

    }





    public void Temperature(View v)
    {
        Intent intent = new Intent(this,Temperature.class);
        intent.putExtra("TPH","Temperature");
        startActivity(intent);

    }

    public void Pressure(View v)
    {
        Intent intent = new Intent(this,Temperature.class);
        intent.putExtra("TPH","Pressure");
        startActivity(intent);
    }

    public void Humidity(View v)
    {
        Intent intent = new Intent(this,Temperature.class);
        intent.putExtra("TPH","Humidity");
        startActivity(intent);
    }



}
