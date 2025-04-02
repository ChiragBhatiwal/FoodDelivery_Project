package com.example.myapplication.Ui.Screens;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.myapplication.MainActivity;
import com.example.myapplication.Utility.Constant;
import com.example.myapplication.R;
import com.example.myapplication.Utility.Sockets;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import io.socket.emitter.Emitter;

public class OrderPlaceScreen extends AppCompatActivity {

    String restaurantId,userId,addressId,itemId,token;
    TextView restaurantName,addressRestaurant,itemName,itemPrice,itemQuantity,totalPrice,userName,userAddress,userPhone,quantityItem,itemPriceBillingSection;
    ImageView incImage,decImage;
    Button placeOrderButton;
    RadioButton cashOnDeliveryButton;
    private int quantity = 1;
    private boolean isCodChecked = false;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_order_place);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        restaurantName = findViewById(R.id.restaurantName_orderScreen);
        userAddress = findViewById(R.id.userAddress_OrderScreen);
        addressRestaurant = findViewById(R.id.addressOfRestaurant_OrderScreen);
        userPhone = findViewById(R.id.userNumber_OrderScreen);
        itemName = findViewById(R.id.nameOfItem_OrderScreen);
        itemPrice = findViewById(R.id.priceOfItem_OrderScreen);
        userName = findViewById(R.id.userName_OrderScreen);
        quantityItem = findViewById(R.id.tv_quantity);
        incImage = findViewById(R.id.img_increment);
        decImage = findViewById(R.id.img_decrement);
        totalPrice = findViewById(R.id.totalPriceAccordingQuantity_OrderScreen);
        placeOrderButton = findViewById(R.id.placeOrderButton);
        itemPriceBillingSection = findViewById(R.id.itemBasePrice_BillingSection);
        cashOnDeliveryButton = findViewById(R.id.radioButtonCashOnDelivery);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        assert bundle != null;
        itemId = bundle.getString("ItemId","");
        String RestaurantId = bundle.getString("RestaurantId","");
        Log.d("Item id",itemId);
        Log.d("restaurant",RestaurantId);
        SharedPreferences sharedPreferences = getSharedPreferences("Login",MODE_PRIVATE);
        token = sharedPreferences.getString("refreshToken","0");
        Log.d("Token",""+token);
        Sockets.getInstance(OrderPlaceScreen.this,token).on("updated-amount", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                String value = args[0].toString();
            }
        });
        getOrderDetails(itemId,token);

        incImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(quantity>=10)
                {
                    Toast.makeText(OrderPlaceScreen.this, "Sorry We can't deliver More Than 10 Items", Toast.LENGTH_SHORT).show();
                }else{
                    quantity++;
                    quantityItem.setText(String.valueOf(quantity));
                    int value = Integer.parseInt(itemPrice.getText().toString());
                    int totalValue = value*quantity;
                    totalPrice.setText(String.valueOf(totalValue));
                    itemPriceBillingSection.setText(String.valueOf(totalValue));
                }
                Sockets.getInstance(OrderPlaceScreen.this,token).emit("quantity-updated",quantity);
            }
        });

        decImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(quantity>1)
                {
                    quantity--;
                    quantityItem.setText(String.valueOf(quantity));
                    int value = Integer.parseInt(itemPrice.getText().toString());
                    int totalValue = value*quantity;
                    totalPrice.setText(String.valueOf(totalValue));
                    itemPriceBillingSection.setText(String.valueOf(totalValue));
                }
                Sockets.getInstance(OrderPlaceScreen.this,token).emit("quantity-updated",quantity);
            }
        });

        cashOnDeliveryButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    isCodChecked = true;
                }
                else{
                    isCodChecked = false;
                }
            }
        });

        placeOrderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isCodChecked)
                {
                    Toast.makeText(OrderPlaceScreen.this, "Please Selected Payment Details", Toast.LENGTH_SHORT).show();
                }else{
                   placeOrder(itemId,token);
                }
            }
        });
    }

    void getOrderDetails(String itemId,String token){
        RequestQueue requestQueue = Volley.newRequestQueue(OrderPlaceScreen.this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,Constant.base_IP+"/order/order-details/"+itemId, null,new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response){
                Log.d("Order Screen",response.toString());
                try {
                    JSONArray jsonArray = response.getJSONArray("itemDetails");
                    for(int i =0;i<jsonArray.length();i++)
                    {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);

                        restaurantId = jsonObject.getString("restaurantId");
                        itemName.setText(jsonObject.getString("itemName"));
                        String price = jsonObject.optString("itemPrice", "0");
                        itemPrice.setText(price);

                        JSONArray jsonArray1 = jsonObject.getJSONArray("RestaurantDetails");
                        for(int j = 0;j<jsonArray1.length();j++)
                        {
                            JSONObject jsonObject1 = jsonArray1.getJSONObject(j);
                            restaurantName.setText(jsonObject1.getString("restaurantName"));
                        }
                    }

                    JSONArray jsonArray1 = response.getJSONArray("userDetails");
                    for(int k=0;k<jsonArray1.length();k++)
                    {
                        JSONObject jsonObject = jsonArray1.getJSONObject(k);
                        userId = jsonObject.getString("_id");
                        userName.setText(jsonObject.getString("username"));
                        userPhone.setText(jsonObject.getString("phoneNumber"));

                        JSONArray jsonArray2 = jsonObject.getJSONArray("UserAddress");
                        for (int h=0;h<jsonArray2.length();h++)
                        {
                            JSONObject jsonObject1 = jsonArray2.getJSONObject(h);
                            addressId = jsonObject1.getString("_id");
                            userAddress.setText(jsonObject1.getString("Address"));
                        }
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(OrderPlaceScreen.this, "Error While Calling Api"+error, Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                super.getHeaders();
                Map<String,String> params = new HashMap<>();
                params.put("Authorization",token);
                return params;
            }
        };
        requestQueue.add(jsonObjectRequest);
    }

    void placeOrder(String itemId,String token){
        RequestQueue requestQueue = Volley.newRequestQueue(OrderPlaceScreen.this);

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("quantity",quantity);
            jsonObject.put("restaurantId",restaurantId);
            jsonObject.put("addressId",addressId);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Constant.base_IP+"/order/place-order/"+itemId, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if(response != null)
                {
                    Intent intent = new Intent(OrderPlaceScreen.this, MainActivity.class);
                    startActivity(intent);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Error in OrderScreen",error.toString());
                Toast.makeText(OrderPlaceScreen.this, "Got Error in Order Screen", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                super.getHeaders();
                Map<String, String> params = new HashMap<>();
                params.put("Authorization", token);
                return params;
            }
        };
        requestQueue.add(jsonObjectRequest);
    }
}