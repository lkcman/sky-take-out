package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Service
public class DishServiceImpl implements DishService {


    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private DishFlavorMapper dishFlavorMapper;
    /**
     * add 菜品
     * @param dishDTO
     */
    @Override
    @Transactional//事物控制，if产生RuntimeException,则实现事物回滚
    public void save(DishDTO dishDTO) {

        Dish dish = new Dish();
        //复制属性
        BeanUtils.copyProperties(dishDTO, dish);
        //状态停售
        dish.setStatus(StatusConstant.DISABLE);
        dishMapper.insert(dish);
        Long id = dish.getId();
        List<DishFlavor> flavors = dishDTO.getFlavors();
         if (!CollectionUtils.isEmpty(flavors)){
             for (DishFlavor flavor : flavors) {
                 //设置菜品id12
                 flavor.setDishId(id);
                 dishFlavorMapper.insert(flavor);
             }
         }


    }

    @Override
    public PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO) {
        PageHelper.startPage(dishPageQueryDTO.getPage(),dishPageQueryDTO.getPageSize());
        Page<DishVO> page =  dishMapper.selectPage(dishPageQueryDTO);
        return new PageResult(page.getTotal(), page.getResult());
    }
}
