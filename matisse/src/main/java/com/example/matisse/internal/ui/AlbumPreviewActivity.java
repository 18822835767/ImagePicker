package com.example.matisse.internal.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.os.Bundle;

import com.example.matisse.entity.Album;
import com.example.matisse.entity.Item;
import com.example.matisse.internal.ui.adapter.PreviewPagerAdapter;
import com.example.matisse.model.AlbumCollection;
import com.example.matisse.model.AlbumMediaCollection;
import com.example.matisse.model.SelectedItemCollection;

import java.util.ArrayList;
import java.util.List;

/**
 * 预览界面，功能是预览给定的整个Album的图片.
 */
public class AlbumPreviewActivity extends BasePreviewActivity implements
        AlbumMediaCollection.AlbumMediaCallbacks {

    public static final String EXTRA_ALBUM = "extra_album";
    public static final String EXTRA_ITEM = "extra_item";

    /**
     * 标志是否已经跳转到点击的选择的图片的位置.
     */
    private boolean mAlreadySetPosition = false;

    /**
     * 用于加载album数据.
     */
    private AlbumMediaCollection mCollection = new AlbumMediaCollection();

    private SelectedItemCollection mSelectedItemCollection;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mCollection.onCreate(this, this);
        Album album = getIntent().getParcelableExtra(EXTRA_ALBUM);
        mCollection.load(album);
        mSelectedItemCollection = SelectedItemCollection.getInstance();
        
        //初始化checkView
        Item item = getIntent().getParcelableExtra(EXTRA_ITEM);
        if(mSpec.countable){
            mCheckView.setCheckedNum(mSelectedItemCollection.checkNumOf(item));
        }else{
            mCheckView.setChecked(mSelectedItemCollection.isSelected(item));
        }
    }


    @Override
    public void onAlbumMediaLoad(Cursor cursor) {
        List<Item> items = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                Item item = Item.valueOf(cursor);
                items.add(item);
            } while (cursor.moveToNext());
        }

        if (items.isEmpty()) {
            return;
        }

        PreviewPagerAdapter adapter = (PreviewPagerAdapter) mPager.getAdapter();
        adapter.addAll(items);
        adapter.notifyDataSetChanged();
        if (!mAlreadySetPosition) {
            mAlreadySetPosition = true;
            Item selectedItem = getIntent().getParcelableExtra(EXTRA_ITEM);
            int index = items.indexOf(selectedItem);
            mPager.setCurrentItem(index,false);
        }
    }

    @Override
    public void onAlbumMediaReset() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCollection.onDestroy();
    }
}
