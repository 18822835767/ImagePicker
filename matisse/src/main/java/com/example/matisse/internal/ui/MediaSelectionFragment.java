package com.example.matisse.internal.ui;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.matisse.R;
import com.example.matisse.entity.Album;
import com.example.matisse.model.AlbumMediaCollection;

public class MediaSelectionFragment extends Fragment implements AlbumMediaCollection.AlbumMediaCallbacks {

    private static final String TAG = "MediaSelectionFragment";
    private static final String EXTRA_ALBUM = "extra_album";

    private RecyclerView mRecyclerView;
    private AlbumMediaCollection mAlbumMediaCollection = new AlbumMediaCollection();
    
    
    /**
     * @param album 需要加载的album.
     */
    public static MediaSelectionFragment newInstance(Album album) {
        MediaSelectionFragment fragment = new MediaSelectionFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(EXTRA_ALBUM, album);
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
            album = (Album) getArguments().getSerializable(EXTRA_ALBUM);
        }
        mAlbumMediaCollection.onCreate(getActivity(),this);
        mAlbumMediaCollection.load(album);
    }

    @Override
    public void onAlbumMediaLoad(Cursor cursor) {
    }

    @Override
    public void onAlbumMediaReset() {

    }
}
