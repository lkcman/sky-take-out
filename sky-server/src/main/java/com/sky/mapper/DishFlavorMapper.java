package com.sky.mapper;

import com.sky.annotation.AutoFill;
import com.sky.entity.DishFlavor;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DishFlavorMapper {

//@AutoFill(OperationType.INSERT)

    void insert(DishFlavor flavor);
@Delete("delete from dish_flavor where dish_id = #{dishId}")
    void deleteByDishId(Long id);
}
