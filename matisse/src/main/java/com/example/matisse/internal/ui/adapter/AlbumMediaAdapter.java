package com.example.matisse.internal.ui.adapter;

import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.matisse.R;
import com.example.matisse.entity.Item;
import com.example.matisse.internal.entity.SelectionSpec;
import com.example.matisse.internal.ui.widget.MediaGrid;
import com.example.matisse.model.SelectedItemCollection;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class AlbumMediaAdapter extends RecyclerViewCursorAdapter<RecyclerView.ViewHolder> {

    private static final String TAG = "AlbumMediaAdapter";
    private Drawable mPlaceholder;
    private RecyclerView mRecyclerView;
    private int mImageResize;
    private SelectionSpec mSelectionSpec;
    private SelectedItemCollection mSelectedItemCollection;

    public AlbumMediaAdapter(Context context, RecyclerView recyclerView) {
        super(null);

        mSelectionSpec = SelectionSpec.getInstance();
        mSelectedItemCollection = SelectedItemCollection.getInstance();
        mPlaceholder = context.getDrawable(R.drawable.mediagrid_item_placeholder);
        mRecyclerView = recyclerView;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.media_grid_item, parent,
                false);
        return new MediaViewHolder(view);
    }

    @Override
    protected void onBindViewHolder(RecyclerView.ViewHolder holder, Cursor cursor) {
        MediaViewHolder mediaViewHolder = (MediaViewHolder) holder;

        Item item = Item.valueOf(cursor);
        mediaViewHolder.mMediaGrid.preBindMedia(new MediaGrid.PreBindInfo(
                getImageResize(mediaViewHolder.mMediaGrid.getContext()),
                mPlaceholder,
                SelectionSpec.getInstance().countable));

        mediaViewHolder.mMediaGrid.bindMedia(item);
        setCheckStatus(item,mediaViewHolder.mMediaGrid);
    }

    /**
     * 设置CheckView的选中状态.
     */
    private void setCheckStatus(Item item,MediaGrid mediaGrid) {
        //多选
        if(mSelectionSpec.countable){
            int index = mSelectedItemCollection.checkNumOf(item);
            //之前被选中
            if(index > 0){
                //可点击取消
                mediaGrid.setCheckEnabled(true);
                //设置选中数字
                mediaGrid.setCheckedNum(index);
            //之前未被选中
            }else {
                if(mSelectedItemCollection.maxSelectableReached()){
                    //数量到最大值了，未被选中的将不可被选中
                    mediaGrid.setCheckEnabled(false);
                    //多选下，未被选中的标志
                    mediaGrid.setCheckedNum(index);
                //虽然没被选中，但还是有被选到的机会
                }else{
                    //有机会被选中
                    mediaGrid.setCheckEnabled(true);
                    //之前未被选中的标志
                    mediaGrid.setCheckedNum(index);
                }
            }
        //单选
        }else{
            boolean selected = mSelectedItemCollection.isSelected(item);
            //如果之前已经选中
            if(selected){
                //仍然可以被点击取消
                mediaGrid.setCheckEnabled(true);
                //选中的标志
                mediaGrid.setChecked(true);
            //如果之前未被选中
            }else{
                //已经到达最大数量，未被选中的没有机会被选中
                if(mSelectedItemCollection.maxSelectableReached()){
                    //未被选中的不可被选中
                    mediaGrid.setCheckEnabled(false);
                    //未被选中的标志
                    mediaGrid.setChecked(false);
                //未到达最大数量，未被选中的有机会被选中  
                }else{
                    //有机会被选中
                    mediaGrid.setCheckEnabled(true);
                    //未被选中的标志
                    mediaGrid.setChecked(false);
                }
            }
        }
    }

    private int getImageResize(Context context) {
        if (mImageResize == 0) {
            RecyclerView.LayoutManager lm = mRecyclerView.getLayoutManager();
            int spanCount = ((GridLayoutManager) lm).getSpanCount();
            int screenWidth = context.getResources().getDisplayMetrics().widthPixels;
            int availableWidth = screenWidth - context.getResources().
                    getDimensionPixelSize(R.dimen.media_grid_spacing) * (spanCount - 1);
            mImageResize = availableWidth / spanCount;
        }
        return mImageResize;
    }

    private static class MediaViewHolder extends RecyclerView.ViewHolder {

        MediaGrid mMediaGrid;

        public MediaViewHolder(@NonNull View itemView) {
            super(itemView);
            mMediaGrid = (MediaGrid) itemView;
        }
    }

}
