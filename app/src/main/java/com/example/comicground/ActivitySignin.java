package com.example.comicground;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class ActivitySignin extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
        getSupportActionBar().hide();
    }
}