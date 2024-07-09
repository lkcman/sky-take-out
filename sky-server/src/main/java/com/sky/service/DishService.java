package com.sky.service;

import com.sky.dto.DishDTO;

public interface DishService {

    /**
     * add菜品
     * @param dishDTO
     */
    void save(DishDTO dishDTO);
}
