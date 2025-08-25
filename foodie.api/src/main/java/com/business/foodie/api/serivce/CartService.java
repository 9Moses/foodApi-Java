package com.business.foodie.api.serivce;

import com.business.foodie.api.io.CartRequest;
import com.business.foodie.api.io.CartResponse;

public interface CartService {

    CartResponse addToCart(CartRequest request);
}
