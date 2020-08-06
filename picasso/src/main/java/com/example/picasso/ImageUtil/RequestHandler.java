package com.example.picasso.ImageUtil;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.IOException;

public abstract class RequestHandler {
    public abstract boolean canHandleRequest(Request data);
    
    public abstract Bitmap load(Request request) throws IOException;
    
    static void calculateInSampleSize(int reqWidth, int reqHeight, int width, int height,
                                      BitmapFactory.Options options){
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            int heightRatio = Math.round((float) height / (float) reqHeight);
            int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = Math.min(heightRatio, widthRatio);
        }
        options.inSampleSize = inSampleSize;
        options.inJustDecodeBounds = false;
    }
}
