package com.example.DiaShield;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getContentView();
        getNextScreen();
    }

    public void getNextScreen(){
        redirectToScreen();
    }

    public void redirectToScreen(){
        Intent measuredIntent = new Intent(getApplicationContext(), CalculateScreen.class);
        startActivity(measuredIntent);
    }

    public void getContentView(){
        setContentView(R.layout.activity_main);
    }
}