package jisuto.drawerapp.tab;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import jisuto.drawerapp.R;
import jisuto.drawerapp.model.ImageAdapter;
import jisuto.drawerapp.model.loader.ImageLoader;
import jisuto.drawerapp.utils.SingletonCarrier;


public class ImagesFragment extends Fragment {

    private int columnCount = SingletonCarrier.getInstance().getColumnCount();
    private GridLayoutManager lLayout;
    private RecyclerView rView;
    private ImageLoader loader;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View x = inflater.inflate(R.layout.fragment_images, container, false);
        lLayout = new GridLayoutManager(getActivity().getApplicationContext(), columnCount);
        loader = (ImageLoader) getArguments().getSerializable("loader");

        rView = (RecyclerView) x.findViewById(R.id.recycler_view);
        rView.setHasFixedSize(true);
        rView.setLayoutManager(lLayout);

        ImageAdapter adapter = new ImageAdapter(loader);
        rView.setAdapter(adapter);
        return x;
    }

    public static ImagesFragment newInstance(ImageLoader loader) {
        ImagesFragment newFragment = new ImagesFragment();
        Bundle args = new Bundle();
        args.putSerializable("loader", loader);
        newFragment.setArguments(args);

        return newFragment;
    }

}
