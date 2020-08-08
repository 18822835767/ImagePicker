package com.example.picasso.ImageUtil;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import androidx.annotation.NonNull;

import static com.example.picasso.ImageUtil.Dispatcher.HUNTER_BATCH_COMPLETE;


public class Picasso {
    private static volatile Picasso singleton = null;
    private Dispatcher dispatcher;
    private List<RequestHandler> requestHandlers;
    private Context context;

    /**
     * 这里的Object实际上是ImageView.
     */
    private Map<Object, ImageViewAction> targetToAction;

    private static final Handler HANDLER = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case HUNTER_BATCH_COMPLETE: {
                    List<BitmapHunter> batch = (List<BitmapHunter>) msg.obj;
                    for (int i = 0, n = batch.size(); i < n; i++) {
                        BitmapHunter hunter = batch.get(i);
                        hunter.picasso.complete(hunter);
                    }
                    break;
                }
                default:
                    break;
            }
        }
    };

    private Picasso(Context context, Dispatcher dispatcher) {
        this.context = context;
        this.dispatcher = dispatcher;
        requestHandlers = new ArrayList<>();
        requestHandlers.add(new MediaStoreRequestHandler(context));
        this.targetToAction = new WeakHashMap<>();
    }

    public static Picasso get(Context context) {
        if (singleton == null) {
            synchronized (Picasso.class) {
                if (singleton == null) {
                    singleton = new Builder(context).build();
                }
            }
        }
        return singleton;
    }

    public RequestCreator load(Uri uri) {
        return new RequestCreator(this, uri);
    }

    void enqueueAndSubmit(ImageViewAction action) {
        Object target = action.getTarget();

        //如果ImageViewAction中的target为空，直接return
        if (target == null) {
            return;
        }

        ImageViewAction oldAction = targetToAction.get(target);

        //如果该target即ImageView无对应的加载任务，直接加载
        if (oldAction == null) {
            targetToAction.put(target, action);
            dispatcher.dispatchSubmit(action);
            return;
        }

        //如果该target之前的加载任务和当前需要加载的任务不相同，那么重新加载.
        if (!oldAction.getKey().equals(action.getKey())) {
            targetToAction.remove(target);
            dispatcher.dispatchCancel(oldAction);
            targetToAction.put(target, action);
            dispatcher.dispatchSubmit(action);
        }

        //如果该target之前对应的加载任务和当前所需的加载任务一样，那么不进行重复的请求.
    }

    /**
     * 最终展示图片.
     */
    private void complete(BitmapHunter hunter) {
        ImageViewAction action = hunter.getAction();
        targetToAction.remove(action.getTarget());
        ImageView imageView = action.getTarget();
        Bitmap result = hunter.getResult();
        String nowUri = (String) imageView.getTag();

        if (nowUri != null && nowUri.equals(hunter.getKey())) {
            if (result != null) {
                imageView.setImageBitmap(result);
            } else {
                imageView.setImageResource(action.getErrorResId());
            }
        }
    }

    /**
     * 取消所有任务.
     */
    public void cancelAllTasks() {
        for (Object o : targetToAction.keySet()) {
            ImageViewAction action = targetToAction.get(o);
            targetToAction.remove(o);
            dispatcher.dispatchCancel(action);
        }
    }

    List<RequestHandler> getRequestHandlers() {
        return requestHandlers;
    }

    public static class Builder {
        private ExecutorService service;
        private Context context;

        Builder(Context context) {
            this.context = context;
        }

        Picasso build() {
            if (service == null) {
                service = Executors.newFixedThreadPool(10);
            }

            Dispatcher dispatcher = new Dispatcher(context, service, HANDLER);

            return new Picasso(context, dispatcher);
        }
    }
}
