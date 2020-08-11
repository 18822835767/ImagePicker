package com.example.imagepicker;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.Application;
import android.app.Service;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Matrix;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.example.matisse.Matisse;
import com.example.matisse.engine.impl.PicassoEngine;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private static final int CHOOSE_IMAGES_REQUEST_CODE = 0;
    private static final String TAG = "MainActivity";
    
    private Button mButton;
    private RecyclerView mRecyclerView;
    private PathAdapter mAdapter;
    private List<String> mPaths = new ArrayList<>();
    
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
        LinearLayoutManager manager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(manager);
        mAdapter = new PathAdapter(mPaths);
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
                .forResult(CHOOSE_IMAGES_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode != Activity.RESULT_OK){
            return;
        }
        
        if(requestCode == CHOOSE_IMAGES_REQUEST_CODE && data != null){
            mPaths.clear();
            mPaths.addAll(Matisse.obtainPathResult(data));
            mAdapter.notifyDataSetChanged();
        }
    }
}
