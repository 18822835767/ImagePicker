package com.example.matisse.internal.ui;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.matisse.R;
import com.example.matisse.entity.Album;
import com.example.matisse.internal.ui.adapter.AlbumMediaAdapter;
import com.example.matisse.model.AlbumMediaCollection;

public class MediaSelectionFragment extends Fragment implements AlbumMediaCollection.AlbumMediaCallbacks {

    private static final String TAG = "MediaSelectionFragment";
    private static final String EXTRA_ALBUM = "extra_album";

    private RecyclerView mRecyclerView;
    private AlbumMediaCollection mAlbumMediaCollection = new AlbumMediaCollection();
    private AlbumMediaAdapter mAdapter;
    
    
    
    /**
     * @param album 需要加载的album.
     */
    public static MediaSelectionFragment newInstance(Album album) {
        MediaSelectionFragment fragment = new MediaSelectionFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(EXTRA_ALBUM, album);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_media_selection, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mRecyclerView = view.findViewById(R.id.recyclerview);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Album album = null;
        if(getArguments() != null){
            album = (Album) getArguments().getParcelable(EXTRA_ALBUM);
        }
        
        mAdapter = new AlbumMediaAdapter(getContext(),mRecyclerView);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(),3));
        mRecyclerView.setAdapter(mAdapter);
        
        mAlbumMediaCollection.onCreate(getActivity(),this);
        mAlbumMediaCollection.load(album);
    }

    @Override
    public void onAlbumMediaLoad(Cursor cursor) {
        if(cursor.moveToFirst()){
            for(int i=0;i<10;i++){
                cursor.moveToPosition(i);
                Log.d(TAG, "onAlbumMediaLoad: "+cursor.getLong(cursor.getColumnIndex(MediaStore.MediaColumns.SIZE)));
            }
        }
        mAdapter.swapCursor(cursor);
    }

    @Override
    public void onAlbumMediaReset() {
        mAdapter.swapCursor(null);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");
        mAlbumMediaCollection.onDestroy();
    }
}
