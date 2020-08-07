package com.example.matisse.internal.ui.adapter;

import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.matisse.R;
import com.example.matisse.entity.Item;
import com.example.matisse.internal.ui.widget.MediaGrid;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class AlbumMediaAdapter extends RecyclerViewCursorAdapter<RecyclerView.ViewHolder>{

    private static final String TAG = "AlbumMediaAdapter";
    private Drawable mPlaceholder;
    private RecyclerView mRecyclerView;
    private int mImageResize;
    
    public AlbumMediaAdapter(Context context,RecyclerView recyclerView) {
        super(null);
        
        mPlaceholder = context.getDrawable(R.drawable.mediagrid_item_placeholder);
        mRecyclerView = recyclerView;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.media_grid_item,parent,
                false);
        return new MediaViewHolder(view);
    }
    
    @Override
    protected void onBindViewHolder(RecyclerView.ViewHolder holder, Cursor cursor) {
        MediaViewHolder mediaViewHolder = (MediaViewHolder) holder;

        Item item = Item.valueOf(cursor);
        mediaViewHolder.mMediaGrid.preBindMedia(new MediaGrid.PreBindInfo(
                getImageResize(mediaViewHolder.mMediaGrid.getContext()),mPlaceholder));
        mediaViewHolder.mMediaGrid.bindMedia(item);
    }

    private int getImageResize(Context context){
        if(mImageResize == 0){
            RecyclerView.LayoutManager lm = mRecyclerView.getLayoutManager();
            int spanCount = ((GridLayoutManager)lm).getSpanCount();
            int screenWidth = context.getResources().getDisplayMetrics().widthPixels;
            int availableWidth = screenWidth - context.getResources().
                    getDimensionPixelSize(R.dimen.media_grid_spacing)*(spanCount-1);
            mImageResize = availableWidth / spanCount;
        }
        return mImageResize;
    }
    
    private static class MediaViewHolder extends RecyclerView.ViewHolder{

        MediaGrid mMediaGrid;
        
        public MediaViewHolder(@NonNull View itemView) {
            super(itemView);
            mMediaGrid = (MediaGrid) itemView;
        }
    }
   
}
