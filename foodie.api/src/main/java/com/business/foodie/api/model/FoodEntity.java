package com.business.foodie.api.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "foods")
public class FoodEntity {

    @Id
    private String id;
    private String name;
    private String description;
    private double price;
    private String category;
    private String imageUrl;

    // No-args constructor
//    public FoodEntity() {}
//
//    // All-args constructor
//    public FoodEntity(String id, String name, String description, double price, String category, String imageUrl) {
//        this.id = id;
//        this.name = name;
//        this.description = description;
//        this.price = price;
//        this.category = category;
//        this.imageUrl = imageUrl;
//    }
//
//    // Getters and Setters
//    public String getId() {
//        return id;
//    }
//
//    public void setId(String id) {
//        this.id = id;
//    }
//
//    public String getName() {
//        return name;
//    }
//
//    public void setName(String name) {
//        this.name = name;
//    }
//
//    public String getDescription() {
//        return description;
//    }
//
//    public void setDescription(String description) {
//        this.description = description;
//    }
//
//    public double getPrice() {
//        return price;
//    }
//
//    public void setPrice(double price) {
//        this.price = price;
//    }
//
//    public String getCategory() {
//        return category;
//    }
//
//    public void setCategory(String category) {
//        this.category = category;
//    }
//
//    public String getImageUrl() {
//        return imageUrl;
//    }
//
//    public void setImageUrl(String imageUrl) {
//        this.imageUrl = imageUrl;
//    }
//
//    // toString()
//    @Override
//    public String toString() {
//        return "FoodEntity{" +
//                "id='" + id + '\'' +
//                ", name='" + name + '\'' +
//                ", description='" + description + '\'' +
//                ", price=" + price +
//                ", category='" + category + '\'' +
//                ", imageUrl='" + imageUrl + '\'' +
//                '}';
//    }
//
//    // equals() and hashCode()
//    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (!(o instanceof FoodEntity)) return false;
//
//        FoodEntity that = (FoodEntity) o;
//
//        return id != null ? id.equals(that.id) : that.id == null;
//    }
//
//    @Override
//    public int hashCode() {
//        return id != null ? id.hashCode() : 0;
//    }
//
//    // -------- Builder Implementation --------
//    public static class Builder {
//        private String id;
//        private String name;
//        private String description;
//        private double price;
//        private String category;
//        private String imageUrl;
//
//        public Builder id(String id) {
//            this.id = id;
//            return this;
//        }
//
//        public Builder name(String name) {
//            this.name = name;
//            return this;
//        }
//
//        public Builder description(String description) {
//            this.description = description;
//            return this;
//        }
//
//        public Builder price(double price) {
//            this.price = price;
//            return this;
//        }
//
//        public Builder category(String category) {
//            this.category = category;
//            return this;
//        }
//
//        public Builder imageUrl(String imageUrl) {
//            this.imageUrl = imageUrl;
//            return this;
//        }
//
//        public FoodEntity build() {
//            return new FoodEntity(id, name, description, price, category, imageUrl);
//        }
//    }
//
//    // Static method to start builder
//    public static Builder builder() {
//        return new Builder();
//    }
}