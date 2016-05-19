package jisuto.drawerapp.model;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;

import java.io.Serializable;

import jisuto.drawerapp.FullscreenActivity;
import jisuto.drawerapp.R;
import jisuto.drawerapp.utils.ImageSource;
import jisuto.drawerapp.utils.SingletonCarrier;

public class ImageHolder extends RecyclerView.ViewHolder implements ImageLoader.ImageListener,
        Serializable,
        View.OnClickListener {

    public interface ImageContainer extends Serializable {
        void cancelRequest();
        ImageSource getSource();
    }

    private final ImageView image;
    ImageContainer container;
    boolean newApi = true;

    public ImageHolder(View itemView) {
        super(itemView);
        image = (ImageView) itemView.findViewById(R.id.image_view);
        image.setOnClickListener(this);
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

    @Override
    public void onClick(View v) {
        /*Bitmap pic = container.getFullSizeBitmap(getAdapterPosition());
        if (pic != null) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            pic.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();
            */Context context = SingletonCarrier.getInstance().getContext();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && newApi) {
            Log.i("DrawerApp", "ImageHolder: New API.");
            Intent subActivity = new Intent(context,
                    FullscreenActivity.class);
            subActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            subActivity.putExtra("source", container.getSource());
            subActivity.putExtra("position", getAdapterPosition());
            context.startActivity(subActivity);/*,
                        ActivityOptions.makeSceneTransitionAnimation((Activity) v.getContext(),
                                v.findViewById(R.id.image_view), "image").toBundle());*/
        } else {
            Log.i("DrawerApp", "ImageHolder: Old API.");
            int[] screenLocation = new int[2];
            v.getLocationOnScreen(screenLocation);

            Intent subActivity = new Intent(context,
                    FullscreenActivity.class);
            //subActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            subActivity.putExtra("source", container.getSource());
            subActivity.putExtra("position", getAdapterPosition());
            Log.d("BadApi", "subAct");
            context.startActivity(subActivity);
            Log.d("BadApi", "SubAct Created");
            // Override transitions: we don't want the normal window animation in addition
            // to our custom one
            ((Activity) context).overridePendingTransition(0, 0);
        }
        //}
    }
}
