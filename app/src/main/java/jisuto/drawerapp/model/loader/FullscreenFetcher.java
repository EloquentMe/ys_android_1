package jisuto.drawerapp.model.loader;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;


import jisuto.drawerapp.utils.ImageScaler;
import jisuto.drawerapp.utils.ImageSource;
import jisuto.drawerapp.utils.SingletonCarrier;

public class FullscreenFetcher {


    private class FullscreenTask extends BitmapWorkerTask<Object> {

        public FullscreenTask(ImageView imageView) {
            super(imageView);
        }

        @Override
        protected Bitmap doInBackground(Object... params) {
            try {
                Integer i = (Integer) params[0];
                return loader.getBitmap(i);
            } catch (IOException e) {
                Log.e("DrawerApp", "Bad file", e);
                Resources res = SingletonCarrier.getInstance().getContext().getResources();
                return ImageScaler.getPlaceholder(res);
            }
        }
    }

    private ImageLoader loader;

    public FullscreenFetcher(ImageSource source) {
        SingletonCarrier carrier = SingletonCarrier.getInstance();
        switch (source) {
            case CACHE:
                loader = carrier.getCacheImageLoader();
                break;
            case GALLERY:
                loader = carrier.getGalleryImageLoader();
                break;
            case INTERNET:
                loader = carrier.getInternetImageLoader();
                break;
        }

    }

    public void fetchImage(Integer position, ImageView victim, TextView info) {
        Resources res = SingletonCarrier.getInstance().getContext().getResources();
        BitmapWorkerTask<Object> task = new FullscreenTask(victim);
        BitmapDrawable drawable = new AsyncDrawable(res, task);
        victim.setImageDrawable(drawable);
        String title = loader.getTitle(position);
        String author = loader.getAuthor(position);
        if (title != null && author != null) {
            info.setText("Author: " + author + "\nTitle: " + title);
        }

        task.execute(position);
    }

    public int total() {
        return loader.total();
    }
}
