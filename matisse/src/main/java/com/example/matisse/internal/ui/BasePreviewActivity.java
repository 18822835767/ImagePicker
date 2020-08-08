package com.example.matisse.internal.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.widget.TextView;

import com.example.matisse.R;
import com.example.matisse.internal.ui.adapter.PreviewPagerAdapter;

/**
 * 预览界面的父类.
 */
public abstract class BasePreviewActivity extends AppCompatActivity {

    protected ViewPager mPager;
    protected TextView mButtonBack;
    protected TextView mButtonApply;

    protected PreviewPagerAdapter mAdapter;
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_preview);
    
        initView();
        initData();
    }
    
    private void initView(){
        mPager = findViewById(R.id.pager);
        mButtonBack = findViewById(R.id.button_back);
        mButtonApply = findViewById(R.id.button_apply);
    }
    
    private void initData(){
        mAdapter = new PreviewPagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mAdapter);
    }
}
