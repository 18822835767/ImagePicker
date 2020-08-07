package com.example.matisse.internal.ui.adapter;

import android.database.Cursor;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public abstract class RecyclerViewCursorAdapter<VH extends RecyclerView.ViewHolder> extends
        RecyclerView.Adapter<VH> {

    private Cursor mCursor;

    public RecyclerViewCursorAdapter(Cursor cursor) {
        swapCursor(cursor);//置换Cursor
    }

    protected abstract void onBindViewHolder(VH holder, Cursor cursor);

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        mCursor.moveToPosition(position);
        onBindViewHolder(holder, mCursor);
    }

    @Override
    public int getItemCount() {
        if (isDateValid(mCursor)) {
            return mCursor.getCount();
        } else {
            return 0;
        }
    }

    public void swapCursor(Cursor newCursor) {
        //若两个Cursor相同，直接return.
        if (newCursor == mCursor) {
            return;
        }

        if (newCursor != null) {
            mCursor = newCursor;
            notifyDataSetChanged();//通知数据更新
        } else {
            notifyItemMoved(0, getItemCount());//移除数据
            mCursor = null;
        }
    }

    /**
     * 判断传入的Cursor是否是有效的.
     */
    private boolean isDateValid(Cursor cursor) {
        return cursor != null && !cursor.isClosed();
    }
}
