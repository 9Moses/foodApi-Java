package com.business.foodie.api.serivce;

import com.business.foodie.api.io.CartRequest;
import com.business.foodie.api.io.CartResponse;
import com.business.foodie.api.model.CartEntity;
import com.business.foodie.api.repository.CartRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


@Service
@AllArgsConstructor
public class CartServiceImp implements CartService{

   private final CartRepository cartRepository;

   private final  UserService userService;

    @Override
    public CartResponse addToCart(CartRequest request) {
        String loggedInUserId = userService.findByUserId();
        Optional<CartEntity> cartOptional = cartRepository.findByUserId(loggedInUserId);
        CartEntity cart = cartOptional.orElseGet(()-> new CartEntity(loggedInUserId, new HashMap<>()));
        Map<String, Integer> cartItems = cart.getItems();
        cartItems.put(request.getFoodId(), cartItems.getOrDefault(request.getFoodId(), 0)+ 1);
        cart.setItems(cartItems);
       cart = cartRepository.save(cart);
       return convertToResponse(cart);
    }

    private CartResponse convertToResponse(CartEntity cartEntity){
       return CartResponse.builder()
               .id(cartEntity.getId())
               .userId(cartEntity.getUserId())
               .items(cartEntity.getItems())
               .build();
    }
}
