package com.business.foodie.api.serivce;

import com.business.foodie.api.io.FoodRequest;
import com.business.foodie.api.io.FoodResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FoodService {

    String uploadFile(MultipartFile file);

    FoodResponse addFood(FoodRequest request, MultipartFile file);

    List<FoodResponse> readFoods();

    FoodResponse readFoodBy(String id);

    boolean deleteFile(String filename);

    String deleteFood(String id);
}
