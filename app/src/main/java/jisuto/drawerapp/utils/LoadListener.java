package jisuto.drawerapp.utils;

import java.io.Serializable;
import java.util.EventListener;

public interface LoadListener extends EventListener, Serializable {
    void countAcquired();
}
