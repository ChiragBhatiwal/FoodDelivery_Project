package com.example.myapplication.Ui.Fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.myapplication.Data.Model.ModelForItemsInCart;
import com.example.myapplication.Ui.Adapters.RecyclerViewAdapterForCartFragment;
import com.example.myapplication.Utility.Constant;
import com.example.myapplication.R;
import com.example.myapplication.Ui.Screens.OrderPlaceScreen;
import com.example.myapplication.Utility.Sockets;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CartFragment extends Fragment implements RecyclerViewAdapterForCartFragment.OnItemClickListener {
    private ArrayList<ModelForItemsInCart> arrayList;
    private RecyclerViewAdapterForCartFragment recyclerViewAdapterForCartFragment;

    public CartFragment() {
        // Required empty public constructor
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cart, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView_CartFragment);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        arrayList = new ArrayList<>();
        recyclerViewAdapterForCartFragment = new RecyclerViewAdapterForCartFragment(getContext(), arrayList, this);
        recyclerView.setAdapter(recyclerViewAdapterForCartFragment);
        recyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("Login", Context.MODE_PRIVATE);
        String token = sharedPreferences.getString("refreshToken", "");
        String userId = sharedPreferences.getString("userId", "");
        getCartProducts(token,userId);
        return view;
    }

   void getCartProducts(String token,String userId){
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,Constant.base_IP +"/cart/finditems/"+userId,null, new Response.Listener<JSONObject>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(JSONObject response) {
                try {

                    JSONArray findItemsArray = response.getJSONArray("finditems");

                    if (findItemsArray.length() > 0) {
                        JSONObject findItemsObject = findItemsArray.getJSONObject(0);
                        JSONArray itemsArray = findItemsObject.getJSONArray("items");

                        for (int i = 0; i < itemsArray.length(); i++) {
                            JSONObject item = itemsArray.getJSONObject(i);

                            ModelForItemsInCart modelForItemsInCart = new ModelForItemsInCart();
                            modelForItemsInCart.set_id(item.getString("_id"));
                            modelForItemsInCart.setItemName(item.getString("itemName"));
                            modelForItemsInCart.setItemPrice(item.getString("itemPrice"));
                            modelForItemsInCart.setItemDescription(item.getString("itemDescription"));
                            modelForItemsInCart.setRestaurantId(item.getString("restaurantId"));
                            modelForItemsInCart.setItemImage(item.getString("itemImage"));

                            arrayList.add(modelForItemsInCart);
                        }


                        recyclerViewAdapterForCartFragment.notifyDataSetChanged();
                    } else {
                        Toast.makeText(getContext(), "No items found", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), "Parsing error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Errors", String.valueOf(error));
            }
      })
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("Authorization",token);
                return params;
            }
        };
        requestQueue.add(jsonObjectRequest);
   }


    @Override
    public void onCardCLicked(int position,String itemId,String restaurantId) {
       Intent intent = new Intent(getContext(), OrderPlaceScreen.class);
       Bundle bundle = new Bundle();
       bundle.putString("ItemId",itemId);
       bundle.putString("RestaurantId",restaurantId);
//       Log.d("Id are as",""+cartId+" "+restaurantId);
       intent.putExtras(bundle);
       startActivity(intent);
    }
}
