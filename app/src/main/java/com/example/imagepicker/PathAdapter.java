package com.example.imagepicker;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;

public class PathAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    
    private List<String> mPaths;

    public PathAdapter(List<String> paths) {
        mPaths = paths;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.path_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((ViewHolder)holder).mTextView.setText(mPaths.get(position));
    }

    @Override
    public int getItemCount() {
        return mPaths.size();
    }
    
    static class ViewHolder extends RecyclerView.ViewHolder{

        TextView mTextView;
        
        ViewHolder(@NonNull View itemView) {
            super(itemView);
            mTextView = itemView.findViewById(R.id.path_text);
        }
    }
}
