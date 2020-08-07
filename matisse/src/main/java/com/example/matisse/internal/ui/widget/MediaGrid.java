package com.example.matisse.internal.ui.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;

import com.example.matisse.R;
import com.example.matisse.entity.Item;
import com.example.matisse.internal.entity.SelectionSpec;

import java.util.jar.Attributes;

import androidx.annotation.NonNull;

public class MediaGrid extends SquareFrameLayout {

    private ImageView mThumbnail;
    private CheckView mCheckView;

    private PreBindInfo mPreBindInfo;
    private Item mMedia;
    
    public MediaGrid(@NonNull Context context) {
        super(context);
        init(context);
    }

    public MediaGrid(Context context, AttributeSet attrs) {
        super(context,attrs);
        init(context);
    }

    private void init(Context context) {
        //由于该布局的根标签是merge，所以需要制定MediaGrid为父节点。
        LayoutInflater.from(context).inflate(R.layout.media_grid_content, this, true);
    
        mThumbnail = findViewById(R.id.media_thumbnail);
        mCheckView = findViewById(R.id.check_view);
    }
    
    public void preBindMedia(PreBindInfo preBindInfo){
        mPreBindInfo = preBindInfo;
    }
    
    public void bindMedia(Item item){
        mMedia = item;
        setImage();
    }
    
    private void setImage(){
        SelectionSpec.getInstance().imageEngine.loadThumbnail(getContext(),mPreBindInfo.mResize,
                mPreBindInfo.mPlaceholder, mThumbnail,mMedia.getUri());
    }
    
    public static class PreBindInfo{
        int mResize;
        Drawable mPlaceholder;

        public PreBindInfo(int resize, Drawable placeholder) {
            mResize = resize;
            mPlaceholder = placeholder;
        }
    }
}
