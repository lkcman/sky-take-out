package com.sky.mapper;

import com.sky.annotation.AutoFill;
import com.sky.entity.DishFlavor;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DishFlavorMapper {

//@AutoFill(OperationType.INSERT)

    void insert(DishFlavor flavor);
}
