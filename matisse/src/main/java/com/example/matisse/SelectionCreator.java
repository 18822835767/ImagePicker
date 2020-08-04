package com.example.matisse;

import com.example.matisse.internal.entity.SelectionSpec;

public class SelectionCreator {
    private final Matisse mMatisse;
    private final SelectionSpec mSelectionSpec;

    public SelectionCreator(Matisse matisse) {
        mMatisse = matisse;
        mSelectionSpec = SelectionSpec.getCleanInstance();
    }
}
