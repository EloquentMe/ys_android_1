package jisuto.drawerapp.model;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.Serializable;

import jisuto.drawerapp.R;
import jisuto.drawerapp.utils.LoadListener;

public class ImageAdapter extends RecyclerView.Adapter<ImageHolder> implements Serializable {

    private jisuto.drawerapp.model.loader.ImageLoader loader;

    public ImageAdapter(jisuto.drawerapp.model.loader.ImageLoader loader) {
        this.loader = loader;
        loader.onAcquireCount(new LoadListener() {
            @Override
            public void countAcquired() {
                notifyDataSetChanged();
            }
        });
        loader.acquireCount();
    }

    @Override
    public ImageHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view, null);
        ImageHolder rcv = new ImageHolder(layoutView);
        return rcv;
    }

    @Override
    public void onViewRecycled(ImageHolder holder) {
        super.onViewRecycled(holder);
        holder.container.cancelRequest();
    }

    @Override
    public void onBindViewHolder(final ImageHolder holder, int position) {
        loader.setHolderContent(position, holder);
    }

    @Override
    public int getItemCount() {
        return loader.total();
    }
}