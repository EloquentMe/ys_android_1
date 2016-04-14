package jisuto.drawerapp.model.loader;

import android.graphics.Bitmap;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;

import jisuto.drawerapp.model.ImageHolder;
import jisuto.drawerapp.utils.LoadListener;

public interface ImageLoader extends Serializable {

    void setHolderContent(int position, ImageHolder holder);

    int total();

    void acquireCount();

    void onAcquireCount(LoadListener eventListener);

    Bitmap getBitmap(Object id) throws IOException;
}
