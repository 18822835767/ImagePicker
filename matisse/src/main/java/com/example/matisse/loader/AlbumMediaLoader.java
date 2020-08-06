package com.example.matisse.loader;

import android.content.Context;
import android.net.Uri;
import android.provider.MediaStore;

import com.example.matisse.entity.Album;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.loader.content.CursorLoader;

/**
 * 请求相册下的图片数据的类.
 */
public class AlbumMediaLoader extends CursorLoader {

    private static final Uri QUERY_URI = MediaStore.Files.getContentUri("external");

    private static final String[] PROJECTION = {
            MediaStore.Files.FileColumns._ID,
            MediaStore.MediaColumns.DISPLAY_NAME,
            MediaStore.MediaColumns.SIZE};

    private static final String SELECTION_ALL_FOR_SINGLE_MEDIA_TYPE =
            MediaStore.Files.FileColumns.MEDIA_TYPE + "=?"
                    + " AND " + MediaStore.MediaColumns.SIZE + ">0";

    private static final String SELECTION_ALBUM_FOR_SINGLE_MEDIA_TYPE =
            MediaStore.Files.FileColumns.MEDIA_TYPE + "=?"
                    + " AND "
                    + " bucket_id=?"
                    + " AND " + MediaStore.MediaColumns.SIZE + ">0";

    private static final String ORDER_BY = MediaStore.Images.Media.DATE_TAKEN + " DESC";
    
    public AlbumMediaLoader(Context context, String selection,String[] selectionArgs) {
        super(context, QUERY_URI,PROJECTION, selection, selectionArgs, ORDER_BY);
    }
    
    public static CursorLoader newInstance(Context context, Album album){
        String selection;
        String[] selectionArgs;
        
        if(album.isAll()){
            selection = SELECTION_ALL_FOR_SINGLE_MEDIA_TYPE;
            selectionArgs = getSelectionArgsForSingleMediaType(
                    MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE);
        }else{
            selection = SELECTION_ALBUM_FOR_SINGLE_MEDIA_TYPE;
            selectionArgs = getSelectionAlbumArgsForSingleMediaType(
                    MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE,album.getId());
        }
        return new AlbumMediaLoader(context,selection,selectionArgs);
    }

    private static String[] getSelectionArgsForSingleMediaType(int mediaType) {
        return new String[]{String.valueOf(mediaType)};
    }

    private static String[] getSelectionAlbumArgsForSingleMediaType(int mediaType, String albumId) {
        return new String[]{String.valueOf(mediaType), albumId};
    }
}
