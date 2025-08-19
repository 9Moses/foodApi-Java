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
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class FoodServicesImp implements FoodService{

    private  final Cloudinary cloudinary;

    @Autowired
    private final FoodRepository foodRepository;

    @Override
    public String uploadFile(MultipartFile file) {
        // Extract file extension
        String filenameExtension = file.getOriginalFilename()
                .substring(file.getOriginalFilename().lastIndexOf(".") + 1);

        // Use UUID as public_id (no extension included!)
        String key = UUID.randomUUID().toString();

        try {
            Map uploadResult = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap(
                            "public_id", "foodie/" + key,  // store in "foodie" folder
                            "folder", "foodie",
                            "overwrite", true,
                            "resource_type", "image"
                    )
            );

            Object secureUrl = uploadResult.get("secure_url");

            if (secureUrl == null) {
                throw new RuntimeException("Upload failed: secure_url is missing from Cloudinary response");
            }

            return secureUrl.toString();

        } catch (IOException e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "An error occurred while uploading the file");
        }
    }

    @Override
    public FoodResponse addFood(FoodRequest request, MultipartFile file) {
       FoodEntity newFoodEntity = convertToEntity(request);
       String imageUrl = uploadFile(file);

       newFoodEntity.setImageUrl(imageUrl);
       newFoodEntity = foodRepository.save(newFoodEntity);

       return convertToResponse(newFoodEntity);
    }

    @Override
    public List<FoodResponse> readFoods() {
        List<FoodEntity> databaseEntries = foodRepository.findAll();
       return databaseEntries.stream().map(object -> convertToResponse(object)).collect(Collectors.toList());
    }

    @Override
    public FoodResponse readFoodBy(String id) {
       FoodEntity databaseEntity = foodRepository.findById(id).orElseThrow(()-> new RuntimeException("Found not found by the the id:"+ id));
       return  convertToResponse(databaseEntity);
    }

    @Override
    public boolean deleteFile(String imageUrl) {
        try {
            String publicId;

            if (imageUrl.contains("cloudinary.com")) {
                // Example: https://res.cloudinary.com/domlst2zp/image/upload/v12345/foodie/uuid.jpg
                String afterUpload = imageUrl.substring(imageUrl.indexOf("upload/") + 7);
                afterUpload = afterUpload.substring(afterUpload.indexOf("/") + 1); // remove version (v12345/)

                // Strip extension (.jpg, .png, etc.)
                int dotIndex = afterUpload.lastIndexOf(".");
                if (dotIndex > 0) {
                    afterUpload = afterUpload.substring(0, dotIndex);
                }

                publicId = afterUpload; // full folder + filename without extension
            } else {
                publicId = imageUrl; // fallback if already a public_id
            }

            System.out.println("Trying to delete public_id: " + publicId);

            Map result = cloudinary.uploader().destroy(publicId, Map.of());
            String resultStatus = (String) result.get("result");

            System.out.println("Delete result: " + resultStatus);

            return "ok".equals(resultStatus);

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }


    @Override
    public String deleteFood(String id) {
        // 1. Fetch food entry by ID
        FoodResponse response = readFoodBy(id);
        if (response == null) {
            throw new RuntimeException("Food with ID " + id + " not found.");
        }

        String imageUrl = response.getImageUrl();

        // 2. Extract the public_id correctly from Cloudinary URL
        // Example URL: https://res.cloudinary.com/demo/image/upload/v1234567890/foodie/uuid.jpg
        String publicId = null;
        if (imageUrl != null && !imageUrl.isEmpty()) {
            // Take substring after `/upload/`
            int uploadIndex = imageUrl.indexOf("/upload/");
            if (uploadIndex != -1) {
                String pathAfterUpload = imageUrl.substring(uploadIndex + 8); // skip "/upload/"
                // Remove version (starts with v123456) if present
                if (pathAfterUpload.startsWith("v")) {
                    pathAfterUpload = pathAfterUpload.substring(pathAfterUpload.indexOf("/") + 1);
                }
                // Remove extension (.jpg, .png, etc.)
                publicId = pathAfterUpload.substring(0, pathAfterUpload.lastIndexOf("."));
            }
        }

        // 3. Try deleting file in Cloudinary
        boolean isFileDeleted = false;
        if (publicId != null) {
            isFileDeleted = deleteFile(publicId); // pass the clean public_id
        }

        // 4. Delete from DB regardless of Cloudinary deletion
        foodRepository.deleteById(response.getId());

        // 5. Logging
        if (isFileDeleted) {
            System.out.println("✅ File deleted from Cloudinary, DB record removed.");
        } else {
            System.out.println("⚠️ File not found in Cloudinary, but DB record removed.");
        }

        return "Food has been deleted. With id: " + id;
    }


    private FoodEntity convertToEntity(FoodRequest request){
        return FoodEntity.builder()
                .name(request.getName())
                .description(request.getDescription())
                .category(request.getCategory())
                .price(request.getPrice())
                .build();
    }

    private FoodResponse convertToResponse(FoodEntity entity){
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
