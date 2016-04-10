package jisuto.drawerapp.model.loader;

import android.os.Parcelable;

import java.io.Serializable;
import java.util.EventListener;

import jisuto.drawerapp.model.ImageHolder;
import jisuto.drawerapp.utils.LoadListener;

public interface ImageLoader extends Serializable {

    void get(int position, ImageHolder holder);

    int total();

    void acquireCount();

    void onAcquireCount(LoadListener eventListener);
}
