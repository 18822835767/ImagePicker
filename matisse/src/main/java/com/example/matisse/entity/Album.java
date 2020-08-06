package com.example.matisse.entity;

import android.database.Cursor;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

import static com.example.matisse.util.Constant.AlbumLoaderConstants.COLUMN_BUCKET_DISPLAY_NAME;
import static com.example.matisse.util.Constant.AlbumLoaderConstants.COLUMN_BUCKET_ID;
import static com.example.matisse.util.Constant.AlbumLoaderConstants.COLUMN_COUNT;
import static com.example.matisse.util.Constant.AlbumLoaderConstants.COLUMN_URI;

public class Album implements Parcelable {
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

    private Album(Parcel in) {
        id = in.readString();
        coverUri = in.readParcelable(Uri.class.getClassLoader());
        displayName = in.readString();
        count = in.readLong();
    }

    public static final Creator<Album> CREATOR = new Creator<Album>() {
        @Override
        public Album createFromParcel(Parcel in) {
            return new Album(in);
        }

        @Override
        public Album[] newArray(int size) {
            return new Album[size];
        }
    };

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
        return new Album(id, uri, displayName, count);
    }

    /**
     * 判断是不是总相册.
     */
    public boolean isAll() {
        return ALBUM_ID_ALL.equals(id);
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeParcelable(coverUri,0);
        dest.writeString(displayName);
        dest.writeLong(count);
    }
}
