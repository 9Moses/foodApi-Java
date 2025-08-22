package com.business.foodie.api.repository;

import com.business.foodie.api.model.CartEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartRepository extends MongoRepository<CartEntity, String> {
}
