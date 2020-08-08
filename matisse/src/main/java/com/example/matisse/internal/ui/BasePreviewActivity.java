package com.example.matisse.internal.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.matisse.R;
import com.example.matisse.entity.Item;
import com.example.matisse.internal.entity.SelectionSpec;
import com.example.matisse.internal.ui.adapter.PreviewPagerAdapter;
import com.example.matisse.internal.ui.widget.CheckView;
import com.example.matisse.model.SelectedItemCollection;

/**
 * 预览界面的父类.
 */
public abstract class BasePreviewActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String SELECTED_ITEMS = "selection_items";
    
    protected ViewPager mPager;
    protected TextView mButtonBack;
    protected TextView mButtonApply;
    protected CheckView mCheckView;

    protected SelectionSpec mSpec;
    protected SelectedItemCollection mSelectedItemCollection;
    
    protected PreviewPagerAdapter mAdapter;
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_preview);
    
        initView();
        initData();
        initEvent();
    }
    
    private void initView(){
        mPager = findViewById(R.id.pager);
        mButtonBack = findViewById(R.id.button_back);
        mButtonApply = findViewById(R.id.button_apply);
        mCheckView = findViewById(R.id.check_view);
    }
    
    private void initData(){
        mSpec = SelectionSpec.getInstance();
        mSelectedItemCollection = SelectedItemCollection.getInstance();
        mAdapter = new PreviewPagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mAdapter);
        mCheckView.setCountable(mSpec.countable);
    }

    private void initEvent(){
        mCheckView.setOnClickListener(this);
    }
    
    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.check_view){
            Item item = mAdapter.getMediaItem(mPager.getCurrentItem()); 
            //如果当前预览的Item是一个选中的状态
            if(mSelectedItemCollection.isSelected(item)){
                mSelectedItemCollection.remove(item);//将该Item从选中列表中移除
                //如果是多选
                if(mSpec.countable){
                    mCheckView.setCheckedNum(CheckView.UNCHECKED);
                //如果是单选
                }else{
                    mCheckView.setChecked(false);
                }
            //如果当前预览的Item没有被选中
            }else{
                //可选择的Item到达最大数量，那么该item不被选中
                if(mSelectedItemCollection.maxSelectableReached()){
                    Toast.makeText(this,"亲，最多选中"+mSpec.maxSelectable+"张图片",
                            Toast.LENGTH_SHORT).show();
                 //可选择的Item未到达最大数量，那么该item被选中
                }else {
                    mSelectedItemCollection.add(item);
                    //多选下，被选中
                    if(mSpec.countable){
                        mCheckView.setCheckedNum(mSelectedItemCollection.checkNumOf(item));
                    //单选下，被选中
                    }else{
                        mCheckView.setCountable(true);
                    }
                }
            }
        }
    }
}
