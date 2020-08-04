package com.example.picasso.ImageUtil;

import android.graphics.Bitmap;
import android.util.LruCache;

/**
 * 内存中缓存图片的类.
 * */
class ImageMemoryCache {
    /**
     * 缓存图片，会将缓存好后的图片放在这里，在图片占用内存达到设定的最大值时，会根据算法移除最近最少使用的图片.
     * */
    private static LruCache<String, Bitmap> sMemoryCache;
    
    static {
        //应用程序最大的可用内存
        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        //部分空间作为图片缓存
        int cacheSize  =maxMemory/8;
        sMemoryCache = new LruCache<String,Bitmap>(cacheSize){
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getByteCount();//返回图片所占字节数
            }
        };
    }
    
    /**
     * 向缓存中添加图片.
     * */
    static void addBitmapToMemory(String key, Bitmap bitmap){
        if(getBitmapFromMemoryCache(key) == null){
            sMemoryCache.put(key, bitmap);
        }
    }
    
    /**
     * 从缓存中得到图片.
     * @return 有相应的图片则返回，没有则返回null
     * */
    static Bitmap getBitmapFromMemoryCache(String key){
        return sMemoryCache.get(key);
    }
}
