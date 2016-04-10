package jisuto.drawerapp.model;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;

import java.lang.ref.WeakReference;

import jisuto.drawerapp.model.LocalImageAdapter.BitmapWorkerTask;
import jisuto.drawerapp.utils.ImageScaler;

class AsyncDrawable extends BitmapDrawable {
    private final WeakReference<BitmapWorkerTask> bitmapWorkerTaskReference;

    public AsyncDrawable(Resources res,
                         BitmapWorkerTask bitmapWorkerTask) {
        super(res, ImageScaler.getPlaceholder(res));
        bitmapWorkerTaskReference =
                new WeakReference<>(bitmapWorkerTask);
    }

    public BitmapWorkerTask getBitmapWorkerTask() {
        return bitmapWorkerTaskReference.get();
    }
}