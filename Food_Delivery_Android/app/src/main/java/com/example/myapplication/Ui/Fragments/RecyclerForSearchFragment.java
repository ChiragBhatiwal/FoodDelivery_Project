package com.example.myapplication.Ui.Fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.myapplication.Ui.Adapters.RecyclerViewAdapterForSearchFragment;
import com.example.myapplication.Data.Model.ModelForRestaurantInSearchScreen;
import com.example.myapplication.Data.Model.ModelForItemInSearchScreen;
import com.example.myapplication.Utility.Constant;
import com.example.myapplication.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class RecyclerForSearchFragment extends Fragment {
    String value;
    ArrayList<ModelForRestaurantInSearchScreen> arrayList;
    RecyclerViewAdapterForSearchFragment recyclerViewAdapterForSearchFragment;

    public RecyclerForSearchFragment() {
        // Required empty public constructor
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        value = getArguments().getString("Value");
        RecyclerView recyclerView;
        View view =  inflater.inflate(R.layout.fragment_recycler_for_search, container, false);
        arrayList = new ArrayList<>();
        recyclerView = view.findViewById(R.id.recyclerView_searchFragment);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        try {
            getItems();
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        recyclerViewAdapterForSearchFragment = new RecyclerViewAdapterForSearchFragment(getContext(),arrayList);
        recyclerView.setAdapter(recyclerViewAdapterForSearchFragment);
        return view;


    }
    void getItems() throws JSONException {
        RequestQueue requestQueue = Volley.newRequestQueue(requireContext());

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("value",value);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Constant.base_IP +"/item/find", jsonObject, new Response.Listener<JSONObject>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(JSONObject response) {
                Log.d("Resposne is", String.valueOf(response));
                try {
                    JSONArray jsonArray = response.getJSONArray("searchInItems");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                        ModelForRestaurantInSearchScreen restaurant = new ModelForRestaurantInSearchScreen();
                        restaurant.setRestaurantName(jsonObject1.getString("restaurantName"));
                        restaurant.setUserId(jsonObject1.getString("userId"));
                        restaurant.setId(jsonObject1.getString("_id"));
                        restaurant.setRestaurantType(jsonObject1.getString("typeOfRestaurant"));
                        restaurant.setCreatedAt(jsonObject1.getString("createdAt"));
                        restaurant.setUpdatedAt(jsonObject1.getString("updatedAt"));

                        // Handle RestaurantWithItems as a single JSON object
                        if (jsonObject1.has("RestaurantWithItems") && !jsonObject1.isNull("RestaurantWithItems")) {
                            JSONObject jsonObject2 = jsonObject1.getJSONObject("RestaurantWithItems");
                            ModelForItemInSearchScreen searchResponse = new ModelForItemInSearchScreen();
                            searchResponse.setId(jsonObject2.optString("_id", "Unknown"));
                            searchResponse.setItemName(jsonObject2.optString("itemName", "Unknown"));
                            searchResponse.setItemPrice(jsonObject2.optString("itemPrice", "0"));
                            searchResponse.setItemDescription(jsonObject2.optString("itemDescription", ""));
                            searchResponse.setDishType(jsonObject2.optString("dishType","unknown"));
                            searchResponse.setItemImages(jsonObject2.getString("itemImage"));

                            // Add the single item to the list
                            List<ModelForItemInSearchScreen> itemList = new ArrayList<>();
                            itemList.add(searchResponse);
                            restaurant.setRestaurantWithItems(itemList);

                            recyclerViewAdapterForSearchFragment.notifyDataSetChanged();
                        } else {
                            // Handle case where RestaurantWithItems might be missing or null
                            restaurant.setRestaurantWithItems(Collections.emptyList());
                        }

                        // Add the restaurant to the list
                        arrayList.add(restaurant);
                    }

                } catch (JSONException e) {
                    Log.e("JSON Parsing Error", e.toString());
                    throw new RuntimeException(e);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("VolleyError", error.toString());
            }
        });

        requestQueue.add(jsonObjectRequest);
    }
}