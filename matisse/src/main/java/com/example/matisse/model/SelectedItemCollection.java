package com.example.matisse.model;

import android.os.Bundle;
import android.os.Parcelable;

import com.example.matisse.entity.Item;
import com.example.matisse.internal.entity.SelectionSpec;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static com.example.matisse.internal.ui.widget.CheckView.UNCHECKED;

/**
 * 管理被选中的图片，设计为单例模式.
 */
public class SelectedItemCollection {
    
    /**
     * 存储被选中的Item.
     */
    private Set<Item> mItems = new LinkedHashSet<>();

    private SelectedItemCollection() {
    }

    public static SelectedItemCollection getInstance() {
        return SelectedItemCollectionInner.instance;
    }

    public boolean add(Item item) {
        return mItems.add(item);
    }

    public boolean remove(Item item) {
        return mItems.remove(item);
    }

    public boolean isSelected(Item item) {
        return mItems.contains(item);
    }

    public boolean maxSelectableReached() {
        return mItems.size() == SelectionSpec.getInstance().maxSelectable;
    }

    public ArrayList<Item> getItems(){
        return new ArrayList<>(mItems);
    }
    
    public boolean isEmpty(){
        return mItems.size() == 0;
    }
    
    /**
     * 返回item的索引.
     */
    public int checkNumOf(Item item) {
        int index = new ArrayList<>(mItems).indexOf(item);
        return index == -1 ? UNCHECKED : index + 1;
    }

    public void reset(){
        mItems.clear();
    }
    
    private static class SelectedItemCollectionInner {
        private static SelectedItemCollection instance = new SelectedItemCollection();
    }
}
