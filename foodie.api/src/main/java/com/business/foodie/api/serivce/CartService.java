package com.business.foodie.api.serivce;

import com.business.foodie.api.io.CartRequest;
import com.business.foodie.api.io.CartResponse;

public interface CartService {

    CartResponse addToCart(CartRequest request);

    CartResponse getCart();

    void clearCart();

   CartResponse removeFromCart(CartRequest cartRequest);
}
