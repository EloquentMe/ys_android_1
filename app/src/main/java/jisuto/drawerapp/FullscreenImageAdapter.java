package jisuto.drawerapp;

import android.app.Activity;
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import jisuto.drawerapp.model.loader.FullscreenFetcher;
import jisuto.drawerapp.utils.ImageSource;

public class FullscreenImageAdapter extends PagerAdapter {
    private FullscreenActivity _activity;
    private LayoutInflater inflater;
    private FullscreenFetcher fetcher;

    // constructor
    public FullscreenImageAdapter(AppCompatActivity activity, ImageSource source) {
        this._activity = (FullscreenActivity) activity;
        fetcher = new FullscreenFetcher(source);

    }
    @Override
    public int getCount() {
        return fetcher.total();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        inflater = (LayoutInflater) _activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View viewLayout = inflater.inflate(R.layout.layout_fullscreen_image, container, false);
        View touchImageView = viewLayout.findViewById(R.id.touch_image_view);
        touchImageView.setOnClickListener(_activity);
        View infoTextView = viewLayout.findViewById(R.id.info_text_view);
        fetcher.fetchImage(position, (ImageView) touchImageView, (TextView) infoTextView);

        container.addView(viewLayout);
        return viewLayout;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((RelativeLayout) object);
    }
}
