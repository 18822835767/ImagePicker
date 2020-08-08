package com.example.matisse.internal.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.matisse.entity.Item;
import com.example.matisse.internal.ui.adapter.PreviewPagerAdapter;

import java.util.List;

/**
 * 预览选中的图片.
 */
public class SelectedPreviewActivity extends BasePreviewActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PreviewPagerAdapter adapter = (PreviewPagerAdapter) mPager.getAdapter();
        List<Item> items = getIntent().getParcelableArrayListExtra(SELECTED_ITEMS);
        adapter.addAll(items);
        adapter.notifyDataSetChanged();
    }
}
