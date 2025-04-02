package com.example.myapplication.Ui.Screens;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.myapplication.Ui.Adapters.RecyclerViewAdapterForRestaurantScreen;
import com.example.myapplication.Data.Api.CartFragmentApiCalls;
import com.example.myapplication.Data.Model.ModelForRestaurantScreen;
import com.example.myapplication.Data.Model.RestaurantItem;
import com.example.myapplication.Utility.Constant;
import com.example.myapplication.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RestaurantViewScreen extends AppCompatActivity implements RecyclerViewAdapterForRestaurantScreen.OnItemClickListener {
    RecyclerView recyclerView;
    ArrayList<ModelForRestaurantScreen> arrayList;
    ArrayList<RestaurantItem> restaurantItems;
    String id,token;
    TextView restaurantName,restaurantAddress;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_restaurant_view_screen);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        restaurantName = findViewById(R.id.restaurant_Name_RestaurantScreen);
        restaurantAddress = findViewById(R.id.restaurant_Address_restaurantScreen);
        recyclerView = findViewById(R.id.recyclerView_RestaurantScreen);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        assert bundle != null;
        id = bundle.getString("_id");
        arrayList =  new ArrayList<>();
        restaurantItems = new ArrayList<>();
        //getProduct Function
        try {
            getRestaurantDetails();

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        SharedPreferences sharedPreferences = getSharedPreferences("Login",MODE_PRIVATE);
        token = sharedPreferences.getString("refreshToken","");
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        RecyclerViewAdapterForRestaurantScreen recyclerViewAdapterForRestaurantScreen = new RecyclerViewAdapterForRestaurantScreen(this,restaurantItems,this);
        recyclerView.setAdapter(recyclerViewAdapterForRestaurantScreen);
    }

    //Api Calling and Sending Data to Models
    public void getRestaurantDetails() throws JSONException {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("value", id);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Constant.base_IP +"/restaurant/getRestaurantDetails", jsonObject,
                new Response.Listener<JSONObject>() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonArray = response.getJSONArray("restaurantDetails");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                ModelForRestaurantScreen modelForRestaurantScreen = new ModelForRestaurantScreen();
                                modelForRestaurantScreen.set_id(jsonObject1.optString("_id"));
                                modelForRestaurantScreen.setRestaurantName(jsonObject1.optString("restaurantName"));
                                modelForRestaurantScreen.setAddressRestaurant(jsonObject1.optString("addressRestaurant"));
                                modelForRestaurantScreen.setImageOfRestaurant(jsonObject1.optString("imageOfRestaurant"));

                                JSONArray jsonArray1 = jsonObject1.getJSONArray("RestaurantItems");
                                List<RestaurantItem> items = new ArrayList<>();
                                for (int j = 0; j < jsonArray1.length(); j++) {  // Use 'j' for inner loop
                                    JSONObject jsonObject2 = jsonArray1.getJSONObject(j);  // Use 'j' here
                                    RestaurantItem restaurantItem = new RestaurantItem();
                                    restaurantItem.setRestaurantId(jsonObject2.optString("restaurantId"));
                                    restaurantItem.setItemDescription(jsonObject2.optString("itemDescription"));
                                    restaurantItem.set_id(jsonObject2.optString("_id"));
                                    restaurantItem.setItemName(jsonObject2.optString("itemName"));
                                    restaurantItem.setItemPrice(jsonObject2.optString("itemPrice"));

                                    JSONArray jsonArray2 = jsonObject2.optJSONArray("itemImages");
                                    List<String> images = new ArrayList<>();
                                    if (jsonArray2 != null) {
                                        for (int k = 0; k < jsonArray2.length(); k++) {
                                            images.add(jsonArray2.optString(k, ""));
                                        }
                                    }
                                    restaurantItem.setItemImages(images);
                                    items.add(restaurantItem);
                                    restaurantItems.add(restaurantItem);  // Add to the main ArrayList
                                }
                                restaurantName.setText(modelForRestaurantScreen.getRestaurantName());
                                restaurantAddress.setText(modelForRestaurantScreen.getAddressRestaurant());
                                modelForRestaurantScreen.setRestaurantItems(items);
                                arrayList.add(modelForRestaurantScreen);
                            }
                            // Notify adapter that data has been updated
                            Objects.requireNonNull(recyclerView.getAdapter()).notifyDataSetChanged();

                        } catch (JSONException e) {
                            Toast.makeText(RestaurantViewScreen.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(RestaurantViewScreen.this, "API Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        requestQueue.add(jsonObjectRequest);
    }


    @Override
    public void onAddToBagClick(int position,String itemId){
        CartFragmentApiCalls.pushItemInCart(this,itemId,token);
    }



    @Override
    public void onGoToRestaurantClicked(int position) {


    }

}