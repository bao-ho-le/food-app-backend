package com.example.foodie.services.interfaces;

import com.example.foodie.dtos.DishDTO;
import com.example.foodie.models.Dish;
import com.example.foodie.models.Tag;

import java.util.List;

public interface DishService {
    public List<DishDTO> getAllDishes();
    public Dish createDish(Dish dish);
    public List<Tag> getAllTags(Integer dishId);
}
