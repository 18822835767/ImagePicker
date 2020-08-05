package com.example.matisse.util;

import android.net.Uri;
import android.provider.MediaStore;

/**
 * 管理常量.
 */
public class Constant {

    /**
     * 存储加载Album时的一些列名、路径等信息
     */
    public static class AlbumLoaderConstants {
        /**
         * 访问路径.
         */
        public static final Uri QUERY_URI = MediaStore.Files.getContentUri("external");

        public static final String COLUMN_BUCKET_ID = "bucket_id";
        public static final String COLUMN_BUCKET_DISPLAY_NAME = "bucket_display_name";
        public static final String COLUMN_URI = "uri";
        public static final String COLUMN_COUNT = "count";
    }
}
