package com.business.foodie.api.controllers;

import com.business.foodie.api.io.FoodRequest;
import com.business.foodie.api.io.FoodResponse;
import com.business.foodie.api.model.FoodEntity;
import com.business.foodie.api.serivce.FoodService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/foods")
@AllArgsConstructor
public class FoodController {

    @Autowired
    private final FoodService foodService;

    @PostMapping()
    public FoodResponse addFood(@RequestPart("food") String foodString,
                                @RequestPart("file")MultipartFile file){
        ObjectMapper objectMapper = new ObjectMapper();

        FoodRequest request = null;

        try{
           request = objectMapper.readValue(foodString, FoodRequest.class);
        }catch (JsonProcessingException ex){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid Json format");
        }

        FoodResponse response = foodService.addFood(request, file);
        return response;
    }

    @GetMapping()
    public List<FoodResponse> readFood(){
        return foodService.readFoods();
    }

    @GetMapping("/{id}")
    public FoodResponse readFoodById(@PathVariable String id){
        return foodService.readFoodBy(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public String deleteFood(@PathVariable String id){
        return foodService.deleteFood(id);
    }
}
