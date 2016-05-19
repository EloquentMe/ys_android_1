package jisuto.drawerapp.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.preference.PreferenceManager;
import android.util.Log;

import com.android.volley.Cache;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.Volley;

import java.io.ByteArrayOutputStream;

import jisuto.drawerapp.model.ImageHolder;
import jisuto.drawerapp.model.loader.CacheImageLoader;
import jisuto.drawerapp.model.loader.GalleryImageLoader;
import jisuto.drawerapp.model.loader.ImageLoader;
import jisuto.drawerapp.model.loader.InternetImageLoader;

public class SingletonCarrier {

    public static final int SCALE_FACTOR = 1;

    public static int DEFAULT_COLUMN_COUNT = 4;

    private static SingletonCarrier mInstance;
    private ContentResolver mContentResolver;
    private RequestQueue mRequestQueue;
    private ImageLoader mInternetImageLoader;
    private ImageLoader mCacheImageLoader;
    private ImageLoader mGalleryImageLoader;
    private CommonImageCache mCache;

    private Context mCtx;
    private int mColumnCount;
    private int mMemoryCacheSize = 10;

    public int getColumnCount() {
        return mColumnCount;
    }

    public ImageLoader getGalleryImageLoader() {
        return mGalleryImageLoader;
    }

    private class CommonImageCache implements com.android.volley.toolbox.ImageLoader.ImageCache {
        private final BitmapCache<String> lruCache;

        private final DiskBasedCache diskCache;

        public CommonImageCache() {
            lruCache = new BitmapCache<>(mMemoryCacheSize * 1024);
            diskCache = new DiskBasedCache(mCtx.getCacheDir(), 1024 * 1024 * 150);
            diskCache.initialize();
        }

        @Override
        public Bitmap getBitmap(String key) {
            Bitmap bitmap = lruCache.get(key);
            if (bitmap == null) {
                Log.d("SingletonCarrier", "Mem Cache miss: " + key);
                Cache.Entry entry = diskCache.get(key);
                if (entry == null) {
                    Log.d("SingletonCarrier", "Disk Cache miss: " + key);
                    return null;
                }
                byte[] data = entry.data;
                bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                lruCache.put(key, bitmap);
            }
            return bitmap;
        }

        @Override
        public void putBitmap(String key, Bitmap bitmap) {
            lruCache.put(key, bitmap);

            Cache.Entry entry = new Cache.Entry();
            ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteStream);
            entry.data = byteStream.toByteArray();
            diskCache.put(key, entry);
        }

        public void clear() {
            lruCache.evictAll();
            diskCache.clear();
        }
    }

    private SingletonCarrier(Context context) {
        mCtx = context;//.getApplicationContext();
        mRequestQueue = getRequestQueue();

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mCtx);
        mColumnCount = Integer.parseInt(sharedPreferences.getString("tab_count", String.valueOf(DEFAULT_COLUMN_COUNT)));
        mMemoryCacheSize = sharedPreferences.getInt("max_cache_size", 10);

        if (mMemoryCacheSize < 1) {
            Log.w("Singletonium", "Cache size cannot be less than 1.");
            mMemoryCacheSize = 1;
        }
        Log.i("Singletonium", "In-memory cache size=" + mMemoryCacheSize);
        mCache = new CommonImageCache();
        boolean clearCache = sharedPreferences.getBoolean("clear_cache", false);
        if (clearCache) {
            mCache.clear();
        }
    }

    private void initLoaders() {
        mInternetImageLoader = new InternetImageLoader(mRequestQueue);
        mCacheImageLoader = new CacheImageLoader();
        mGalleryImageLoader = new GalleryImageLoader();
        mContentResolver = mCtx.getContentResolver();
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
            mInstance.initLoaders();
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

    public com.android.volley.toolbox.ImageLoader.ImageCache getCommonCache () {
        return mCache;
    }
}