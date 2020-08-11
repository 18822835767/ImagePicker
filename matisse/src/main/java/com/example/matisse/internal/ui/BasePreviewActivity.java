package com.example.matisse.internal.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
public abstract class BasePreviewActivity extends AppCompatActivity implements View.OnClickListener,
        ViewPager.OnPageChangeListener {

    public static final String SELECTED_ITEMS = "selection_items";
    public static final String EXTRA_RESULT_APPLY = "extra_result_apply";

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
        
        updateBottomToolbar();
    }

    private void initView() {
        mPager = findViewById(R.id.pager);
        mButtonBack = findViewById(R.id.button_back);
        mButtonApply = findViewById(R.id.button_apply);
        mCheckView = findViewById(R.id.check_view);
    }

    private void initData() {
        mSpec = SelectionSpec.getInstance();
        mSelectedItemCollection = SelectedItemCollection.getInstance();
        mAdapter = new PreviewPagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mAdapter);
        mCheckView.setCountable(mSpec.countable);
    }

    private void initEvent() {
        mCheckView.setOnClickListener(this);
        mPager.addOnPageChangeListener(this);
        mButtonApply.setOnClickListener(this);
        mButtonBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.check_view) {
            Item item = mAdapter.getMediaItem(mPager.getCurrentItem());
            //如果当前预览的Item原先是一个选中的状态
            if (mSelectedItemCollection.isSelected(item)) {
                mSelectedItemCollection.remove(item);//将该Item从选中列表中移除
                //如果是多选
                if (mSpec.countable) {
                    mCheckView.setCheckedNum(CheckView.UNCHECKED);
                    //如果是单选
                } else {
                    mCheckView.setChecked(false);
                }
                //如果当前预览的Item原先没有被选中
            } else {
                //可选择的Item到达最大数量，那么该item不被选中
                if (mSelectedItemCollection.maxSelectableReached()) {
                    Toast.makeText(this, "亲，最多选中" + mSpec.maxSelectable + "张图片",
                            Toast.LENGTH_SHORT).show();
                    //可选择的Item未到达最大数量，那么该item被选中
                } else {
                    mSelectedItemCollection.add(item);
                    //多选下，被选中
                    if (mSpec.countable) {
                        mCheckView.setCheckedNum(mSelectedItemCollection.checkNumOf(item));
                        //单选下，被选中
                    } else {
                        mCheckView.setChecked(true);
                    }
                }
            }
            updateBottomToolbar();
        }else if(v.getId() == R.id.button_apply){
            if (mSelectedItemCollection.isEmpty()) {
                Toast.makeText(this, "亲，还没有图片呢", Toast.LENGTH_SHORT).show();
            }else{
                sendBackResult(true);
                finish();
            }
        }else if(v.getId() == R.id.button_back){
            sendBackResult(false);
            finish();
        }
    }

    /**
     * 当滚动ViewPager时，更新顶部CheckView的状态.
     */
    @Override
    public void onPageSelected(int position) {
        Item item = mAdapter.getMediaItem(position);
        //如果当前是多选
        if (mSpec.countable) {
            int checkNum = mSelectedItemCollection.checkNumOf(item);
            //被选中了
            if (checkNum > 0) {
                mCheckView.setCheckedNum(checkNum);
                mCheckView.setEnabled(true);
                //未被选中
            } else {
                mCheckView.setCheckedNum(checkNum);
                //到达最大数量，不可被选中
                if (mSelectedItemCollection.maxSelectableReached()) {
                    mCheckView.setEnabled(false);
                    //未到达最大数量，仍有机会被选中
                } else {
                    mCheckView.setEnabled(true);
                }
            }
            //如果当前是单选
        } else {
            boolean selected = mSelectedItemCollection.isSelected(item);
            //如果被选中
            if (selected) {
                mCheckView.setChecked(true);
                mCheckView.setEnabled(true);
            //如果未被选中
            } else {
                mCheckView.setChecked(false);
                //到达最大数量了，不可选中
                if (mSelectedItemCollection.maxSelectableReached()) {
                    mCheckView.setEnabled(false);
                    //仍然有机会被选中
                } else {
                    mCheckView.setEnabled(true);
                }
            }
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }


    @Override
    public void onPageScrollStateChanged(int state) {
    }

    @Override
    public void onBackPressed() {
        sendBackResult(false);
        super.onBackPressed();
    }

    /**
     * 向上一个活动返回结果.
     *
     * @param apply 是否直接提交所选中的图片.
     */
    private void sendBackResult(boolean apply) {
        Intent intent = new Intent();
        intent.putExtra(EXTRA_RESULT_APPLY,apply);
        setResult(RESULT_OK,intent);
    }

    /**
     * 更新底部的状态栏.
     */
    private void updateBottomToolbar() {
        int count = mSelectedItemCollection.getSize();
        if(count == 0){
            mButtonApply.setText(getResources().getString(R.string.apply));
        }else{
            mButtonApply.setText(String.valueOf(getResources().getString(R.string.apply)+"("+count+")"));
        }
    }
}
