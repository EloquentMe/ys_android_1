package jisuto.drawerapp.model.loader;

import android.content.AsyncTaskLoader;
import android.content.ContentResolver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import jisuto.drawerapp.model.ImageHolder;
import jisuto.drawerapp.utils.LoadListener;
import jisuto.drawerapp.utils.SingletonCarrier;

/**
 * Created by jisuto on 4/10/16.
 */
public class GalleryImageLoader implements ImageLoader {

    private class GalleryThinContainer implements ImageHolder.ImageContainer, Serializable {

        long imageId;
        //Bitmap thumb;

        @Override
        public void cancelRequest() { }
    }

    private List<GalleryThinContainer> itemList;
    private transient BitmapFactory.Options thumbOptions;


    @Override
    public void setHolderContent(int position, ImageHolder holder) {
        ContentResolver resolver = SingletonCarrier.getInstance().getContentResolver();
        GalleryThinContainer container = itemList.get(position);
        Bitmap thumb = MediaStore.Images.Thumbnails.getThumbnail(resolver, container.imageId, MediaStore.Images.Thumbnails.MICRO_KIND
        , thumbOptions);
        holder.setContainer(container);
        holder.getImage().setImageBitmap(thumb);
    }

    @Override
    public int total() {
        return itemList.size();
    }

    public GalleryImageLoader() {
        itemList = new ArrayList<>();
        thumbOptions = new BitmapFactory.Options();
    }

    @Override
    public void acquireCount() {
        String[] projection = {MediaStore.Images.Media._ID};
        ContentResolver resolver = SingletonCarrier.getInstance().getContentResolver();
        Cursor galleryCursor =
                resolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        projection, null, null, null);

        while(galleryCursor.moveToNext()) {
            GalleryThinContainer container = new GalleryThinContainer();
            long imgId = galleryCursor.getLong(0);
            container.imageId = imgId;
            itemList.add(container);
        }
        galleryCursor.close();
    }

    @Override
    public void onAcquireCount(LoadListener eventListener) {
        eventListener.countAcquired();
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();

        thumbOptions = new BitmapFactory.Options();
    }
}
