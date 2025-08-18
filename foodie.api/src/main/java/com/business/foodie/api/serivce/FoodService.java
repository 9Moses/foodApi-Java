package com.business.foodie.api.serivce;

import com.business.foodie.api.io.FoodRequest;
import com.business.foodie.api.io.FoodResponse;
import org.springframework.web.multipart.MultipartFile;

public interface FoodService {

    String uploadFile(MultipartFile file);

    FoodResponse addFood(FoodRequest request, MultipartFile file);
}
