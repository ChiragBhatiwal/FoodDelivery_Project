package com.example.myapplication.Ui.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Data.Model.ModelForHomeFragment;
import com.example.myapplication.R;

import java.util.ArrayList;

public class RecyclerViewAdapterForHomeFragment extends RecyclerView.Adapter<RecyclerViewAdapterForHomeFragment.ViewHolder> {

    Context context;
    ArrayList<ModelForHomeFragment> arrayList;
    OnItemClickedListener onItemClickedListener;
    public RecyclerViewAdapterForHomeFragment(Context context, ArrayList<ModelForHomeFragment> arrayList, OnItemClickedListener onItemClickedListener){
        this.arrayList = arrayList;
        this.context = context;
        this.onItemClickedListener = onItemClickedListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.model_for_recyclerview_in_homefragment,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAdapterForHomeFragment.ViewHolder holder, int position) {
       holder.textView.setText(arrayList.get(position).getRestaurantName());
//        Log.d("image",arrayList.get(position).getImageOfRestaurant());
//        Picasso.get().load(arrayList.get(position).getImageOfRestaurant()).into(holder.imageView);
        holder.cardView.setOnClickListener(v -> {
            if(!(onItemClickedListener == null))
            {
                String restaurantId = arrayList.get(position).get_id();
                onItemClickedListener.onCardViewClicked(position,restaurantId);
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        ImageView imageView;
        CardView cardView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.restaurantName_HomeFragment);
            imageView = itemView.findViewById(R.id.imageView_restaurant_HomeFragment);
            cardView = itemView.findViewById(R.id.cardView_HomeFragment);
//            textView2 = itemView.findViewById(R.id.itemDesc_Recycler_HomeFragment);
        }
    }

    public interface OnItemClickedListener {
        void onCardViewClicked(int position , String restaurantId);
    }
}
