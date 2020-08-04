package com.example.matisse.internal.entity;

import com.example.matisse.engine.ImageEngine;
import com.example.matisse.engine.impl.PicassoEngine;

/**
 * 用于保存用户所需的配置信息.
 */
public class SelectionSpec {
    public boolean countable;
    public int maxSelectable;
    public ImageEngine imageEngine;

    private SelectionSpec() {
    }

    public static SelectionSpec getInstance() {
        return InstanceHolder.INSTANCE;
    }

    public static SelectionSpec getCleanInstance(){
        SelectionSpec selectionSpec = getInstance();
        selectionSpec.reset();
        return selectionSpec;
    }
    
    /**
     * 初始化内部信息.
     */
    private void reset() {
        countable = false;
        maxSelectable = 1;
        imageEngine = new PicassoEngine();
    }

    /**
     * 静态内部类的方式获取单例.
     */
    private static final class InstanceHolder {
        private static final SelectionSpec INSTANCE = new SelectionSpec();
    }
}
