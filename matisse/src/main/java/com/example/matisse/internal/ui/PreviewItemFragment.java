package com.example.matisse.internal.ui;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.matisse.R;
import com.example.matisse.entity.Item;

/**
 * 作为预览界面下的ViewPager的pager.
 */
public class PreviewItemFragment extends Fragment {

    private static final String ARGS_ITEM = "args_item";
    
    public PreviewItemFragment newInstance(Item item){
        PreviewItemFragment fragment = new PreviewItemFragment();
        Bundle args = new Bundle();
        
        return fragment;
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_preview_item, container, false);
    }
}
