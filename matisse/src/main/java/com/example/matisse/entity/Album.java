package com.example.matisse.entity;

import android.database.Cursor;
import android.net.Uri;

import java.io.Serializable;

import static com.example.matisse.util.Constant.AlbumLoaderConstants.COLUMN_BUCKET_DISPLAY_NAME;
import static com.example.matisse.util.Constant.AlbumLoaderConstants.COLUMN_BUCKET_ID;
import static com.example.matisse.util.Constant.AlbumLoaderConstants.COLUMN_COUNT;
import static com.example.matisse.util.Constant.AlbumLoaderConstants.COLUMN_URI;

public class Album implements Serializable {
    /**
     * "全部"相册的信息.
     */
    public static final String ALBUM_ID_ALL = String.valueOf(-1);
    public static final String ALBUM_NAME_ALL = "All";

    private String id;
    private Uri coverUri;
    private String displayName;
    private long count;

    public Album(String id, Uri coverUri, String displayName, long count) {
        this.id = id;
        this.coverUri = coverUri;
        this.displayName = displayName;
        this.count = count;
    }

    /**
     * @param cursor 传入一个cursor
     * @return 根据当前cursor所在的行，构造出一个Album对象
     */
    public static Album valueOf(Cursor cursor) {
        String uriPath = cursor.getString(cursor.getColumnIndex(COLUMN_URI));
                
        String id = cursor.getString(cursor.getColumnIndex(COLUMN_BUCKET_ID));
        Uri uri = uriPath == null ? null : Uri.parse(uriPath);
        String displayName = cursor.getString(cursor.getColumnIndex(COLUMN_BUCKET_DISPLAY_NAME));
        long count = cursor.getLong(cursor.getColumnIndex(COLUMN_COUNT));
        return new Album(id,uri,displayName,count);
    }

    public String getId() {
        return id;
    }

    public Uri getCoverUri() {
        return coverUri;
    }

    public String getDisplayName() {
        return displayName;
    }

    public long getCount() {
        return count;
    }
}
