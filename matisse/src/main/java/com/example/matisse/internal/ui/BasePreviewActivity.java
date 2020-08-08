package com.example.matisse.internal.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.matisse.R;

/**
 * 预览界面的父类.
 */
public abstract class BasePreviewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_preview);
    }
}
