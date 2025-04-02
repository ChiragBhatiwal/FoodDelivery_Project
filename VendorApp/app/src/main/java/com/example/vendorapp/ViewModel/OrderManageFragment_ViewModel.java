package com.example.vendorapp.ViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.vendorapp.Data.Models.OrderManageFragment_Model;

import java.util.ArrayList;
import java.util.List;

public class OrderManageFragment_ViewModel extends ViewModel{
        List<String> screenStatuses;
        int currentPage = 1;
        private MutableLiveData<List<OrderManageFragment_Model>> orders;

        public OrderManageFragment_ViewModel(List<String> screenStatuses){
            this.screenStatuses = screenStatuses;
            orders = new MutableLiveData<>();
            fetchOrders();
        }


        void loadMoreOrders(){
            currentPage++;
            fetchOrders();
        }

        void fetchOrders(){

        }
    }


