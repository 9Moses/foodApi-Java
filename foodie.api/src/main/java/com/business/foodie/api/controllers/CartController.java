package com.business.foodie.api.controllers;

import com.business.foodie.api.io.CartRequest;
import com.business.foodie.api.io.CartResponse;
import com.business.foodie.api.serivce.CartService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@RestController
@AllArgsConstructor
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    @PostMapping
    public CartResponse addToCart(@RequestBody CartRequest request){
       String foodId = request.getFoodId();

       if(foodId == null || foodId.isEmpty()){
           throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "FoodId not found");
       }

      return cartService.addToCart(request);
    }

    @GetMapping
    public CartResponse getCart(){
        return cartService.getCart();
    }
}
