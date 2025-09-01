package com.business.foodie.api.controllers;

import com.business.foodie.api.io.OrderRequest;
import com.business.foodie.api.io.OrderResponse;
import com.business.foodie.api.serivce.OrderService;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(
        value = "/api/order",
        produces = MediaType.APPLICATION_JSON_VALUE
)
@AllArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/create")
    public OrderResponse createOrderWithPayment(@RequestBody OrderRequest request){
        return null;
    }
}
