package jisuto.drawerapp.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.util.LruCache;

import com.android.volley.Cache;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

import java.io.ByteArrayOutputStream;

public class SingletonCarrier {
    private static SingletonCarrier mInstance;
    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;
    private static Context mCtx;

    private SingletonCarrier(Context context) {
        mCtx = context.getApplicationContext();
        mRequestQueue = getRequestQueue();

        mImageLoader = new ImageLoader(mRequestQueue,
                new ImageLoader.ImageCache() {
                    private final LruCache<String, Bitmap>
                            lruCache = new LruCache<String, Bitmap>(40);
                    private final DiskBasedCache diskCache = new DiskBasedCache(mCtx.getCacheDir(), 1024 * 1024 * 50);

                    @Override
                    public Bitmap getBitmap(String url) {
                        Bitmap bitmap = lruCache.get(url);
                        if (bitmap == null) {
                            Cache.Entry entry = diskCache.get(url);
                            if (entry == null) {
                                return null;
                            }
                            byte[] data = entry.data;
                            bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                        }
                        return bitmap;
                    }

                    @Override
                    public void putBitmap(String url, Bitmap bitmap) {
                        lruCache.put(url, bitmap);

                        Cache.Entry entry = new Cache.Entry();
                        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteStream);
                        entry.data = byteStream.toByteArray();
                        diskCache.put(url, entry);
                    }
                });
    }

    public static synchronized SingletonCarrier getInstance() {
        if (mInstance == null) {
            throw new RuntimeException("Not initialized, call init()");
        }
        return mInstance;
    }

    public static synchronized void init(Context context) {
        if (mInstance != null) {
            throw new RuntimeException("Already initialized");
        } else {
            mInstance = new SingletonCarrier(context);
        }
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            mRequestQueue = Volley.newRequestQueue(mCtx, new HurlStack());
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }

    public ImageLoader getImageLoader() {
        return mImageLoader;
    }

}