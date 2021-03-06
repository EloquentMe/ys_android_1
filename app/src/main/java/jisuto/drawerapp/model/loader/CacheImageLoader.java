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
import jisuto.drawerapp.utils.ImageSource;
import jisuto.drawerapp.utils.LoadListener;

import jisuto.drawerapp.utils.SingletonCarrier;

public class CacheImageLoader implements ImageLoader {

    public static final ImageSource LABEL = ImageSource.CACHE;

    class CacheImageContainer implements ImageHolder.ImageContainer {

        CacheWorker task;

        CacheImageContainer(CacheWorker task) {
            this.task = task;
        }

        @Override
        public void cancelRequest() {
            if (task != null) {
                task.cancel(true);
            }
        }

        @Override
        public ImageSource getSource() {
            return ImageSource.CACHE;
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
            cache.putBitmap(LABEL.name() + pos, pic);
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

    private transient com.android.volley.toolbox.ImageLoader.ImageCache cache;
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
        cache = SingletonCarrier.getInstance().getCommonCache();
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        cache = SingletonCarrier.getInstance().getCommonCache();
    }

    @Override
    public void setHolderContent(int position, ImageHolder holder) {
        ImageView view = holder.getImage();
        //"Local" label for distinction between different sources
        Bitmap pic = cache.getBitmap(LABEL.name() + (position % allItems.length));
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
            holder.setContainer(new CacheImageContainer(null));
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
    public Bitmap getBitmap(int position) throws IOException {
        int resId = position % allItems.length;
        String cacheKey = LABEL.name() + "_big_" + resId;
        Bitmap b = cache.getBitmap(cacheKey);
        if (b == null) {
            Resources res = SingletonCarrier.getInstance().getContext().getResources();
            b = ImageScaler.decodeSampledBitmapFromResource(res, allItems[resId]);
            cache.putBitmap(cacheKey, b);
        }
        return b;
    }

    @Override
    public String getAuthor(int position) {
        return null;
    }

    @Override
    public String getTitle(int position) {
        return null;
    }
}
