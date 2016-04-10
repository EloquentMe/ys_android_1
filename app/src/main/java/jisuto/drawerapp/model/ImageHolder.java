package jisuto.drawerapp.model;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;

import java.io.Serializable;

import jisuto.drawerapp.R;

public class ImageHolder extends RecyclerView.ViewHolder implements ImageLoader.ImageListener, Serializable {


    public static interface ImageContainer {
        public abstract void cancelRequest();
    }

    private final ImageView image;

    ImageContainer container;

    public ImageHolder(View itemView) {
        super(itemView);
        image = (ImageView) itemView.findViewById(R.id.image_view);
    }

    public void setContainer(ImageContainer container) {
        this.container = container;
    }

    public ImageView getImage() {
        return image;
    }

    @Override
    public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
        image.setImageBitmap(response.getBitmap());
    }

    @Override
    public void onErrorResponse(VolleyError error) {
    }
}
