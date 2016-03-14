package com.example.jisuto.drawerapp;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;


public class ImagesFragment extends Fragment {

    private GridLayoutManager lLayout;
    public ImagesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View x = inflater.inflate(R.layout.fragment_images, container, false);
        List<ItemObject> rowListItem = getAllItemList();
        lLayout = new GridLayoutManager(getActivity().getApplicationContext(), 4);

        RecyclerView rView = (RecyclerView) x.findViewById(R.id.recycler_view);
        rView.setHasFixedSize(true);
        rView.setLayoutManager(lLayout);

        RecyclerViewAdapter rcAdapter = new RecyclerViewAdapter(getActivity().getApplicationContext(), rowListItem);
        rView.setAdapter(rcAdapter);
        return x;
    }

    private List<ItemObject> getAllItemList(){

        List<ItemObject> allItems = new ArrayList<>();
        allItems.add(new ItemObject(R.drawable.thumb_1));
        allItems.add(new ItemObject(R.drawable.thumb_2));
        allItems.add(new ItemObject(R.drawable.thumb_3));
        allItems.add(new ItemObject(R.drawable.thumb_4));
        allItems.add(new ItemObject(R.drawable.thumb_5));
        allItems.add(new ItemObject(R.drawable.thumb_6));
        allItems.add(new ItemObject(R.drawable.thumb_1));
        allItems.add(new ItemObject(R.drawable.thumb_2));
        allItems.add(new ItemObject(R.drawable.thumb_3));
        allItems.add(new ItemObject(R.drawable.thumb_4));
        allItems.add(new ItemObject(R.drawable.thumb_5));
        allItems.add(new ItemObject(R.drawable.thumb_6));
        allItems.add(new ItemObject(R.drawable.thumb_1));
        allItems.add(new ItemObject(R.drawable.thumb_2));
        allItems.add(new ItemObject(R.drawable.thumb_3));
        allItems.add(new ItemObject(R.drawable.thumb_4));
        allItems.add(new ItemObject(R.drawable.thumb_5));
        allItems.add(new ItemObject(R.drawable.thumb_6));
        allItems.add(new ItemObject(R.drawable.thumb_1));
        allItems.add(new ItemObject(R.drawable.thumb_2));
        allItems.add(new ItemObject(R.drawable.thumb_3));
        allItems.add(new ItemObject(R.drawable.thumb_4));
        allItems.add(new ItemObject(R.drawable.thumb_5));
        allItems.add(new ItemObject(R.drawable.thumb_6));
        allItems.add(new ItemObject(R.drawable.thumb_1));
        allItems.add(new ItemObject(R.drawable.thumb_2));
        allItems.add(new ItemObject(R.drawable.thumb_3));
        allItems.add(new ItemObject(R.drawable.thumb_4));
        allItems.add(new ItemObject(R.drawable.thumb_5));
        allItems.add(new ItemObject(R.drawable.thumb_6));

        return allItems;
    }

}
