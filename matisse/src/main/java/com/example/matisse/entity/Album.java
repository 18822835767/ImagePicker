package com.example.matisse.entity;

import android.net.Uri;

import java.io.Serializable;

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
