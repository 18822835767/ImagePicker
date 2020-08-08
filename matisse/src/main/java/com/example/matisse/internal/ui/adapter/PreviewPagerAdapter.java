package com.example.matisse.internal.ui.adapter;

import com.example.matisse.entity.Item;
import com.example.matisse.internal.ui.PreviewItemFragment;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class PreviewPagerAdapter extends FragmentPagerAdapter {
 
    private List<Item> mItems = new ArrayList<>();
    
    public PreviewPagerAdapter(@NonNull FragmentManager fm) {
        super(fm,BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return PreviewItemFragment.newInstance(mItems.get(position));
    }

    @Override
    public int getCount() {
        return mItems.size();
    }
    
    public void addAll(List<Item> items){
        mItems.addAll(items);
    }
}
