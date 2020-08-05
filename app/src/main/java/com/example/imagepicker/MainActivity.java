package com.example.imagepicker;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.Application;
import android.app.Service;
import android.content.Context;
import android.content.ContextWrapper;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.matisse.Matisse;


public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private Button mButton;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mButton = findViewById(R.id.start_activity);
        mButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Matisse.from(this).choose().forResult(1);
    }
}
