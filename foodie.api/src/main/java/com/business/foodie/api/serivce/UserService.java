package com.business.foodie.api.serivce;

import com.business.foodie.api.io.UserRequest;
import com.business.foodie.api.io.UserResponse;

public interface UserService {
    UserResponse registerUser(UserRequest request);

    String findByUserId();
}
