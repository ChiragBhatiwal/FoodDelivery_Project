package com.example.myapplication.Ui.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Data.Model.RestaurantItem;
import com.example.myapplication.R;

import java.util.ArrayList;

public class RecyclerViewAdapterForRestaurantScreen  extends RecyclerView.Adapter<RecyclerViewAdapterForRestaurantScreen.ViewHolder>{
    Context context;
    ArrayList<RestaurantItem> arrayList;
    OnItemClickListener onItemClickListener;
    public RecyclerViewAdapterForRestaurantScreen(Context context, ArrayList<RestaurantItem> arrayList, OnItemClickListener onItemClickListener){
        this.context=context;
        this.arrayList=arrayList;
        this.onItemClickListener = onItemClickListener;
    };

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.model_for_items_in_restaurantscreen,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
          holder.name.setText(arrayList.get(position).getItemName());
          holder.description.setText(arrayList.get(position).getItemDescription());
          holder.price.setText(arrayList.get(position).getItemPrice());
          holder.button.setOnClickListener(v -> {
              if(!(onItemClickListener == null))
              {
                  String itemId = arrayList.get(position).get_id();
                  onItemClickListener.onAddToBagClick(position,itemId);
              }
          });
          holder.cardView.setOnClickListener(v -> {
              if (!(onItemClickListener == null))
              {
                  onItemClickListener.onGoToRestaurantClicked(position);
              }
          });
    }


    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView name,description,price;
        Button button;
        CardView cardView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardView_For_Items_In_RestaurantScreen);
            name = itemView.findViewById(R.id.name_items_RestaurantScreen);
            description = itemView.findViewById(R.id.Description_items_RestaurantScreen);
            button = itemView.findViewById(R.id.addToCart_button_RestaurantScreen);
            price = itemView.findViewById(R.id.price_items_RestaurantScreen);

        }
        }
        public interface OnItemClickListener {
            void onAddToBagClick(int position,String itemId);
            void onGoToRestaurantClicked(int position);
        }

}
