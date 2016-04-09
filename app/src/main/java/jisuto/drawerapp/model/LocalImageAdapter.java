package jisuto.drawerapp.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import jisuto.drawerapp.R;
import jisuto.drawerapp.RecyclerViewHolder;
import jisuto.drawerapp.utils.ImageScaler;

public class LocalImageAdapter extends RecyclerView.Adapter<RecyclerViewHolder> {

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

    private List<WeakReference<Bitmap>> itemList;
    private Context context;

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

    public LocalImageAdapter(Context context) {
        this.context = context;
        itemList = new ArrayList<>(allItems.length);

        WeakReference<Bitmap> emptyRef = new WeakReference<>(null);
        for(int i = 0; i < allItems.length; i++) {
            itemList.add(emptyRef);
        }
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_list, null);
        RecyclerViewHolder rcv = new RecyclerViewHolder(layoutView);
        return rcv;
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {
        ImageView view = holder.imageView;
        boolean cancelled = potentialCancel(position, view);
        if (cancelled) {
            BitmapWorkerTask task = new BitmapWorkerTask(view);
            AsyncDrawable drawable = new AsyncDrawable(context.getResources(), task);
            view.setImageDrawable(drawable);
            task.execute(position);
        }
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

    @Override
    public int getItemCount() {
        return Integer.MAX_VALUE;
    }

    class BitmapWorkerTask extends AsyncTask<Integer, Void, Bitmap> {
        private final WeakReference<ImageView> imageViewReference;
        public int position;

        public BitmapWorkerTask(ImageView imageView) {
            // Use a WeakReference to ensure the ImageView can be garbage collected
            imageViewReference = new WeakReference<>(imageView);
        }

        // Decode image in background.
        @Override
        protected Bitmap doInBackground(Integer... params) {
            position = params[0] % 6;
            Bitmap pic = itemList.get(position).get();
            if (pic == null) {
                pic = ImageScaler.decodeSampledBitmapFromResource(context.getResources(),
                        allItems[position], 100, 100);
                itemList.set(position, new WeakReference<>(pic));
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
}
