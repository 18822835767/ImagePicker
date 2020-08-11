package com.example.picasso.ImageUtil;

import android.net.Uri;

class Request {
    Uri uri;
    int targetWidth;
    int targetHeight;
    boolean resize;

    /**
     * 加载方式，默认是FIFO.
     */
    boolean LIFO;

    private Request(Uri uri, int targetWidth, int targetHeight, boolean resize, boolean LIFO) {
        this.uri = uri;
        this.targetWidth = targetWidth;
        this.targetHeight = targetHeight;
        this.resize = resize;
        this.LIFO = LIFO;
    }

    static class Builder {
        Uri uri;
        int targetWidth;
        int targetHeight;
        boolean resize = false;
        boolean LIFO = false;

        Builder(Uri uri) {
            this.uri = uri;
        }

        Builder resize(int targetWidth, int targetHeight) {
            this.targetWidth = targetWidth;
            this.targetHeight = targetHeight;
            resize = true;
            return this;
        }

        Builder LIFO(boolean LIFO) {
            this.LIFO = LIFO;
            return this;
        }

        Request build() {
            return new Request(uri, targetWidth, targetHeight, resize, LIFO);
        }

    }
}
