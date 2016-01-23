package com.hengtan.nanodegreeapp.stocount;

import android.support.wearable.view.WearableListView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class ProductItemAdapter extends WearableListView.Adapter {

    private List<MainActivity.ProductItem> mData;

    private static class ItemViewHolder extends WearableListView.ViewHolder {
        TextView text;
        ImageView thumbnail;

        public ItemViewHolder(View itemView) {
            super(itemView);
            text = (TextView) itemView.findViewById(R.id.name);
            thumbnail = (ImageView) itemView.findViewById(R.id.thumbnail);
        }
    }

    public ProductItemAdapter(List<MainActivity.ProductItem> demoItems) {
        mData = demoItems;
    }

    public void swapItem(List<MainActivity.ProductItem> productItems)
    {
        mData = productItems;

    }
    @Override
    public WearableListView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        return new ItemViewHolder(LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.view_product_item_layout, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(WearableListView.ViewHolder viewHolder, int position) {
        // Get item
        MainActivity.ProductItem item = mData.get(position);

        // Update TextView
        ItemViewHolder itemViewHolder = (ItemViewHolder) viewHolder;
        itemViewHolder.text.setText(item.getName());

        if(item.getThumbnail() != null)
        {
            itemViewHolder.thumbnail.setImageBitmap(item.getThumbnail());
        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }
}
