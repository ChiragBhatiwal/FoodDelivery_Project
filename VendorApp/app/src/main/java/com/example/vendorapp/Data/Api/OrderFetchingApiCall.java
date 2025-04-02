//package com.example.vendorapp.Data.Api;
//
//import android.util.Log;
//
//import com.apollographql.apollo3.rx3.Rx3Apollo;
//import com.example.vendorapp.Data.Models.OrderManageFragment_Model;
//import com.example.vendorapp.Utils.ApolloClientProvider;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import io.reactivex.rxjava3.core.Single;
//import io.reactivex.rxjava3.schedulers.Schedulers;
//
//public class OrderFetchingApiCall {
//    public Single<List<OrderManageFragment_Model>> getOrdersByStatus(int currentPage, List<String> statuses, int limit) {
//        return Rx3Apollo.single(
//                        ApolloClientProvider.getApolloClient().query(new GetOrdersQuery()))
//                .subscribeOn(Schedulers.io())  // Run on background thread
//                .map(response -> {
//                    if (response.hasErrors()) {
//                        Log.e("GraphQL", "GraphQL Errors: " + response.errors);
//                        return new ArrayList<>(); // Return empty list if error occurs
//                    }
//
//                    List<OrderManageFragment_Model> orderList = new ArrayList<>();
//                    List<GetOrdersQuery.Order> orders = response.data.orders;
//
//                    for (GetOrdersQuery.Order order : orders) {
//                        orderList.add(new OrderManageFragment_Model(
//                                order.itemName,
//                                order.itemPrice,
//                                order.buyerName,
//                                order.buyerAddress,
//                                order.itemQuantity,
//                                order.orderStatus
//                        ));
//                    }
//                    return orderList;
//                })
//                .observeOn(AndroidSchedulers.mainThread()); // Get result on UI thread
//    }
//}
