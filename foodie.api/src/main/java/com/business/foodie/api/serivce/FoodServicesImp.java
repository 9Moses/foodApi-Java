package com.business.foodie.api.serivce;

import com.business.foodie.api.io.FoodRequest;
import com.business.foodie.api.io.FoodResponse;
import com.business.foodie.api.model.FoodEntity;
import com.business.foodie.api.repository.FoodRepository;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@Service
@AllArgsConstructor
public class FoodServicesImp implements FoodService{

    private  final Cloudinary cloudinary;

    @Autowired
    private final FoodRepository foodRepository;

//    public FoodServicesImp(Cloudinary cloudinary) {
//        this.cloudinary = cloudinary;
//    }

    @Override
    public String uploadFile(MultipartFile file) {
      String filenameExtension =  file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".")+1);
        String key = UUID.randomUUID().toString() + "." + filenameExtension;

        try{
            Map uploadResult = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap(
                            "public_id", key,       // custom filename
                            "folder", "foodie"      // optional: put all files under "foodie" folder
                    )
            );

            // You can return secure_url (HTTPS)
            Object secureUrl = uploadResult.get("secure_url");

            if (secureUrl == null) {
                throw new RuntimeException("Upload failed: secure_url is missing from Cloudinary response");
            }

            return secureUrl.toString();

        }catch (IOException e){
            System.out.println("Check the file again");
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "An error occurred while uploading the file");

        }
    }

    @Override
    public FoodResponse addFood(FoodRequest request, MultipartFile file) {
       FoodEntity newFoodEntity = convertToEntity(request);
       String imageUrl = uploadFile(file);

       newFoodEntity.setImageUrl(imageUrl);
       newFoodEntity = foodRepository.save(newFoodEntity);

       return convertToReposne(newFoodEntity);
    }

    private FoodEntity convertToEntity(FoodRequest request){
        return FoodEntity.builder()
                .name(request.getName())
                .description(request.getDescription())
                .category(request.getCategory())
                .price(request.getPrice())
                .build();
    }

    private FoodResponse convertToReposne(FoodEntity entity){
         return FoodResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .imageUrl(entity.getImageUrl())
                .category(entity.getCategory())
                .price(entity.getPrice())
                .build();
    }
}
