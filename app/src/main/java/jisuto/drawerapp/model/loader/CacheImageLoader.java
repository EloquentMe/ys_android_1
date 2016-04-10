package jisuto.drawerapp.model.loader;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.LruCache;
import android.widget.ImageView;

import java.io.IOException;
import java.io.NotActiveException;
import java.io.ObjectInputStream;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import jisuto.drawerapp.R;
import jisuto.drawerapp.model.ImageHolder;
import jisuto.drawerapp.utils.ImageScaler;
import jisuto.drawerapp.utils.LoadListener;
import jisuto.drawerapp.utils.SingletonCarrier;

public class CacheImageLoader implements ImageLoader {

    public static final int[] allItems = new int[]{
            R.drawable.image_1,
            R.drawable.image_2,
            R.drawable.image_3,
            R.drawable.image_4,
            R.drawable.image_5,
            R.drawable.image_6};
    static {
        shuffleItems();
    }

    private transient LruCache<Integer, Bitmap> itemList;
    private LoadListener eventListener;

    private static void shuffleItems() {
        Random r = new Random();
        for (int i = 0; i < allItems.length; i++) {
            int rIndex;
            do {
                rIndex = r.nextInt(allItems.length);
            } while (rIndex == i);
            int temp = allItems[i];
            allItems[i] = allItems[rIndex];
            allItems[rIndex] = temp;
        }
    }

    class BitmapWorkerTask extends AsyncTask<Integer, Void, Bitmap> {
        private final SoftReference<ImageView> imageViewReference;
        public int position;

        public BitmapWorkerTask(ImageView imageView) {
            // Use a WeakReference to ensure the ImageView can be garbage collected
            imageViewReference = new SoftReference<>(imageView);
        }

        // Decode image in background.
        @Override
        protected Bitmap doInBackground(Integer... params) {
            position = params[0] % 6;
            Bitmap pic = itemList.get(position);
            if (pic == null) {
                Context context = SingletonCarrier.getInstance().getContext();
                pic = ImageScaler.decodeSampledBitmapFromResource(context.getResources(),
                        allItems[position], 100, 100);
                itemList.put(position, pic);
            }
            return pic;
        }

        // Once complete, see if ImageView is still around and set bitmap.
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (isCancelled()) {
                bitmap = null;
            }
            final ImageView imageView = imageViewReference.get();
            if (imageView != null && bitmap != null) {
                imageView.setImageBitmap(bitmap);
            }
        }
    }

    public CacheImageLoader() {
        itemList = new LruCache<>(allItems.length);
    }

    private final void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();

        itemList = new LruCache<>(allItems.length);
    }

    private boolean potentialCancel(int position, ImageView view) {
        if (view.getDrawable() instanceof AsyncDrawable) {
            BitmapWorkerTask task = ((AsyncDrawable) view.getDrawable()).getBitmapWorkerTask();
            if (!view.equals(task.imageViewReference.get()) || position != task.position) {
                task.cancel(true);
                return true;
            } else {
                return false;
            }
        } else {
            return true;
        }
    }

    public class CacheImageContainer implements ImageHolder.ImageContainer {

        BitmapWorkerTask task;

        CacheImageContainer(BitmapWorkerTask task) {
            this.task = task;
        }

        @Override
        public void cancelRequest() {
            task.cancel(true);
        }
    }

    @Override
    public void get(int position, ImageHolder holder) {
        ImageView view = holder.getImage();
        boolean cancelled = potentialCancel(position, view);
        if (cancelled) {
            BitmapWorkerTask task = new BitmapWorkerTask(view);
            Context context = SingletonCarrier.getInstance().getContext();
            AsyncDrawable drawable = new AsyncDrawable(context.getResources(), task);
            view.setImageDrawable(drawable);
            holder.setContainer(new CacheImageContainer(task));
            task.execute(position);
        }
    }

    @Override
    public int total() {
        return Integer.MAX_VALUE;
    }

    @Override
    public void acquireCount() {
        eventListener.countAcquired();
    }

    @Override
    public void onAcquireCount(LoadListener eventListener) {
        this.eventListener = eventListener;
    }
}
