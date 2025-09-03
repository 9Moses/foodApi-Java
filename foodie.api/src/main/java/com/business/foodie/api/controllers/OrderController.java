package com.business.foodie.api.controllers;

import com.business.foodie.api.io.InitializePaymentResponse;
import com.business.foodie.api.io.OrderRequest;
import com.business.foodie.api.io.OrderResponse;
import com.business.foodie.api.io.PaymentVerificationResponse;
import com.business.foodie.api.serivce.OrderService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(
        value = "/api/order",
        produces = MediaType.APPLICATION_JSON_VALUE
)
@AllArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponse createOrderWithPayment(@RequestBody OrderRequest request){
        return orderService.createOrderWithPayment(request);
    }

    @PostMapping("/initializepayment")
    public InitializePaymentResponse initializePayment(@Validated @RequestBody OrderRequest request) throws Throwable {
        return orderService.initializePayment(request);
    }

    @GetMapping("/verifypayment/{reference}/{id}")
    public PaymentVerificationResponse paymentVerification(@PathVariable("reference") String reference,
                                                           @PathVariable("id") String id) throws Exception {
        if (reference.isEmpty() || id.isEmpty()) {
            throw new Exception("reference and id must be provided in path");
        }
        return orderService.paymentVerification(reference, id);
    }

    @GetMapping("/getUserOrder")
    public List<OrderResponse> getOrders(){
      return orderService.getUserOrders();
    }

    @DeleteMapping("/deleteOrder/{orderId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletedOrder(@PathVariable String orderId){
        orderService.removeOrder(orderId);
    }

    @GetMapping("/getAllUserOrder")
    public List<OrderResponse> getAllOrdersOfAllUsers(){
        return orderService.getAllOrdersOfAllUsers();
    }

    @PutMapping("/update/{orderId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateOrderStatus(@PathVariable String orderId,@RequestParam String status){
        orderService.updateOrderStatus(orderId, status);
    }
}
