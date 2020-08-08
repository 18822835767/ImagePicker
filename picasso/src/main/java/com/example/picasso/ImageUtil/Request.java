package com.example.picasso.ImageUtil;

import android.net.Uri;

class Request {
    Uri uri;
    int targetWidth;
    int targetHeight;
    boolean resize;

    private Request(Uri uri, int targetWidth, int targetHeight,boolean resize) {
        this.uri = uri;
        this.targetWidth = targetWidth;
        this.targetHeight = targetHeight;
        this.resize = resize;
    }

    static class Builder {
        Uri uri;
        int targetWidth;
        int targetHeight;
        boolean resize = false;

        Builder(Uri uri) {
            this.uri = uri;
        }

        Builder resize(int targetWidth, int targetHeight) {
            this.targetWidth = targetWidth;
            this.targetHeight = targetHeight;
            resize = true;
            return this;
        }

        Request build() {
            return new Request(uri, targetWidth, targetHeight,resize);
        }

    }
}
