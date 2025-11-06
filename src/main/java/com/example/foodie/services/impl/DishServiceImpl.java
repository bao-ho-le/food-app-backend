package com.example.foodie.services.impl;

import com.example.foodie.dtos.DishDTO;
import com.example.foodie.models.Dish;
import com.example.foodie.models.Image;
import com.example.foodie.models.Review;
import com.example.foodie.models.Tag;
import com.example.foodie.repos.DishRepository;
import com.example.foodie.repos.ImageRepository;
import com.example.foodie.repos.ReviewRepository;
import com.example.foodie.repos.TagRepository;
import com.example.foodie.services.interfaces.DishService;
import com.example.foodie.services.interfaces.ImageService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class DishServiceImpl implements DishService {

    private DishRepository dishRepository;
    private TagRepository tagRepository;
    private ReviewRepository reviewRepository;
    private ImageRepository imageRepository;


    @Override
    public List<DishDTO> getAllDishes() {
        List<Dish> dishes = dishRepository.findAll();

        if (dishes.isEmpty()) {
            throw new RuntimeException("Không có món nào");
        }

        return dishes.stream()
                .map(dish -> {
                    // Lấy tag của món theo id
                    List<Tag> listTag = getAllTags(dish.getId());

                    // Lấy tất cả review của món và tính rating trung bình
                    Float avgRatingObj = reviewRepository.findAverageRatingByDishId(dish.getId());
                    float avgRating = avgRatingObj != null ? avgRatingObj : 0f; // nếu null thì mặc định 0

                    Image image = imageRepository.findByDish_Id(dish.getId()).stream()
                            .findFirst()
                            .orElse(null);

                    String url = "";
                    if (image == null) {
                        System.out.println("⚠️ Không có ảnh cho món: " + dish.getName());
                    } else{
                        url = image.getUrl();
                    }


                    return DishDTO.builder()
                            .id(dish.getId())
                            .name(dish.getName())
                            .price(dish.getPrice())
                            .isAvailable(dish.isAvailable()) // builder method đúng kiểu Boolean
                            .restaurant(dish.getRestaurant())
                            .rating(avgRating)
                            .tags(listTag)
                            .url(url)
                            .build();
                })
                .toList();

    }



    @Override
    public List<Tag> getAllTags(Integer dishId){

        List<Tag> listTag = tagRepository.findTagsByDishId(dishId);

        return listTag;
    }

    @Override
    public Dish createDish(Dish dish){
        Dish newDish = Dish.builder()
                .name(dish.getName())
                .price(dish.getPrice())
                .restaurant(dish.getRestaurant())
                .build();

        return dishRepository.save(newDish);
    }
}
