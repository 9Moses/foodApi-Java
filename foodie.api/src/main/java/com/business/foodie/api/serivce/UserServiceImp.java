package com.business.foodie.api.serivce;

import com.business.foodie.api.io.UserRequest;
import com.business.foodie.api.io.UserResponse;
import com.business.foodie.api.model.UserEntity;
import com.business.foodie.api.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserServiceImp implements UserService{

    @Autowired
    private final UserRepository repository;

    @Autowired
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserResponse registerUser(UserRequest request) {
       UserEntity newUser = convertToEntity(request);
      newUser =  repository.save(newUser);
      return convertTOResponse(newUser);
    }

    private UserEntity convertToEntity(UserRequest request){
        return UserEntity.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .name(request.getName())
                .build();
    }

    private UserResponse convertTOResponse(UserEntity registerUser){
        return UserResponse.builder()
                .id(registerUser.getId())
                .email(registerUser.getEmail())
                .name(registerUser.getName())
                .build();
    }
}
