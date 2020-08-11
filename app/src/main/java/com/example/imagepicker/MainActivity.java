package com.example.imagepicker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.Application;
import android.app.Service;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Matrix;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.Button;

import com.example.matisse.Matisse;
import com.example.matisse.engine.impl.PicassoEngine;


public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private Button mButton;
    private RecyclerView mRecyclerView;
    private PathAdapter mAdapter;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        initData();
        initEvent();
    }
    
    private void initView(){
        mButton = findViewById(R.id.start_activity);
        mRecyclerView = findViewById(R.id.recycler_view);
    }
    
    private void initData(){
        mAdapter = new PathAdapter();
        mRecyclerView.setAdapter(mAdapter);
    }
    
    private void initEvent(){
        mButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Matisse.from(this).choose()
                .imageEngine(new PicassoEngine())
                .countable(true)
                .maxSelectable(9)
                .forResult(1);
    }
}
