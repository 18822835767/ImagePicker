package com.example.picasso.ImageUtil;

import android.graphics.Bitmap;
import android.net.Uri;
import android.widget.ImageView;

public class RequestCreator {
    private Picasso picasso;
    private Request.Builder data;
    private int placeholderResId;
    private int errorResId;
    private boolean setPlaceholder = false;

    RequestCreator(Picasso picasso, Uri uri) {
        this.picasso = picasso;
        data = new Request.Builder(uri);
    }
    
    public RequestCreator resize(int targetWidth,int targetHeight){
        data.resize(targetWidth,targetHeight);
        return this;
    }
    
    public RequestCreator placeholder(int placeholderResId){
        this.placeholderResId = placeholderResId;
        setPlaceholder = true;
        return this;
    }
    
    public RequestCreator error(int errorResId){
        this.errorResId = errorResId;
        return this;
    }
    
    public void into(ImageView imageView){
        Request request = data.build();
        String requestKey = data.uri.toString();

        Bitmap bitmap = ImageMemoryCache.getBitmapFromMemoryCache(requestKey);
        if(bitmap != null){
            imageView.setImageBitmap(bitmap);
            return;
        }
        
        if(setPlaceholder){
            imageView.setImageResource(placeholderResId);
        }
        
        ImageViewAction action = new ImageViewAction(picasso,imageView,request,errorResId,requestKey);
         
        picasso.enqueueAndSubmit(action);
    }
}
