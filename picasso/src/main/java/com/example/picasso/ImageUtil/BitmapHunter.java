package com.example.picasso.ImageUtil;

import android.graphics.Bitmap;

import java.io.IOException;
import java.util.List;

public class BitmapHunter implements Runnable,Comparable<BitmapHunter> {

    /**
     * 这里面的数字代表的是优先级的顺序,根据加载方式来判断该数字是递增还是递减.
     * */
    private static long priorityOrder = 0;
    
    Picasso picasso;
    boolean LIFO;
    private Dispatcher dispatcher;
    private ImageViewAction action;
    private RequestHandler requestHandler;
    private Bitmap result;
    private String key;
    private Request data;
    private long priority;

    private BitmapHunter(Picasso picasso, Dispatcher dispatcher, ImageViewAction action,
                         RequestHandler requestHandler) {
        this.picasso = picasso;
        this.dispatcher = dispatcher;
        this.action = action;
        this.requestHandler = requestHandler;
        this.key = action.getKey();
        this.data = action.getRequest();
        
        //FIFO方式加载，后面加进来的优先级比较低.
        if(!action.isLIFO()){
            this.LIFO = false;
            --priorityOrder;
            this.priority = priorityOrder;
        //LIFO方式加载，后面加进来的优先级比较高.
        }else {
            this.LIFO = true;
            ++priorityOrder;
            this.priority = priorityOrder;
        }
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
        return Long.compare(o.priority, this.priority);
    }
}
