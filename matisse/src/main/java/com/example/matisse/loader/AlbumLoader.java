package com.example.matisse.loader;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.MergeCursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;

import com.example.matisse.entity.Album;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.loader.content.CursorLoader;

import static com.example.matisse.util.Constant.AlbumLoaderConstants.COLUMN_BUCKET_DISPLAY_NAME;
import static com.example.matisse.util.Constant.AlbumLoaderConstants.COLUMN_BUCKET_ID;
import static com.example.matisse.util.Constant.AlbumLoaderConstants.COLUMN_COUNT;
import static com.example.matisse.util.Constant.AlbumLoaderConstants.COLUMN_URI;
import static com.example.matisse.util.Constant.AlbumLoaderConstants.QUERY_URI;

/**
 * 用于加载图库相册信息的loader.
 */
public class AlbumLoader extends CursorLoader {
    
    /**
     * 表的列的名字,
     */
    private static final String[] COLUMNS = {
            MediaStore.Files.FileColumns._ID,
            COLUMN_BUCKET_ID,
            COLUMN_BUCKET_DISPLAY_NAME,
            MediaStore.MediaColumns.MIME_TYPE,
            COLUMN_URI,
            COLUMN_COUNT};

    /**
     * 版本10之前访问的列.
     */
    private static final String[] PROJECTION = {
            MediaStore.Files.FileColumns._ID,
            COLUMN_BUCKET_ID,
            COLUMN_BUCKET_DISPLAY_NAME,
            MediaStore.MediaColumns.MIME_TYPE,
            "COUNT(*) AS " + COLUMN_COUNT};

    /**
     * 版本10之后访问的列.
     */
    private static final String[] PROJECTION_29 = {
            MediaStore.Files.FileColumns._ID,
            COLUMN_BUCKET_ID,
            COLUMN_BUCKET_DISPLAY_NAME,
            MediaStore.MediaColumns.MIME_TYPE};

    /**
     * 版本10之前的selection
     */
    private static final String SELECTION_FOR_SINGLE_MEDIA_TYPE =
            MediaStore.Files.FileColumns.MEDIA_TYPE + "=?"
                    + " AND " + MediaStore.MediaColumns.SIZE + ">0"
                    + ") GROUP BY (bucket_id";

    /**
     * 版本10之后的selection
     */
    private static final String SELECTION_FOR_SINGLE_MEDIA_TYPE_29 =
            MediaStore.Files.FileColumns.MEDIA_TYPE + "=?"
                    + " AND " + MediaStore.MediaColumns.SIZE + ">0";

    /**
     * 排列方式.
     */
    private static final String BUCKET_ORDER_BY = "datetaken DESC";

    private AlbumLoader(Context context, String selection, String[] selectionArgs) {
        super(
                context,
                QUERY_URI,
                beforeAndroidTen() ? PROJECTION : PROJECTION_29,
                selection,
                selectionArgs,
                BUCKET_ORDER_BY
        );
    }


    public static CursorLoader newInstance(Context context) {
        String selection = beforeAndroidTen()
                ? SELECTION_FOR_SINGLE_MEDIA_TYPE : SELECTION_FOR_SINGLE_MEDIA_TYPE_29;

        String[] selectionArgs = getSelectionArgsForSingleMediaType(
                MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE);

        return new AlbumLoader(context, selection, selectionArgs);
    }

