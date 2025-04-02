package com.example.vendorapp.UI.Screens;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioButton;
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
import com.example.vendorapp.R;
import com.example.vendorapp.Utils.Constants;
import com.example.vendorapp.Utils.SharedPreference;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class RestaurantSetupScreen extends AppCompatActivity {

    TextInputEditText restaurantNameEditText,restaurantDescriptionEditText;
    RadioButton vegButton,nonVegButton,bothDishButton;
    TextInputLayout restaurantNameLayout;
    Button restaurantNamePickButton;
    String restaurantName,restaurantDescription,restaurantType = "";

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_restaurant_setup_screen);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        restaurantNameEditText = findViewById(R.id.restaurantName_RestaurantScreen);
        restaurantDescriptionEditText = findViewById(R.id.restaurantDescription_RestaurantScreen);
        restaurantNamePickButton =  findViewById(R.id.restaurantNamePickButton_RestaurantScreen);
        restaurantNameLayout = findViewById(R.id.restaurantNameLayout_RestaurantScreen);
        vegButton = findViewById(R.id.vegRadioButton_RestaurantScreen);
        nonVegButton = findViewById(R.id.nonVegRadioButton_RestaurantScreen);
        bothDishButton = findViewById(R.id.bothDishesRadioButton_RestaurantScreen);

        SharedPreferences sharedPreferences = getSharedPreferences("Login",MODE_PRIVATE);
        String token = sharedPreferences.getString("refreshToken","0");

        Log.d("Token",token);
        vegButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    restaurantType = "Veg";
                }
            }
        });

        nonVegButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    restaurantType = "Non-Veg";
                }
            }
        });

        bothDishButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    restaurantType = "Both-Dish";
                }
            }
        });

        restaurantNamePickButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               restaurantName = String.valueOf(restaurantNameEditText.getText());
               restaurantDescription = String.valueOf(restaurantDescriptionEditText.getText());
               if(!restaurantName.isEmpty() && !restaurantDescription.isEmpty() && !Objects.equals(restaurantType, ""))
               {
                   RequestQueue requestQueue = Volley.newRequestQueue(RestaurantSetupScreen.this);

                   JSONObject jsonObject = new JSONObject();
                   try {
                       jsonObject.put("restaurantName", restaurantName);
                       jsonObject.put("restaurantDescription", restaurantDescription);
                       jsonObject.put("restaurantType", restaurantType);
                   }catch (Exception e)
                   {
                       Toast.makeText(RestaurantSetupScreen.this, "Error in Calling Api " + e, Toast.LENGTH_SHORT).show();
                   }

                   JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Constants.Url+"/restaurant/create", jsonObject, new Response.Listener<JSONObject>() {
                       @Override
                       public void onResponse(JSONObject response) {
                           try {
                               SharedPreference sharedPreference = SharedPreference.getInstance(RestaurantSetupScreen.this);
                               sharedPreference.saveUserInfo("restaurantId",response.getString("_id"));
                           } catch (JSONException e) {
                               throw new RuntimeException(e);
                           }
                           Intent intent = new Intent(RestaurantSetupScreen.this, LocationScreen.class);
                          startActivity(intent);
                       }
                   }, new Response.ErrorListener() {
                       @Override
                       public void onErrorResponse(VolleyError error) {
                           Toast.makeText(RestaurantSetupScreen.this, "Error in Restaurant Screen "+error, Toast.LENGTH_SHORT).show();
                       }
                   })
                   {
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
            }
        });
    }
}