package com.example.myapplication.Ui.Adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;



import com.example.myapplication.Data.Model.ModelForRestaurantInSearchScreen;
import com.example.myapplication.R;
import com.example.myapplication.Ui.Screens.RestaurantViewScreen;

import java.util.ArrayList;

public class RecyclerViewAdapterForSearchFragment extends RecyclerView.Adapter<RecyclerViewAdapterForSearchFragment.ViewHolder> {
    Context context;
    ArrayList<ModelForRestaurantInSearchScreen> arrayList;

    public RecyclerViewAdapterForSearchFragment(Context context, ArrayList<ModelForRestaurantInSearchScreen> arrayList){
        this.context=context;
        this.arrayList=arrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.model_layout_for_searchfragment,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ModelForRestaurantInSearchScreen restaurant = arrayList.get(position);
        RecyclerViewAdapterForItemInSearchView recyclerViewAdapterForItemInSearchView = new RecyclerViewAdapterForItemInSearchView(context, restaurant.getRestaurantWithItems(), new RecyclerViewAdapterForItemInSearchView.OnItemClicked() {
            @Override
            public void onButtonClicked(int position) {
                Intent intent = new Intent(context, RestaurantViewScreen.class);
                Bundle bundle = new Bundle();
                bundle.putString("_id",restaurant.getId());
                intent.putExtras(bundle);
                context.startActivity(intent);
            }
        });
        holder.recyclerView.setLayoutManager(new LinearLayoutManager(context));
        holder.recyclerView.setAdapter(recyclerViewAdapterForItemInSearchView);
        holder.nameRestaurant.setText(arrayList.get(position).getRestaurantName());
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        RecyclerView recyclerView;
        TextView nameRestaurant;
        ImageView imageView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardView_SearchFragment);
            recyclerView = itemView.findViewById(R.id.item_recycler_SearchScreen);
            nameRestaurant = itemView.findViewById(R.id.restaurant_Name_SearchScreen);
            imageView = itemView.findViewById(R.id.image_item_SearchView);
        }
    }
}
