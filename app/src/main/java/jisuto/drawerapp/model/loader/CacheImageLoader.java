package jisuto.drawerapp.model.loader;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.widget.ImageView;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Random;

import jisuto.drawerapp.R;
import jisuto.drawerapp.model.ImageHolder;
import jisuto.drawerapp.utils.ImageScaler;
import jisuto.drawerapp.utils.LoadListener;

import jisuto.drawerapp.utils.SingletonCarrier;

public class CacheImageLoader implements ImageLoader {

    public static final String LABEL = "Local";

    class CacheImageContainer implements ImageHolder.ImageContainer {

        CacheWorker task;

        CacheImageContainer(CacheWorker task) {
            this.task = task;
        }

        @Override
        public void cancelRequest() {
            task.cancel(true);
        }
    }

    class CacheWorker extends BitmapWorkerTask<Integer> {

        public CacheWorker(ImageView imageView) {
            super(imageView);
        }

        // Decode image in background.
        @Override
        protected Bitmap doInBackground(Integer... params) {
            this.position = params[0];
            int pos = position % allItems.length;
            Context context = SingletonCarrier.getInstance().getContext();
            Bitmap pic = ImageScaler.decodeSampledBitmapFromResource(context.getResources(), allItems[pos]
                    , 100, 100);
            thumbCache.putBitmap(LABEL + pos, pic);
            return pic;
        }

    }

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

    private transient com.android.volley.toolbox.ImageLoader.ImageCache thumbCache;
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

    public CacheImageLoader() {
        thumbCache = SingletonCarrier.getInstance().getCommonCache();
    }

    private final void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        thumbCache = SingletonCarrier.getInstance().getCommonCache();
    }

    @Override
    public void setHolderContent(int position, ImageHolder holder) {
        ImageView view = holder.getImage();
        //"Local" label for distinction between different sources
        Bitmap pic = thumbCache.getBitmap(LABEL + (position % allItems.length));
        if (pic == null) {
            boolean cancelled = BitmapWorkerTask.potentialCancel(position, view);
            if (cancelled) {
                CacheWorker task = new CacheWorker(view);
                Context context = SingletonCarrier.getInstance().getContext();
                AsyncDrawable drawable = new AsyncDrawable(context.getResources(), task);
                view.setImageDrawable(drawable);
                holder.setContainer(new CacheImageContainer(task));
                task.execute(position);
            }
        } else {
            view.setImageBitmap(pic);
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

    @Override
    public Bitmap getBitmap(Object id) throws IOException {
        int resId = ((Integer) id ) % allItems.length;
        Resources res = SingletonCarrier.getInstance().getContext().getResources();
        return ImageScaler.decodeSampledBitmapFromResource(res, resId);
    }
}
