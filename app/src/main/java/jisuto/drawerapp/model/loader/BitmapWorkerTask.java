package jisuto.drawerapp.model.loader;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.lang.ref.SoftReference;

public abstract class BitmapWorkerTask<T> extends AsyncTask<T, Void, Bitmap> {
    protected final SoftReference<ImageView> imageViewReference;
    protected int position;

    public BitmapWorkerTask(ImageView imageView) {
        imageViewReference = new SoftReference<>(imageView);
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

    public static boolean potentialCancel(int position, ImageView view) {
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


}
