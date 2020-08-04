package com.example.picasso.ImageUtil;

import android.graphics.Bitmap;

import java.io.IOException;

public abstract class RequestHandler {
    public abstract boolean canHandleRequest(Request data);
    
    public abstract Bitmap load(Request request) throws IOException;
}
