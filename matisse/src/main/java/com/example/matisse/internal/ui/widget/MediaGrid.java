package com.example.matisse.internal.ui.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.example.matisse.R;
import com.example.matisse.entity.Item;
import com.example.matisse.internal.entity.SelectionSpec;

import java.util.jar.Attributes;

import androidx.annotation.NonNull;

public class MediaGrid extends SquareFrameLayout implements View.OnClickListener{

    private ImageView mThumbnail;
    private CheckView mCheckView;

    private PreBindInfo mPreBindInfo;
    private Item mMedia;
    private OnMediaGridClickListener mListener;

    public MediaGrid(@NonNull Context context) {
        super(context);
        init(context);
    }

    public MediaGrid(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        //由于该布局的根标签是merge，所以需要制定MediaGrid为父节点。
        LayoutInflater.from(context).inflate(R.layout.media_grid_content, this, true);

        mThumbnail = findViewById(R.id.media_thumbnail);
        mCheckView = findViewById(R.id.check_view);
        
        mCheckView.setOnClickListener(this);
    }

    public void preBindMedia(PreBindInfo preBindInfo) {
        mPreBindInfo = preBindInfo;
    }

    public void bindMedia(Item item) {
        mMedia = item;
        setImage();
        initCheckView();
    }

    private void initCheckView(){
        mCheckView.setCountable(mPreBindInfo.mCheckViewCountable);
    }

    public void setCheckEnabled(boolean enabled) {
        mCheckView.setEnabled(enabled);
    }

    public void setCheckedNum(int checkedNum) {
        mCheckView.setCheckedNum(checkedNum);
    }

    public void setChecked(boolean checked) {
        mCheckView.setChecked(checked);
    }


    private void setImage() {
        SelectionSpec.getInstance().imageEngine.loadThumbnail(getContext(), mPreBindInfo.mResize,
                mPreBindInfo.mPlaceholder, mThumbnail, mMedia.getUri());
    }

    @Override
    public void onClick(View v) {
        if(mListener != null){
            mListener.onCheckViewClicked(mCheckView,mMedia);
        }
    }

    public void setOnMediaGridClickListener(OnMediaGridClickListener listener) {
        mListener = listener;
    }

    public static class PreBindInfo {
        int mResize;
        Drawable mPlaceholder;
        boolean mCheckViewCountable;

        public PreBindInfo(int resize, Drawable placeholder, boolean checkViewCountable) {
            mResize = resize;
            mPlaceholder = placeholder;
            mCheckViewCountable = checkViewCountable;
        }
    }
    
    public interface OnMediaGridClickListener{
        void onCheckViewClicked(CheckView checkView,Item item);
    }
}
