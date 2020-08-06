package com.example.matisse.internal.ui.adapter;

import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.matisse.R;
import com.example.matisse.entity.Album;
import com.example.matisse.internal.entity.SelectionSpec;

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
        Album album = Album.valueOf(cursor);
        ((TextView)view.findViewById(R.id.album_name)).setText(album.getDisplayName());
        ((TextView)view.findViewById(R.id.album_media_count)).setText(String.valueOf(album.getCount()));
        
        //加载缩略图
        SelectionSpec.getInstance().imageEngine.loadThumbnail(context,context.getResources().getDimensionPixelSize(R
                .dimen.media_grid_size),mPlaceholder, 
                (ImageView) view.findViewById(R.id.album_cover),album.getCoverUri());
    }
}
