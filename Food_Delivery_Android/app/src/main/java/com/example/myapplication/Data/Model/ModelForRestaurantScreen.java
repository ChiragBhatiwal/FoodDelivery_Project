package com.example.myapplication.Data.Model;

import java.util.List;

public class ModelForRestaurantScreen {
    private String _id;
    private String userId;
    private String restaurantName;
    private String addressRestaurant;
    private String imageOfRestaurant;
    private List<RestaurantItem> RestaurantItems;

    // Getters and Setters
    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getRestaurantName() {
        return this.restaurantName;
    }

    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }

    public String getAddressRestaurant() {
        return this.addressRestaurant;
    }

    public void setAddressRestaurant(String addressRestaurant) {
        this.addressRestaurant = addressRestaurant;
    }

    public String getImageOfRestaurant() {
        return this.imageOfRestaurant;
    }

    public void setImageOfRestaurant(String imageOfRestaurant) {
        this.imageOfRestaurant = imageOfRestaurant;
    }

    public List<RestaurantItem> getRestaurantItems() {
        return RestaurantItems;
    }

    public void setRestaurantItems(List<RestaurantItem> restaurantItems) {
        RestaurantItems = restaurantItems;
    }
}
