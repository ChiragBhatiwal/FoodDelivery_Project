package com.example.vendorapp.UI.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.vendorapp.Data.Models.MenuScreen_Model;
import com.example.vendorapp.R;

import java.util.List;

public class MenuRecyclerAdapter extends RecyclerView.Adapter<MenuRecyclerAdapter.ViewHolder> {


    List<MenuScreen_Model> arrayList;
    Context context;

    public MenuRecyclerAdapter(Context context)
    {
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.menu_screen_recycler_layout,parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.itemName.setText(arrayList.get(position).getItemName());
        holder.itemPrice.setText(arrayList.get(position).getItemPrice());
        holder.itemDescription.setText(arrayList.get(position).getItemDescription());

        Glide.with(context)
                .load(arrayList.get(position).getItemImage())
                .into(holder.itemImage);

    }

    @Override
    public int getItemCount() {
        return arrayList != null ? arrayList.size() : 0;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateItems(List<MenuScreen_Model> arrayList)
    {
        this.arrayList=arrayList;
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        TextView itemPrice,itemName,itemDescription;
        ImageView itemImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.itemName_MenuScreen);
            itemPrice = itemView.findViewById(R.id.itemPrice_MenuScreen);
            itemDescription = itemView.findViewById(R.id.itemDescription_MenuScreen);
            itemImage = itemView.findViewById(R.id.itemImage_MenuScreen);
        }
    }
}
