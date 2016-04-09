package jisuto.drawerapp.model;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;

import java.lang.ref.WeakReference;

import jisuto.drawerapp.model.LocalImageAdapter.BitmapWorkerTask;

class AsyncDrawable extends BitmapDrawable {
    private final WeakReference<BitmapWorkerTask> bitmapWorkerTaskReference;

    public AsyncDrawable(Resources res,
                         BitmapWorkerTask bitmapWorkerTask) {
        super(res, (Bitmap) null);
        bitmapWorkerTaskReference =
                new WeakReference<>(bitmapWorkerTask);
    }

    public BitmapWorkerTask getBitmapWorkerTask() {
        return bitmapWorkerTaskReference.get();
    }
}