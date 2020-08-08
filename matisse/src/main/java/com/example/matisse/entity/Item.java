package com.example.matisse.entity;

import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.MediaStore;

import java.io.Serializable;

import androidx.annotation.Nullable;

public class Item implements Parcelable {
    private long id;
    private Uri uri;
    private long size;

    public Item(long id, long size) {
        this.id = id;
        Uri contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        this.uri = ContentUris.withAppendedId(contentUri,id);
        this.size = size;
    }

    protected Item(Parcel in) {
        id = in.readLong();
        uri = in.readParcelable(Uri.class.getClassLoader());
        size = in.readLong();
    }

    public static final Creator<Item> CREATOR = new Creator<Item>() {
        @Override
        public Item createFromParcel(Parcel in) {
            return new Item(in);
        }

        @Override
        public Item[] newArray(int size) {
            return new Item[size];
        }
    };

    public static Item valueOf(Cursor cursor){
        return new Item(cursor.getLong(cursor.getColumnIndex(MediaStore.Files.FileColumns._ID)),
                cursor.getLong(cursor.getColumnIndex(MediaStore.MediaColumns.SIZE)));
    }

    public Uri getUri() {
        return uri;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if(!(obj instanceof Item)){
            return false;
        }
        
        Item item = (Item) obj;
        
        return id == item.id 
                && (uri != null && uri.equals(item.uri))
                    || (uri == null && item.uri == null)
                && size == item.size;
    }

    @Override
    public int hashCode() {
        int result = 1;
        result = result * 31 + Long.valueOf(id).hashCode();
        result = result * 31 + (uri == null ? 0 : uri.hashCode());
        result = result * 31 + Long.valueOf(size).hashCode();
        return result;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeParcelable(uri, flags);
        dest.writeLong(size);
    }
}
