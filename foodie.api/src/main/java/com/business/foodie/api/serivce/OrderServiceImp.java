package com.business.foodie.api.serivce;

import com.business.foodie.api.io.OrderRequest;
import com.business.foodie.api.io.OrderResponse;
import com.business.foodie.api.model.OrderEntity;
import com.business.foodie.api.repository.OrderRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class OrderServiceImp implements OrderService{

    private final OrderRepository orderRepository;

    @Override
    public OrderResponse createOrderWithPayment(OrderRequest request) {
       OrderEntity newOrder =  convertToEntity(request);
       newOrder = orderRepository.save(newOrder);


       //create a payment order
    }

    private OrderEntity convertToEntity(OrderRequest request) {
        return OrderEntity.builder()
                .userId(request.getUserId())
                .userAddress(request.getUserAddress())
                .amount(request.getAmount())
                .ordersItems(request.getOrderItems())
                .build();
    }
}
