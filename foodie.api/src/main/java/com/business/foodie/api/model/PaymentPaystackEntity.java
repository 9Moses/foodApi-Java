package com.business.foodie.api.model;

import lombok.Builder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.math.BigDecimal;
import java.util.Date;

@Builder
@Document(collection = "paystack_payment")
public class PaymentPaystackEntity {
    @Id
    private String id;

    private String userId;

    @Field(name = "reference")
    private String reference;

    @Field(name = "amount")
    private BigDecimal amount;

    @Field(name = "gateway_response")
    private String gatewayResponse;

    @Field(name = "paid_at")
    private String paidAt;

    @Field(name = "created_at")
    private String createdAt;

    @Field(name = "channel")
    private String channel;

    @Field(name = "currency")
    private String currency;

    @Field(name = "ip_address")
    private String ipAddress;

//    @Field("pricing_plan_type")
//    private PricingPlanType pricingPlanType = PricingPlanType.BASIC;

    @CreatedDate
    @Field("created_on")
    private Date createdOn;
}
