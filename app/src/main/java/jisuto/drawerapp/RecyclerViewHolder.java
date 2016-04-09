package jisuto.drawerapp;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

public class RecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public ImageView imageView;

    public RecyclerViewHolder(View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);
        imageView = (ImageView) itemView.findViewById(R.id.image_view);
    }

    @Override
    public void onClick(View view) {
        Toast.makeText(view.getContext(), "Clicked Position = " + getAdapterPosition(), Toast.LENGTH_SHORT).show();
    }
}
