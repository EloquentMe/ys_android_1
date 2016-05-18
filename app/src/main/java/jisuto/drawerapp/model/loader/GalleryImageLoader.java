package jisuto.drawerapp.model.loader;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import jisuto.drawerapp.model.ImageHolder;
import jisuto.drawerapp.utils.ImageScaler;
import jisuto.drawerapp.utils.ImageSource;
import jisuto.drawerapp.utils.LoadListener;
import jisuto.drawerapp.utils.SingletonCarrier;

public class GalleryImageLoader implements ImageLoader {

    public static final String LABEL = "Gallery";

    private static ImageHolder.ImageContainer CACHED_DATA_CONTAINER = new ImageHolder.ImageContainer() {
        public void cancelRequest() {}
        public ImageSource getSource() {
            return ImageSource.GALLERY;
        }
    };

    private class GalleryContainer implements ImageHolder.ImageContainer, Serializable {
        GalleryTask task;

        GalleryContainer(GalleryTask task) {
            this.task = task;
        }

        /*
         * cancel(false) because we're dealing with android services and not simple computation
         */
        @Override
        public void cancelRequest() {
            task.cancel(false);
        }

        @Override
        public ImageSource getSource() {
            return ImageSource.GALLERY;
        }
    }

    private class GalleryTask extends BitmapWorkerTask<Long> {

        public GalleryTask(ImageView imageView) {
            super(imageView);
        }

        @Override
        protected Bitmap doInBackground(Long... params) {
            Long imgId = params[0];
            ContentResolver resolver = SingletonCarrier.getInstance().getContentResolver();
            Bitmap pic = MediaStore.Images.Thumbnails.getThumbnail(resolver, imgId, MediaStore.Images.Thumbnails.MICRO_KIND
                    , thumbOptions);
            if (pic == null) { // OOM / bad id
                pic = ImageScaler.getPlaceholder(SingletonCarrier.getInstance().getContext().getResources());
            }
            thumbCache.putBitmap(LABEL + imgId, pic);
            return pic;
        }
    }

    private List<Long> itemList;
    private transient com.android.volley.toolbox.ImageLoader.ImageCache thumbCache;
    private transient BitmapFactory.Options thumbOptions;


    @Override
    public void setHolderContent(int position, ImageHolder holder) {
        final long imgId = itemList.get(position);
        Bitmap pic = thumbCache.getBitmap(LABEL + imgId);
        if (pic == null) {
            GalleryTask task = new GalleryTask(holder.getImage());
            Context context = SingletonCarrier.getInstance().getContext();
            AsyncDrawable drawable = new AsyncDrawable(context.getResources(), task);
            holder.getImage().setImageDrawable(drawable);
            holder.setContainer(new GalleryContainer(task));
            task.execute(imgId);
        } else {
            holder.setContainer(CACHED_DATA_CONTAINER);
            holder.getImage().setImageBitmap(pic);
        }
    }

    @Override
    public int total() {
        return itemList.size();
    }

    public GalleryImageLoader() {
        itemList = new ArrayList<>();
        thumbOptions = new BitmapFactory.Options();
        thumbCache = SingletonCarrier.getInstance().getCommonCache();
    }

    @Override
    public void acquireCount() {
        itemList = new ArrayList<>();
        String[] projection = {MediaStore.Images.Media._ID};
        ContentResolver resolver = SingletonCarrier.getInstance().getContentResolver();
        Cursor galleryCursor =
                resolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        projection, null, null, null);

        while (galleryCursor.moveToNext()) {
            itemList.add(galleryCursor.getLong(0));
        }
        galleryCursor.close();
    }

    @Override
    public void onAcquireCount(LoadListener eventListener) {
        eventListener.countAcquired();
    }

    @Override
    public Bitmap getBitmap(Object id) throws IOException {
        Log.d("GalleryImageLoader", "Id: " + id);
        SingletonCarrier carrier = SingletonCarrier.getInstance();
        ContentResolver resolver = carrier.getContentResolver();
        String[] projection = {MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.DISPLAY_NAME};
        String where = MediaStore.Images.ImageColumns._ID.concat(" = ?");
        String[] args = new String[]{String.valueOf(itemList.get((int) id))};
        Cursor image = MediaStore.Images.Media.query(resolver, MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection,
                where, args, MediaStore.Images.ImageColumns._ID);

        if (image.moveToFirst()) {
            String path = image.getString(0);
            String name = image.getString(1);
            image.close();
            Log.d("GalleryImageLoader", "File: " + path);
            InputStream in = resolver.openInputStream(Uri.parse("file://" + path));
            Display display = ((WindowManager) carrier.getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            return ImageScaler.decodeSampledBitmapFromStream(in);
        } else {
            return null;
        }
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();

        thumbOptions = new BitmapFactory.Options();
        thumbCache = SingletonCarrier.getInstance().getCommonCache();
    }
}
