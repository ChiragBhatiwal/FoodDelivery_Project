package com.example.vendorapp.ViewModel;

import android.app.Application;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.vendorapp.Data.Api.MenuScreenApiCalls;
import com.example.vendorapp.Data.Models.MenuScreen_Model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MenuScreen_ViewModel extends AndroidViewModel {

    private MutableLiveData<List<MenuScreen_Model>> items;
    private boolean isDataLoaded = false;

    public MenuScreen_ViewModel(@NonNull Application application) {
        super(application);
        items = new MutableLiveData<>(new ArrayList<>());
    }

    public LiveData<List<MenuScreen_Model>> getItems(){
         return items;
    }

    public void setItems(List<MenuScreen_Model> item)
    {
        items.postValue(item);
    }

    public void fetchItem(Context context,String restaurantId,String token){
        if(isDataLoaded)
        {
            return;
        }
        Log.d("MenuScreen RestaurantId is :-",restaurantId);
        MenuScreenApiCalls.getMenuItems(context, restaurantId, token, new MenuScreenApiCalls.ApiCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                List<MenuScreen_Model> menuScreenModel = parseIntoJsonObject(response);
                setItems(menuScreenModel);
                isDataLoaded = true;
            }
            @Override
            public void onError(String error) {
                Log.d("Menu Screen",error);
                Toast.makeText(context, "Error in Calling Menu Items", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public List<MenuScreen_Model> parseIntoJsonObject(JSONObject response){
        List<MenuScreen_Model> items = new ArrayList<>();
        try {
            JSONArray jsonArray = response.getJSONArray("findItems");
            for (int i=0;i<jsonArray.length();i++)
            {
                MenuScreen_Model menuScreenModel = new MenuScreen_Model();
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                menuScreenModel.set_id(jsonObject.getString("_id"));
                menuScreenModel.setItemName(jsonObject.getString("itemName"));
                menuScreenModel.setItemPrice(jsonObject.getString("itemPrice"));
                menuScreenModel.setItemImage(jsonObject.getString("itemImage"));
                menuScreenModel.setItemDescription(jsonObject.getString("itemDescription"));
                menuScreenModel.setItemType(jsonObject.getString("dishType"));

                items.add(menuScreenModel);
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        return items;
    }
}
