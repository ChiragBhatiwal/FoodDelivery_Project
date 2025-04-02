package com.example.myapplication.Data.Model;

public class ModelForHomeFragment {

            private String _id;
            private String userId;
            private String restaurantName;
            private String restaurantType;

    public String getRestaurantType() {
        return restaurantType;
    }

    public void setRestaurantType(String restaurantType) {
        this.restaurantType = restaurantType;
    }

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
                return restaurantName;
            }

            public void setRestaurantName(String restaurantName) {
                this.restaurantName = restaurantName;
            }
        }



