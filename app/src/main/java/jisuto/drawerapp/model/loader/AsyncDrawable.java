package jisuto.drawerapp.model.loader;

import android.content.res.Resources;
import android.graphics.drawable.BitmapDrawable;

import java.lang.ref.SoftReference;

import jisuto.drawerapp.utils.ImageScaler;

public class AsyncDrawable extends BitmapDrawable {
    private final SoftReference<BitmapWorkerTask> workerReference;

    public AsyncDrawable(Resources res,
                         BitmapWorkerTask worker) {
        super(res, ImageScaler.getPlaceholder(res));
        workerReference =
                new SoftReference<>(worker);
    }

    public BitmapWorkerTask getBitmapWorkerTask() {
        return workerReference.get();
    }
}