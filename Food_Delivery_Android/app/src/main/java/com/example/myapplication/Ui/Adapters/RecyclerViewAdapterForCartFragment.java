package com.example.myapplication.Ui.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;


import com.example.myapplication.Data.Model.ModelForItemsInCart;
import com.example.myapplication.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class RecyclerViewAdapterForCartFragment extends RecyclerView.Adapter<RecyclerViewAdapterForCartFragment.ViewHolder> {

    Context context;
    ArrayList<ModelForItemsInCart> arrayList;
    OnItemClickListener onItemClickListener;

    public RecyclerViewAdapterForCartFragment(Context context, ArrayList<ModelForItemsInCart> arrayList, OnItemClickListener onItemClickListener){
        this.context = context;
        this.arrayList = arrayList;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.model_layout_for_cart_fragment,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.name.setText(arrayList.get(position).getItemName());
        holder.price.setText(arrayList.get(position).getItemPrice());
        holder.desc.setText(arrayList.get(position).getItemDescription());
        String imageUrl = arrayList.get(position).getItemImage();
        Picasso.get().load(imageUrl).into(holder.imageView);
        holder.button.setOnClickListener(v -> {
            if (onItemClickListener != null)
            {
                onItemClickListener.onCardCLicked(position,arrayList.get(position).get_id(),arrayList.get(position).getRestaurantId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
         TextView name,desc,price;
         ImageView imageView;
         Button button;
         CardView cardView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.nameOfItem_CartFragment);
            desc = itemView.findViewById(R.id.itemDescription_CartFragment);
            price = itemView.findViewById(R.id.priceOfItem_CartFragment);
            button = itemView.findViewById(R.id.proceedButton_CartFragment);
            imageView = itemView.findViewById(R.id.imageOfItem_CartFragment);
        }
    }
    public interface OnItemClickListener{
        void onCardCLicked(int position,String cartId,String restaurantId);
    }
}
