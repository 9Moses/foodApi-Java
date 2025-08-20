package com.business.foodie.api.controllers;

import com.business.foodie.api.io.UserRequest;
import com.business.foodie.api.io.UserResponse;
import com.business.foodie.api.serivce.UserService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/api")
public class UserControllers {

    @Autowired
    private final UserService service;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse register(@RequestBody UserRequest request){
        return  service.registerUser(request);

    }

}
