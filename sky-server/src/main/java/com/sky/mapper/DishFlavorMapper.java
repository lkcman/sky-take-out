package com.sky.mapper;

import com.sky.annotation.AutoFill;
import com.sky.entity.DishFlavor;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DishFlavorMapper {

//@AutoFill(OperationType.INSERT)

    void insert(List<DishFlavor> flavors);

@Delete("delete from dish_flavor where dish_id = #{dishId}")
    void deleteByDishId(Long id);

@Select("select  * from dish_flavor where dish_id=#{dishId}")
    List<DishFlavor> selectByDishId(Long id);
}
