package com.sky.mapper;

import com.sky.entity.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {
    //查询
@Select("select  * from  user where openid=#{openId} ")
    User getByOpenId(String openId);
//新增
    void insert(User user);
}
