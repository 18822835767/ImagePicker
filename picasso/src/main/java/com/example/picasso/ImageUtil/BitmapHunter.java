package com.example.picasso.ImageUtil;

import android.graphics.Bitmap;

import java.io.IOException;
import java.util.List;

public class BitmapHunter implements Runnable,Comparable<BitmapHunter> {

    Picasso picasso;
    private Dispatcher dispatcher;
    private ImageViewAction action;
    private RequestHandler requestHandler;
    private Bitmap result;
    private String key;
    private Request data;
    private int priority;

    private BitmapHunter(Picasso picasso, Dispatcher dispatcher, ImageViewAction action,
                         RequestHandler requestHandler) {
        this.picasso = picasso;
        this.dispatcher = dispatcher;
        this.action = action;
        this.requestHandler = requestHandler;
        this.key = action.getKey();
        this.data = action.getRequest();
    }

    String getKey() {
        return key;
    }

    @Override
    public void run() {
        try {
            result = hunt();

            if (result == null) {
                dispatcher.dispatchFailed(this);
            } else {
                dispatcher.dispatchComplete(this);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Bitmap hunt() throws IOException {
        Bitmap bitmap = ImageMemoryCache.getBitmapFromMemoryCache(key);

        //如果内存缓存中可以直接拿到图片，那么直接返回
        if (bitmap != null) {
            return bitmap;
        }

        bitmap = requestHandler.load(data);
        if (bitmap != null) {
            ImageMemoryCache.addBitmapToMemory(key, bitmap);
        }
        return bitmap;
    }

    Bitmap getResult() {
        return result;
    }

    ImageViewAction getAction() {
        return action;
    }

    static BitmapHunter forRequest(Picasso picasso, Dispatcher dispatcher, ImageViewAction action) {
        Request request = action.getRequest();
        List<RequestHandler> requestHandlers = picasso.getRequestHandlers();

        //责任链模式遍历处理器.
        for (int i = 0, count = requestHandlers.size(); i < count; i++) {
            RequestHandler requestHandler = requestHandlers.get(i);
            if (requestHandler.canHandleRequest(request)) {
                return new BitmapHunter(picasso, dispatcher, action, requestHandler);
            }
        }

        return null;
    }
    
    @Override
    public int compareTo(BitmapHunter o) {
        return Integer.compare(this.priority, o.priority);
    }
}
