package com.example.matisse;

import android.app.Activity;
import android.content.Intent;

import com.example.matisse.engine.ImageEngine;
import com.example.matisse.internal.entity.SelectionSpec;
import com.example.matisse.ui.MatisseActivity;

public class SelectionCreator {
    private final Matisse mMatisse;
    private final SelectionSpec mSelectionSpec;

    public SelectionCreator(Matisse matisse) {
        mMatisse = matisse;
        mSelectionSpec = SelectionSpec.getCleanInstance();
    }

    public SelectionCreator countable(boolean countable) {
        mSelectionSpec.countable = countable;
        return this;
    }

    public SelectionCreator maxSelectable(int maxSelectable) {
        mSelectionSpec.maxSelectable = maxSelectable;
        return this;
    }

    public SelectionCreator imageEngine(ImageEngine imageEngine) {
        mSelectionSpec.imageEngine = imageEngine;
        return this;
    }

    /**
     * 启动MatisseActivity.
     */
    public void forResult(int requestCode) {
        Activity activity = mMatisse.getActivity();
        if (activity == null) {
            return;
        }

        Intent intent = new Intent(activity, MatisseActivity.class);

        activity.startActivityForResult(intent, requestCode);
    }
}
