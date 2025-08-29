package com.business.foodie.api.repository;

import com.business.foodie.api.model.PaymentPaystackEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaystackPaymentRepository extends MongoRepository<PaymentPaystackEntity, Long> {
}
