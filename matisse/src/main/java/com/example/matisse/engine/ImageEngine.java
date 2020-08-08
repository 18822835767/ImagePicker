package com.example.matisse.engine;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.widget.ImageView;

public interface ImageEngine {
    /**
     * 加载缩略图.
     */
    void loadThumbnail(Context context, int resize, Drawable placeholder, ImageView imageView, Uri uri);

    /**
     * 加载完整的图.
     * */
    void loadImage(Context context, ImageView imageView, Uri uri);
}
