package com.example.picasso.ImageUtil;

import android.widget.ImageView;

import java.lang.ref.WeakReference;

class ImageViewAction {
    private Picasso picasso;
    private Request request;
    private WeakReference<ImageView> target;
    private int errorResId;
    private String key;
    private boolean LIFO;
    
    ImageViewAction(Picasso picasso, ImageView imageView, Request data, int errorResId, String key) {
        this.picasso = picasso;
        this.target = new WeakReference<>(imageView);
        request = data;
        this.errorResId = errorResId;
        this.key = key;
        this.LIFO = data.LIFO;
    }
    
    ImageView getTarget(){
        if(target != null){
            return target.get();
        }
        return null;
    }
    
    String getKey(){
        return key;
    }
    
    Request getRequest(){
        return request;
    }

    Picasso getPicasso() {
        return picasso;
    }

    int getErrorResId() {
        return errorResId;
    }

    public boolean isLIFO() {
        return LIFO;
    }
}
