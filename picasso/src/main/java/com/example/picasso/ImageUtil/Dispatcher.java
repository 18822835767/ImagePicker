package com.example.picasso.ImageUtil;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;

import androidx.annotation.NonNull;

class Dispatcher {
    private static final String HANDLER_THREAD_NAME = "handler_thread";

    private static final int REQUEST_SUBMIT = 1;
    private static final int REQUEST_CANCEL = 2;
    private static final int HUNTER_COMPLETE = 4;
    private static final int HUNTER_DECODE_FAILED = 6;
    private static final int HUNTER_DELAY_NEXT_BATCH = 7;
    static final int HUNTER_BATCH_COMPLETE = 8;
    private static final int LIFO_DELAY_BATCH_HUNTER = 9;

    private static final int BATCH_DELAY = 200; // ms
    private static final int LIFO_DELAY = 200;//ms

    private Context context;
    private HandlerThread dispatcherThread;
    private ExecutorService service;
    private Handler handler;
    private Handler mainThreadHandler;
    private List<BitmapHunter> batch;

    /**
     * 存储的是进行LIFO加载的BitmapHunter.
     */
    private LinkedList<BitmapHunter> LIFOHunters = new LinkedList<>();

    /**
     * 这里的是String存储的是图片标识(例如图片的uri).
     */
    private Map<String, BitmapHunter> hunterMap;

    Dispatcher(Context context, ExecutorService service, Handler mainThreadHandler) {
        this.dispatcherThread = new HandlerThread(HANDLER_THREAD_NAME);
        this.dispatcherThread.start();
        this.context = context;
        this.service = service;
        this.hunterMap = new LinkedHashMap<>();
        this.handler = new DispatcherHandler(dispatcherThread.getLooper(), this);
        this.mainThreadHandler = mainThreadHandler;
        this.batch = new ArrayList<>(4);
    }

    void dispatchSubmit(ImageViewAction action) {
        handler.sendMessage(handler.obtainMessage(REQUEST_SUBMIT, action));
    }

    void dispatchCancel(ImageViewAction action) {
        handler.sendMessage(handler.obtainMessage(REQUEST_CANCEL, action));
    }

    void dispatchFailed(BitmapHunter hunter) {
        handler.sendMessage(handler.obtainMessage(HUNTER_DECODE_FAILED, hunter));
    }

    void dispatchComplete(BitmapHunter hunter) {
        handler.sendMessage(handler.obtainMessage(HUNTER_COMPLETE, hunter));
    }

    private void performBatchComplete() {
        List<BitmapHunter> copy = new ArrayList<>(batch);
        batch.clear();
        mainThreadHandler.sendMessage(mainThreadHandler.obtainMessage(HUNTER_BATCH_COMPLETE, copy));
    }

    /**
     * 对于已经到达延迟时间的LIFO下的BitmapHunter进行处理.
     */
    private void performLIFOBatchComplete() {
        List<BitmapHunter> copy = new ArrayList<>(LIFOHunters);
        LIFOHunters.clear();
        for (BitmapHunter hunter : copy) {
            service.execute(hunter);
        }
    }

    private void performSubmit(ImageViewAction action) {
        //如果线程池已经关闭,直接return
        if (service.isShutdown()) {
            return;
        }

        BitmapHunter hunter = BitmapHunter.forRequest(action.getPicasso(), this, action);
        if (hunter != null) {
            //FIFO
            if (!action.isLIFO()) {
                //提交任务
                service.execute(hunter);
                //LIFO
            } else {
                LIFOHunterBatch(hunter);
            }

        }
        hunterMap.put(action.getKey(), hunter);
    }

    private void performError(BitmapHunter hunter) {
        hunterMap.remove(hunter.getKey());
        batch(hunter);
    }

    private void performComplete(BitmapHunter hunter) {
        hunterMap.remove(hunter.getKey());
        batch(hunter);
    }

    private void performCancel(ImageViewAction action) {
        //先试着从LIFO批处理的队列中进行移除
        LIFOHunters.remove(hunterMap.get(action.getKey()));
        //然后再从hunterMap以及线程池里移除
        String key = action.getKey();
        BitmapHunter hunter = hunterMap.get(key);

        if (hunter != null) {
            //移除对应的任务
            ((ThreadPoolExecutor) service).remove(hunter);
            hunterMap.remove(key);
        }
    }

    /**
     * 更新UI时，进行批处理操作.
     */
    private void batch(BitmapHunter hunter) {
        batch.add(hunter);
        if (!handler.hasMessages(HUNTER_DELAY_NEXT_BATCH)) {
            handler.sendEmptyMessageDelayed(HUNTER_DELAY_NEXT_BATCH, BATCH_DELAY);
        }

    }

    /**
     * 对于LIFO加载的BitmapHunter，进行延迟的批处理操作.
     */
    private void LIFOHunterBatch(BitmapHunter hunter) {
        LIFOHunters.addFirst(hunter);
        if (!handler.hasMessages(LIFO_DELAY_BATCH_HUNTER)) {
            handler.sendEmptyMessageDelayed(LIFO_DELAY_BATCH_HUNTER, LIFO_DELAY);
        }
    }

    private static class DispatcherHandler extends Handler {
        private Dispatcher dispatcher;

        DispatcherHandler(Looper looper, Dispatcher dispatcher) {
            super(looper);
            this.dispatcher = dispatcher;
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case REQUEST_SUBMIT: {
                    ImageViewAction action = (ImageViewAction) msg.obj;
                    dispatcher.performSubmit(action);
                    break;
                }
                case REQUEST_CANCEL: {
                    ImageViewAction action = (ImageViewAction) msg.obj;
                    dispatcher.performCancel(action);
                    break;
                }
                case HUNTER_DECODE_FAILED: {
                    BitmapHunter hunter = (BitmapHunter) msg.obj;
                    dispatcher.performError(hunter);
                    break;
                }
                case HUNTER_COMPLETE: {
                    BitmapHunter hunter = (BitmapHunter) msg.obj;
                    dispatcher.performComplete(hunter);
                    break;
                }
                case HUNTER_DELAY_NEXT_BATCH: {
                    dispatcher.performBatchComplete();
                    break;
                }
                case LIFO_DELAY_BATCH_HUNTER: {
                    dispatcher.performLIFOBatchComplete();
                    break;
                }
                default:
                    break;
            }
        }
    }
}
