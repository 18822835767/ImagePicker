package com.example.matisse;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;

import com.example.matisse.ui.MatisseActivity;

import java.lang.ref.WeakReference;
import java.util.List;

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

    public SelectionCreator choose() {
        return new SelectionCreator(this);
    }

    public Activity getActivity() {
        return mContext.get();
    }

    /**
     * 用户调用，用于解析MatisseActivity返回的数据.
     */
    public static List<String> obtainPathResult(Intent data) {
        return data.getStringArrayListExtra(MatisseActivity.EXTRA_RESULT_SELECTION_PATH);
    }
}
