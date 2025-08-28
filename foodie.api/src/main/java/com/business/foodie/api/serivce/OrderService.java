package com.business.foodie.api.serivce;

import com.business.foodie.api.io.OrderRequest;
import com.business.foodie.api.io.OrderResponse;

public interface OrderService {

  OrderResponse createOrderWithPayment(OrderRequest requet);
}
