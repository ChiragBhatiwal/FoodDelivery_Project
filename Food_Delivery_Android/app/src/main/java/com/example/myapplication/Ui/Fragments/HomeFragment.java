package com.example.myapplication.Ui.Fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.myapplication.Ui.Adapters.RecyclerViewAdapterForHomeFragment;
import com.example.myapplication.Data.Model.ModelForHomeFragment;
import com.example.myapplication.Utility.Constant;
import com.example.myapplication.R;
import com.example.myapplication.Ui.Screens.RestaurantViewScreen;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class HomeFragment extends Fragment implements RecyclerViewAdapterForHomeFragment.OnItemClickedListener {

    ImageView imageView;
    RecyclerView recyclerView;
    private ArrayList<ModelForHomeFragment> arrayList;
    private RecyclerViewAdapterForHomeFragment recyclerViewAdapterForHomeFragment;

    public HomeFragment() {}

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        arrayList = new ArrayList<>();
        recyclerView = view.findViewById(R.id.recyclerView_HomeFragment);
        imageView = view.findViewById(R.id.image_view_HomeFragment);
        getRestaurantForHome();
        recyclerViewAdapterForHomeFragment = new RecyclerViewAdapterForHomeFragment(getContext(),arrayList,this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        recyclerView.setAdapter(recyclerViewAdapterForHomeFragment);
        return view;
    }

    void getRestaurantForHome(){
        RequestQueue requestQueue = Volley.newRequestQueue(requireContext());

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Constant.base_IP +"/restaurant/getRestaurant", null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray jsonArray = response.getJSONArray("getItems");
                    for (int i=0;i<jsonArray.length();i++)
                    {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        ModelForHomeFragment modelForHomeFragment = new ModelForHomeFragment();
                        modelForHomeFragment.set_id(jsonObject.getString("_id"));
                        modelForHomeFragment.setRestaurantName(jsonObject.getString("restaurantName"));
//                        modelForHomeFragment.setAddressRestaurant(jsonObject.getString("addressRestaurant"));
                        modelForHomeFragment.setUserId(jsonObject.getString("userId"));
//                        modelForHomeFragment.setImageOfRestaurant(jsonObject.getString("imageOfRestaurant"));
                        arrayList.add(modelForHomeFragment);
                        recyclerViewAdapterForHomeFragment.notifyDataSetChanged();
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), "Error ocurred "+error, Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue.add(jsonObjectRequest);
    }

    @Override
    public void onCardViewClicked(int position,String restaurantId) {
        Intent intent = new Intent(getContext(), RestaurantViewScreen.class);
        Bundle bundle = new Bundle();
        bundle.putString("_id",restaurantId);
        intent.putExtras(bundle);
        startActivity(intent);
    }
}