package jisuto.drawerapp.model;

import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageContainer;

import java.util.List;

import jisuto.drawerapp.R;
import jisuto.drawerapp.utils.SingletonCarrier;

public class InternetImageAdapter extends RecyclerView.Adapter<InternetImageAdapter.ImageHolder> {

    class ImageHolder extends RecyclerView.ViewHolder {

        public final ImageView image;

        public ImageContainer container;

        public ImageHolder(View itemView) {
            super(itemView);
            image = (ImageView)itemView.findViewById(R.id.image_view);
        }
    }

    private final List<String> mUrls;

    ImageLoader loader = SingletonCarrier.getInstance().getImageLoader();

    Handler mMainHandler = new Handler(Looper.getMainLooper());

    public InternetImageAdapter(List<String> urls) {
        mUrls = urls;
    }

    @Override
    public ImageHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_list, null);
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
        holder.container = loader.get(mUrls.get(position), new com.android.volley.toolbox.ImageLoader.ImageListener() {
            @Override
            public void onResponse(ImageContainer response, boolean isImmediate) {
                holder.image.setImageBitmap(response.getBitmap());
            }

            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return mUrls.size();
    }
}