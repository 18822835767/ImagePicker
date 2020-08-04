package com.example.picasso.ImageUtil;

import android.net.Uri;

class Request {
    Uri uri;
    int targetWidth;
    int targetHeight;

    private Request(Uri uri, int targetWidth, int targetHeight) {
        this.uri = uri;
        this.targetWidth = targetWidth;
        this.targetHeight = targetHeight;
    }

    static class Builder {
        Uri uri;
        int targetWidth;
        int targetHeight;

        Builder(Uri uri) {
            this.uri = uri;
        }

        Builder resize(int targetWidth, int targetHeight) {
            this.targetWidth = targetWidth;
            this.targetHeight = targetHeight;
            return this;
        }

        Request build() {
            return new Request(uri, targetWidth, targetHeight);
        }

    }
}
