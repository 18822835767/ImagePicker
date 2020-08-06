package com.example.matisse.model;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;

import com.example.matisse.entity.Album;
import com.example.matisse.loader.AlbumMediaLoader;

import java.lang.ref.WeakReference;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;

public class AlbumMediaCollection implements LoaderManager.LoaderCallbacks<Cursor> {

    /**
     * 弱引用方式持有Activity的引用.
     */
    private WeakReference<Context> mContext;

    /**
     * Loader的Id.
     */
    private static final int LOADER_ID = 2;

    private static final String ARGS_ALBUM = "args_album";

    private LoaderManager mLoaderManager;

    private AlbumMediaCallbacks mCallbacks;

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        Context context = mContext.get();
        if(context == null){
            return null;
        }
        
        if(args != null){
            Album album = (Album) args.getParcelable(ARGS_ALBUM);
            if(album == null){
                return null;
            }else{
                return AlbumMediaLoader.newInstance(context,album);
            }
        }
        
        return null;
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        Context context = mContext.get();
        if(context == null){
            return ;
        }
        mCallbacks.onAlbumMediaLoad(data);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        Context context = mContext.get();
        if(context == null){
            return;
        }
        
        mCallbacks.onAlbumMediaReset();
    }
    
    public void load(Album target){
        Bundle args = new Bundle();
        args.putParcelable(ARGS_ALBUM,target);
        mLoaderManager.initLoader(LOADER_ID,args,this);
        
    }

    public void onCreate(FragmentActivity activity, AlbumMediaCallbacks callbacks) {
        mContext = new WeakReference<Context>(activity);
        mLoaderManager = LoaderManager.getInstance(activity);
        mCallbacks = callbacks;
    }

    public void onDestroy() {
        if (mLoaderManager != null) {
            mLoaderManager.destroyLoader(LOADER_ID);
        }
        //引用置空，防止内存泄漏.
        mCallbacks = null;
    }

    /**
     * 加载完数据后的回调接口.
     */
    public interface AlbumMediaCallbacks {
        void onAlbumMediaLoad(Cursor cursor);

        void onAlbumMediaReset();
    }
}
