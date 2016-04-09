package jisuto.drawerapp.tab;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import jisuto.drawerapp.R;
import jisuto.drawerapp.model.LocalItemObject;
import jisuto.drawerapp.model.LocalImageAdapter;


public class LocalImagesFragment extends Fragment {


    private int columnCount = 4;
    private GridLayoutManager lLayout;
    private Random r = new Random();

    public LocalImagesFragment() {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View x = inflater.inflate(R.layout.fragment_images, container, false);
        lLayout = new GridLayoutManager(getActivity().getApplicationContext(), columnCount);

        RecyclerView rView = (RecyclerView) x.findViewById(R.id.recycler_view);
        rView.setHasFixedSize(true);
        rView.setLayoutManager(lLayout);

        LocalImageAdapter rcAdapter = new LocalImageAdapter(getActivity().getApplicationContext());
        rView.setAdapter(rcAdapter);
        return x;
    }

    public static LocalImagesFragment newInstance(int cols) {
        LocalImagesFragment newFragment = new LocalImagesFragment();
        Bundle args = new Bundle();
        args.putInt("columnCount", cols);
        newFragment.setArguments(args);

        return newFragment;
    }

    public int getColumnCount() {
        return columnCount;
    }

    public void setColumnCount(int cols) {
        if (columnCount == cols)
            return;

        android.support.v4.app.FragmentTransaction ft = getFragmentManager().beginTransaction();
        LocalImagesFragment newFragment = newInstance(cols);
        ft.replace(R.id.tabs, newFragment); //tabs???
        ft.commit();
    }


}
