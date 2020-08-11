package com.example.matisse.engine.impl;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.widget.ImageView;

import com.example.matisse.engine.ImageEngine;
import com.example.picasso.ImageUtil.Picasso;

public class PicassoEngine implements ImageEngine {
    @Override
    public void loadThumbnail(Context context, int resize, Drawable placeholder, ImageView imageView, Uri uri) {
        Picasso.get(context).load(uri).placeholder(placeholder)
                .resize(resize,resize)
                .LIFO(true)
                .into(imageView);
    }

    @Override
    public void loadImage(Context context, ImageView imageView, Uri uri) {
        Picasso.get(context).load(uri).into(imageView);
    }
}
