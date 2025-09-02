package com.business.foodie.api.io;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class OrderRequest {
    private String email; // Required by Paystack
    private String phoneNumber; // optional but useful
   private List<OrderItem> orderItems;
   private String userAddress;
   private double amount;
    private String currency = "GHC";
    private String reference; // unique order ref (you generate this)

    private String orderStatus;
   // private String callbackUrl; // Paystack will redirect here after payment

}
