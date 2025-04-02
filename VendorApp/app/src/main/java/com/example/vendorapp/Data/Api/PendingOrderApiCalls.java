package com.example.vendorapp.Data.Api;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.vendorapp.Utils.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class PendingOrderApiCalls {

    public static void getPendingOrder(Context context,String token,String restaurantId,ApiCallback callback){

        RequestQueue requestQueue = Volley.newRequestQueue(context);

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("restaurantId",restaurantId);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Constants.Url+"/order/findOrders", jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                callback.onSuccess(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callback.onError(error.toString());
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

    public interface ApiCallback {
        void onSuccess(JSONObject response);
        void onError(String errorMessage);
    }
}
