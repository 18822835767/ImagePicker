package com.example.picasso.ImageUtil;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.widget.ImageView;

public class RequestCreator {
    private Picasso picasso;
    private Request.Builder data;
    private Drawable placeholder;
    private int errorResId;
    private boolean setPlaceholder = false;

    RequestCreator(Picasso picasso, Uri uri) {
        this.picasso = picasso;
        data = new Request.Builder(uri);
    }

    public RequestCreator resize(int targetWidth, int targetHeight) {
        data.resize(targetWidth, targetHeight);
        return this;
    }

    public RequestCreator placeholder(Drawable placeholder) {
        this.placeholder = placeholder;
        setPlaceholder = true;
        return this;
    }

    public RequestCreator error(int errorResId) {
        this.errorResId = errorResId;
        return this;
    }

    public void into(ImageView imageView) {
        //设置tag标志.
        imageView.setTag(createKey(data));

        Request request = data.build();
        String requestKey = createKey(data);

        Bitmap bitmap = ImageMemoryCache.getBitmapFromMemoryCache(requestKey);
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
            return;
        }

        if (setPlaceholder) {
            imageView.setImageDrawable(placeholder);
        }

        ImageViewAction action = new ImageViewAction(picasso, imageView, request, errorResId, requestKey);

        picasso.enqueueAndSubmit(action);
    }
    
    private static String createKey(Request.Builder data){
        return data.uri.toString() + data.resize;
    }
}
