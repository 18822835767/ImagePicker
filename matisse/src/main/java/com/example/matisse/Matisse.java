package com.example.matisse;

import android.app.Activity;

import java.lang.ref.WeakReference;

public class Matisse {
    private final WeakReference<Activity> mContext;

    private Matisse(Activity activity) {
        mContext = new WeakReference<>(activity);
    }

    /**
     * 获取一个Matisse对象.
     */
    public static Matisse from(Activity activity) {
        return new Matisse(activity);
    }
    
    public SelectionCreator choose(){
        return new SelectionCreator(this);
    }
    
    public Activity getActivity(){
        return mContext.get();
    }
}
