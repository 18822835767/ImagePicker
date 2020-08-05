package com.example.matisse.internal.ui.widget;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListPopupWindow;
import android.widget.TextView;

import com.example.matisse.R;
import com.example.matisse.entity.Album;

/**
 * 该类主要用于维护一个选择相册的下拉列表.
 */
public class AlbumSpinner {
    /**
     * 最多显示的条数.
     */
    private static final int MAX_SHOW_COUNT = 6;

    /**
     * 显示相册名字的textView.
     */
    private TextView mSelectedText;

    private ListPopupWindow mListPopupWindow;
    private CursorAdapter mAdapter;
    private AdapterView.OnItemSelectedListener mOnItemSelectedListener;

    public AlbumSpinner(Context context) {
        mListPopupWindow = new ListPopupWindow(context);
        mListPopupWindow.setModal(true);//设置显示模式
        int density = (int) context.getResources().getDisplayMetrics().density;
        mListPopupWindow.setContentWidth(200 * density);

        mListPopupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AlbumSpinner.this.onItemSelected(position);
                if (mOnItemSelectedListener != null) {
                    mOnItemSelectedListener.onItemSelected(parent, view, position, id);
                }
            }
        });
    }

    /**
     * 下拉列表的item被选中后，关闭列表，设置相册名字.
     */
    private void onItemSelected(int position) {
        mListPopupWindow.dismiss();
        Cursor cursor = mAdapter.getCursor();
        cursor.moveToPosition(position);
        Album album = Album.valueOf(cursor);
        mSelectedText.setText(album.getDisplayName());
    }

    /**
     * 设置选择监听.
     */
    public void setOnItemSelectedListener(AdapterView.OnItemSelectedListener listener) {
        mOnItemSelectedListener = listener;
    }

    /**
     * 设置选中某一项.
     */
    public void setSelection(int position) {
        mListPopupWindow.setSelection(position);
        onItemSelected(position);
    }

    /**
     * 设置下拉列表显示在哪个控件下面.
     */
    public void setAnchorView(View view) {
        mListPopupWindow.setAnchorView(view);
    }

    /**
     * 为下拉列表设置适配器.
     */
    public void setAdapter(CursorAdapter adapter) {
        mListPopupWindow.setAdapter(adapter);
        mAdapter = adapter;
    }

    /**
     * 传入显示相册名字的TextView.
     */
    public void setSelectedText(TextView textView) {
        mSelectedText = textView;
        mSelectedText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //根据相册的数量，显示的控制下拉列表的高度.
                int itemHeight = v.getResources().getDimensionPixelOffset(R.dimen.album_item_height);
                mListPopupWindow.setHeight(mAdapter.getCount() >= MAX_SHOW_COUNT ?
                        itemHeight * MAX_SHOW_COUNT : itemHeight * mAdapter.getCount());
                mListPopupWindow.show();
            }
        });
    }
}
