package com.example.vendorapp.ViewModelFactory;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.vendorapp.ViewModel.OrderManageFragment_ViewModel;

import java.util.List;

public class OrderManageViewModelFactory implements ViewModelProvider.Factory {
    private final List<String> statuses;

    public OrderManageViewModelFactory(List<String> statuses) {
        this.statuses = statuses;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(OrderManageFragment_ViewModel.class)) {
            return (T) new OrderManageFragment_ViewModel(statuses);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
