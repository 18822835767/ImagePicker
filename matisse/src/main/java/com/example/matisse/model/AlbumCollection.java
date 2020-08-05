package com.example.matisse.model;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;

import com.example.matisse.loader.AlbumLoader;

import java.lang.ref.WeakReference;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;

/**
 * 该类作为Loader机制的中间层，通过定义接口回调的方式将数据传送给Activity.
 */
public class AlbumCollection implements LoaderManager.LoaderCallbacks<Cursor> {

    /**
     * 弱引用方式持有Activity的引用.
     */
    private WeakReference<Context> mContext;

    /**
     * Loader的Id.
     */
    private static final int LOADER_ID = 1;

    /**
     * 记录当前选中的相册索引，初始时为0.
     */
    private int mCurrentSelection;

    private AlbumCallbacks mCallbacks;
    private LoaderManager mLoaderManager;
    private boolean mLoadFinished;

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        Context context = mContext.get();
        if (context == null) {
            return null;
        }

        mLoadFinished = false;

        return AlbumLoader.newInstance(context);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        Context context = mContext.get();
        if (context == null) {
            return;
        }

        if (!mLoadFinished) {
            mLoadFinished = true;
            mCallbacks.onAlbumLoad(data);
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        Context context = mContext.get();
        if (context == null) {
            return;
        }

        mCallbacks.onAlbumReset();
    }

    public void onCreate(FragmentActivity activity, AlbumCallbacks callbacks) {
        mContext = new WeakReference<Context>(activity);
        mLoaderManager = LoaderManager.getInstance(activity);
        mCallbacks = callbacks;
    }

    public void onDestroy() {
        if (mLoaderManager != null) {
            mLoaderManager.destroyLoader(LOADER_ID);
        }
        mCallbacks = null;
    }

    public void loadAlbums() {
        mLoaderManager.initLoader(LOADER_ID, null, this);
    }

    public int getCurrentSelection() {
        return mCurrentSelection;
    }

    public void setStateCurrentSelection(int currentSelection) {
        mCurrentSelection = currentSelection;
    }

    public interface AlbumCallbacks {
        void onAlbumLoad(Cursor cursor);

        void onAlbumReset();
    }
}
