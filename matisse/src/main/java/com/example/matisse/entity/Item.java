package com.example.matisse.entity;

import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.Serializable;

public class Item implements Serializable {
    private long id;
    private Uri uri;
    private long size;

    public Item(long id, long size) {
        this.id = id;
        Uri contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        this.uri = ContentUris.withAppendedId(contentUri,id);
        this.size = size;
    }
    
    public static Item valueOf(Cursor cursor){
        return new Item(cursor.getLong(cursor.getColumnIndex(MediaStore.Files.FileColumns._ID)),
                cursor.getLong(cursor.getColumnIndex(MediaStore.MediaColumns.SIZE)));
    }

    public Uri getUri() {
        return uri;
    }
}
