package com.example.vendorapp.Data.Api;

import android.annotation.SuppressLint;
import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.vendorapp.Utils.Constants;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MenuScreenApiCalls {
    public static void getMenuItems(Context context,String restaurantId,String token,ApiCallback apiCallback){
        RequestQueue requestQueue = Volley.newRequestQueue(context);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Constants.Url+"/item/getItems/"+restaurantId, null, new Response.Listener<JSONObject>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(JSONObject response) {
               apiCallback.onSuccess(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                apiCallback.onError(error.toString());
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

    public interface ApiCallback{
        void onSuccess(JSONObject response);
        void onError(String error);
    }
}
