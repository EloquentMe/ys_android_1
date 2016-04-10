package jisuto.drawerapp.model.loader;

import android.content.res.Resources;
import android.graphics.drawable.BitmapDrawable;

import java.lang.ref.SoftReference;

import jisuto.drawerapp.model.loader.CacheImageLoader.BitmapWorkerTask;
import jisuto.drawerapp.utils.ImageScaler;

public class AsyncDrawable extends BitmapDrawable {
    private final SoftReference<BitmapWorkerTask> bitmapWorkerTaskReference;

    public AsyncDrawable(Resources res,
                         BitmapWorkerTask bitmapWorkerTask) {
        super(res, ImageScaler.getPlaceholder(res));
        bitmapWorkerTaskReference =
                new SoftReference<>(bitmapWorkerTask);
    }

    public BitmapWorkerTask getBitmapWorkerTask() {
        return bitmapWorkerTaskReference.get();
    }
}