package com.example.matisse.internal.ui.adapter;

import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;

import com.example.matisse.R;

public class AlbumsAdapter extends CursorAdapter {

    private Drawable mPlaceholder;
    
    public AlbumsAdapter(Context context, Cursor c, boolean autoRequery) {
        super(context, c, autoRequery);
        
        mPlaceholder = context.getDrawable(R.drawable.placeholder);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.album_list_item,parent,false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

    }
}
