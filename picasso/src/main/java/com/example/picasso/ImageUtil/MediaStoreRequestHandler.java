package com.example.picasso.ImageUtil;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Size;

import java.io.IOException;

import static android.content.ContentResolver.SCHEME_CONTENT;

public class MediaStoreRequestHandler extends RequestHandler {
    private Context context;

    MediaStoreRequestHandler(Context context) {
        this.context = context;
    }

    @Override
    public boolean canHandleRequest(Request data) {
        final Uri uri = data.uri;
        return (SCHEME_CONTENT.equals(uri.getScheme())
                && MediaStore.AUTHORITY.equals(uri.getAuthority()));
    }

    @Override
    public Bitmap load(Request request) throws IOException {
        ContentResolver contentResolver = context.getContentResolver();
        
        //加载图片
       return contentResolver.loadThumbnail(request.uri,new Size(request.targetWidth,
                request.targetHeight),null);
    }
}
