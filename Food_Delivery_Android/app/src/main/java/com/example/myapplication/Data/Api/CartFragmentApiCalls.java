package com.example.myapplication.Data.Api;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.myapplication.Utility.Constant;

import java.util.HashMap;
import java.util.Map;

public class CartFragmentApiCalls {
    public static void pushItemInCart(Context context, String itemId,String token){
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.base_IP +"/cart/add/"+itemId, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(context, "Successfully Inserted", Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                super.getParams();
                Map<String,String> params = new HashMap<>();
                params.put("itemId",itemId);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                 super.getHeaders();
                 Map<String,String> headers = new HashMap<>();
                 headers.put("Authorization",token);
                 return headers;
            }
        };
        requestQueue.add(stringRequest);
    }

    public static void deleteItemFromCart(){

    }
}
