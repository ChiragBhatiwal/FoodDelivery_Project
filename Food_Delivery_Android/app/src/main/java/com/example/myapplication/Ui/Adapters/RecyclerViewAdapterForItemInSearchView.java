package com.example.myapplication.Ui.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.myapplication.Data.Model.ModelForItemInSearchScreen;
import com.example.myapplication.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class RecyclerViewAdapterForItemInSearchView extends RecyclerView.Adapter<RecyclerViewAdapterForItemInSearchView.ViewHolder> {
    Context context;
    List<ModelForItemInSearchScreen> item;
    OnItemClicked onItemClicked;
    public RecyclerViewAdapterForItemInSearchView(Context context, List<ModelForItemInSearchScreen> item, OnItemClicked onItemClicked) {
        this.context = context;
        this.item = item;
        this.onItemClicked = onItemClicked;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.model_layout_for_items_of_search_screen_view, parent, false);
        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ModelForItemInSearchScreen searchResponse = item.get(position);
        holder.itemName.setText(item.get(position).getItemName());
        holder.itemPrice.setText(item.get(position).getItemPrice());
        holder.itemDescription.setText(item.get(position).getItemDescription());
        String imageUrl = searchResponse.getItemImages();
        Picasso.get()
                .load(imageUrl)
                .into(holder.imageView);
        holder.addToBag.setOnClickListener(v -> {
            if(onItemClicked != null)
            {
                onItemClicked.onButtonClicked(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return item.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView itemName,itemPrice,itemDescription;
        Button addToBag;
        ImageView imageView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.name_items_SearchView);
            itemPrice = itemView.findViewById(R.id.price_items_SearchView);
            itemDescription = itemView.findViewById(R.id.Description_items_SearchView);
            imageView = itemView.findViewById(R.id.image_item_SearchView);
            addToBag = itemView.findViewById(R.id.addToCart_button_SearchView);
        }
    }
    public interface OnItemClicked{
        void onButtonClicked(int position);
    }
}
