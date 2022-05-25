package com.example.comicground;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {
    ConstraintLayout constraintLayout;
    AppCompatButton btnLogin;
    AppCompatButton btnSignin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        btnLogin= findViewById(R.id.btnlogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goSearch=new Intent(MainActivity.this, ActivitySearch.class);
                startActivity(goSearch);
            }
        });

        btnSignin= findViewById(R.id.btnsignup);
        btnSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goSignup=new Intent(MainActivity.this, ActivitySignup.class);
                startActivity(goSignup);
            }
        });

        constraintLayout = (ConstraintLayout) findViewById(R.id.main_layout);
        constraintLayout.setOnClickListener(null);
    }
}