    @Override
    public Cursor loadInBackground() {
        Cursor albums = super.loadInBackground();
        //用于存储"总相册"的信息.
        MatrixCursor allAlbum = new MatrixCursor(COLUMNS);

        if (beforeAndroidTen()) {
            int totalCount = 0;//所有照相的数量
            Uri allAlbumCoverUri = null;//总相册封面图片
            MatrixCursor otherAlbums = new MatrixCursor(COLUMNS);//子相册
            if (albums != null) {
                while (albums.moveToNext()) {
                    long filedId = albums.getLong(
                            albums.getColumnIndex(MediaStore.Files.FileColumns._ID));
                    long bucketId = albums.getLong(
                            albums.getColumnIndex(COLUMN_BUCKET_ID));
                    String bucketDisplayName = albums.getString(
                            albums.getColumnIndex(COLUMN_BUCKET_DISPLAY_NAME));
                    String mimeType = albums.getString(
                            albums.getColumnIndex(MediaStore.MediaColumns.MIME_TYPE));
                    Uri uri = getUri(albums);
                    int count = albums.getInt(albums.getColumnIndex(COLUMN_COUNT));

                    //将子相册的信息存入表中
                    otherAlbums.addRow(new String[]{
                            Long.toString(filedId),
                            Long.toString(bucketId), bucketDisplayName, mimeType, uri.toString(),
                            String.valueOf(count)});
                    //所有图片总数的增加
                    totalCount += count;
                }
                if (albums.moveToFirst()) {
                    //获取"总相册"的封面
                    allAlbumCoverUri = getUri(albums);
                }
            }

            //将"总相册"的信息写入表中.
            allAlbum.addRow(new String[]{
                    Album.ALBUM_ID_ALL, Album.ALBUM_ID_ALL, Album.ALBUM_NAME_ALL, null,
                    allAlbumCoverUri == null ? null : allAlbumCoverUri.toString(),
                    String.valueOf(totalCount)});

            //合并两张表
            return new MergeCursor(new Cursor[]{allAlbum, otherAlbums});
        } else {
            int totalCount = 0;
            Uri allAlbumCoverUri = null;

            //模仿group by id的操作，这里主要是为了获取每个相册的图片数量.
            Map<Long, Long> countMap = new HashMap<>();
            if (albums != null) {
                while (albums.moveToNext()) {
                    long bucketId = albums.getLong(albums.getColumnIndex(COLUMN_BUCKET_ID));

                    Long count = countMap.get(bucketId);
                    if (count == null) {
                        count = 1L;
                    } else {
                        count++;
                    }
                    countMap.put(bucketId, count);
                }
            }

            //子相册的表.
            MatrixCursor otherAlbums = new MatrixCursor(COLUMNS);
            if (albums != null) {
                if (albums.moveToFirst()) {
                    allAlbumCoverUri = getUri(albums);

                    //记录是否已经包含该子相册的信息.
                    Set<Long> done = new HashSet<>();

                    do {
                        long bucketId = albums.getLong(albums.getColumnIndex(COLUMN_BUCKET_ID));

                        //如果包含，直接跳过本轮循环
                        if (done.contains(bucketId)) {
                            continue;
                        }

                        //信息的获取
                        long fileId = albums.getLong(
                                albums.getColumnIndex(MediaStore.Files.FileColumns._ID));
                        String bucketDisplayName = albums.getString(
                                albums.getColumnIndex(COLUMN_BUCKET_DISPLAY_NAME));
                        String mimeType = albums.getString(
                                albums.getColumnIndex(MediaStore.MediaColumns.MIME_TYPE));
                        Uri uri = getUri(albums);
                        long count = countMap.get(bucketId);

                        //将子相册的信息添加到子表中.
                        otherAlbums.addRow(new String[]{
                                Long.toString(fileId),
                                Long.toString(bucketId),
                                bucketDisplayName,
                                mimeType,
                                uri.toString(),
                                String.valueOf(count)});
                        done.add(bucketId);

                        totalCount += count;
                    } while (albums.moveToNext());
                }
            }

            //“总相册”的信息.
            allAlbum.addRow(new String[]{
                    Album.ALBUM_ID_ALL,
                    Album.ALBUM_ID_ALL, Album.ALBUM_NAME_ALL, null,
                    allAlbumCoverUri == null ? null : allAlbumCoverUri.toString(),
                    String.valueOf(totalCount)});

            //2张表合并.
            return new MergeCursor(new Cursor[]{allAlbum, otherAlbums});
        }
    }

    private static Uri getUri(Cursor cursor) {
        long id = cursor.getLong(cursor.getColumnIndex(MediaStore.Files.FileColumns._ID));
        String mimeType = cursor.getString(
                cursor.getColumnIndex(MediaStore.MediaColumns.MIME_TYPE));
        Uri contentUri;

        if (mimeType.startsWith("image")) {
            contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        } else {
            contentUri = MediaStore.Files.getContentUri("external");
        }

        return ContentUris.withAppendedId(contentUri, id);
    }

    private static String[] getSelectionArgsForSingleMediaType(int mediaType) {
        return new String[]{String.valueOf(mediaType)};
    }

    /**
     * 判断版本是否在Android 10 之前
     */
    private static boolean beforeAndroidTen() {
        return Build.VERSION.SDK_INT > Build.VERSION_CODES.Q;
    }
}
