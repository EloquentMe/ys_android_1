package jisuto.drawerapp.utils;

import android.app.LoaderManager;
import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.util.LruCache;
import android.util.Log;

import com.android.volley.Cache;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.Volley;

import java.io.ByteArrayOutputStream;

import jisuto.drawerapp.model.loader.CacheImageLoader;
import jisuto.drawerapp.model.loader.GalleryImageLoader;
import jisuto.drawerapp.model.loader.ImageLoader;
import jisuto.drawerapp.model.loader.InternetImageLoader;

public class SingletonCarrier {
    private static SingletonCarrier mInstance;
    private ContentResolver mContentResolver;
    private RequestQueue mRequestQueue;
    private ImageLoader mInternetImageLoader;
    private ImageLoader mCacheImageLoader;
    private ImageLoader mGalleryImageLoader;

    private Context mCtx;

    public ImageLoader getGalleryImageLoader() {
        return mGalleryImageLoader;
    }

    private class InternetImageCache implements com.android.volley.toolbox.ImageLoader.ImageCache {
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
    }

    private SingletonCarrier(Context context) {
        mCtx = context.getApplicationContext();
        mRequestQueue = getRequestQueue();

        mInternetImageLoader = new InternetImageLoader(mRequestQueue, new InternetImageCache());
        mCacheImageLoader = new CacheImageLoader();
        mGalleryImageLoader = new GalleryImageLoader();
        mContentResolver = context.getContentResolver();
    }

    public static synchronized SingletonCarrier getInstance() {
        if (mInstance == null) {
            throw new RuntimeException("Not initialized, call init()");
        }
        return mInstance;
    }

    public static synchronized void init(Context context) {
        if (mInstance != null) {
            Log.i("DrawerApp", "Singletonium: Already initialized");
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

    public ImageLoader getInternetImageLoader() {
        return mInternetImageLoader;
    }

    public ImageLoader getCacheImageLoader() {
        return mCacheImageLoader;
    }

    public Context getContext() {
        return mCtx;
    }

    public ContentResolver getContentResolver() {
        return mContentResolver;
        //TODO: LoaderManager!
    }
}