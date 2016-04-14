package jisuto.drawerapp.utils;

import android.graphics.Bitmap;
import android.util.Log;
import android.util.LruCache;

public class BitmapCache<K> extends LruCache<K, Bitmap> {
    /**
     * @param kilobytes max size of cache
     */
    public BitmapCache(int kilobytes) {
        super(kilobytes);
    }

    @Override
    protected int sizeOf(K key, Bitmap value) {
        return value.getByteCount() / 1024;
    }

    @Override
    protected void entryRemoved(boolean evicted, K key, Bitmap oldValue, Bitmap newValue) {
        super.entryRemoved(evicted, key, oldValue, newValue);
    }
}