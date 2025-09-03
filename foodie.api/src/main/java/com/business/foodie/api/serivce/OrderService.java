package com.business.foodie.api.serivce;

import com.business.foodie.api.io.InitializePaymentResponse;
import com.business.foodie.api.io.OrderRequest;
import com.business.foodie.api.io.OrderResponse;
import com.business.foodie.api.io.PaymentVerificationResponse;

import java.util.List;

public interface OrderService {

  OrderResponse createOrderWithPayment(OrderRequest request);
    InitializePaymentResponse initializePayment(OrderRequest request);
    PaymentVerificationResponse paymentVerification(String reference, String id) throws Exception;

    List<OrderResponse> getUserOrders();

    void removeOrder(String orderId);

    List<OrderResponse> getAllOrdersOfAllUsers();

     void updateOrderStatus(String orderId, String status);
}
