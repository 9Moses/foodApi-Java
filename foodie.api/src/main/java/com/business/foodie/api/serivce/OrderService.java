package com.business.foodie.api.serivce;

import com.business.foodie.api.io.InitializePaymentResponse;
import com.business.foodie.api.io.OrderRequest;
import com.business.foodie.api.io.OrderResponse;
import com.business.foodie.api.io.PaymentVerificationResponse;

public interface OrderService {

  OrderResponse createOrderWithPayment(OrderRequest request);
    InitializePaymentResponse initializePayment(OrderRequest request);
    PaymentVerificationResponse paymentVerification(String reference, String id) throws Exception;
}
