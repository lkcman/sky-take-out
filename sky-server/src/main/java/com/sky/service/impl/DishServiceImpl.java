package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import org.aspectj.bridge.Message;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

@Service
public class DishServiceImpl implements DishService {


    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private DishFlavorMapper dishFlavorMapper;
    @Autowired
    private SetmealDishMapper setmealDishMapper;
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
                 dishFlavorMapper.insert(flavors);
             }
         }


    }

    @Override
    public PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO) {
        PageHelper.startPage(dishPageQueryDTO.getPage(),dishPageQueryDTO.getPageSize());
        Page<DishVO> page =  dishMapper.selectPage(dishPageQueryDTO);
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 批量删除菜品
     * @param ids
     */
    @Override
    @Transactional
    public void deleteBath(List<Long> ids) {
        if (!CollectionUtils.isEmpty(ids)){
            for (Long id : ids){
                Dish dish=dishMapper.getById(id);
                if (dish.getStatus()==StatusConstant.DISABLE){
                    throw  new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
                }
            }

            List<Long> setmealIds=setmealDishMapper.getSetmealIdsByDishIds(ids);
            if (!CollectionUtils.isEmpty(setmealIds)){
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            }
            for (Long id : ids) {
                dishMapper.deleteById(id);
                dishFlavorMapper.deleteByDishId(id);

            }
        }

    }

    /**
     * id查询菜品
     * @param id
     * @return
     */
    @Override
    public DishVO getByIdWithFlavor(Long id) {
        Dish dish=dishMapper.getById(id);
        List<DishFlavor> flavorList=dishFlavorMapper.selectByDishId(id);
        DishVO build = DishVO.builder().flavors(flavorList).build();
        BeanUtils.copyProperties(dish,build);
        return build;
    }
@Transactional
    @Override
    public void update(DishDTO dishDTO) {
    Dish dish = new Dish();
    BeanUtils.copyProperties(dishDTO, dish);

    //修改菜品表基本信息
    dishMapper.update(dish);

    //删除原有的口味数据
    dishFlavorMapper.deleteByDishId(dishDTO.getId());

    //重新插入口味数据
    List<DishFlavor> flavors = dishDTO.getFlavors();
    if (flavors != null && flavors.size() > 0) {
        flavors.forEach(dishFlavor -> {
            dishFlavor.setDishId(dishDTO.getId());
        });
        //向口味表插入n条数据
        dishFlavorMapper.insert(flavors);
    }
        //删除以前口味信息
    }

    /**
     * 条件查询菜品和口味
     * @param dish
     * @return
     */
    public List<DishVO> listWithFlavor(Dish dish) {
        List<Dish> dishList = dishMapper.list(dish);

        List<DishVO> dishVOList = new ArrayList<>();

        for (Dish d : dishList) {
            DishVO dishVO = new DishVO();
            BeanUtils.copyProperties(d,dishVO);

            //根据菜品id查询对应的口味
            List<DishFlavor> flavors = dishFlavorMapper.selectByDishId(d.getId());

            dishVO.setFlavors(flavors);
            dishVOList.add(dishVO);
        }

        return dishVOList;
    }


}
