package com.business.foodie.api.io;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class OrderRequest {

   private String userId;
   private List<OrderItem> orderItems;
   private String userAddress;
   private double amount;

}
