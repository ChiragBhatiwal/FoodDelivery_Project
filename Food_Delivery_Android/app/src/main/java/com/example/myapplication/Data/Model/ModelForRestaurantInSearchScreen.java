package com.example.myapplication.Data.Model;

import java.util.List;

public class ModelForRestaurantInSearchScreen {

    private String id;
    private String userId;
    private String restaurantName;
    private String restaurantType;
    private List<ModelForItemInSearchScreen> restaurantWithItems;
    private String createdAt;
    private String updatedAt;

    public String getRestaurantType() {
        return restaurantType;
    }

    public void setRestaurantType(String restaurantType) {
        this.restaurantType = restaurantType;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }



    public List<ModelForItemInSearchScreen> getRestaurantWithItems() {
        return restaurantWithItems;
    }

    public void setRestaurantWithItems(List<ModelForItemInSearchScreen> restaurantWithItems) {
        this.restaurantWithItems = restaurantWithItems;
    }
}


