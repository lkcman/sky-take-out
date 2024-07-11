package com.sky.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.sky.constant.JwtClaimsConstant;
import com.sky.constant.MessageConstant;
import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;
import com.sky.exception.LoginFailedException;
import com.sky.mapper.UserMapper;
import com.sky.properties.JwtProperties;
import com.sky.properties.WeChatProperties;
import com.sky.service.UserService;
import com.sky.utils.HttpClientUtil;
import com.sky.utils.JwtUtil;
import com.sky.vo.UserLoginVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

private  static  final  String WECHAT_LOGIN_URL = "https://api.weixin.qq.com/sns/jscode2session";
@Autowired
private WeChatProperties WeChatProperties;
@Autowired
private UserMapper userMapper;
    @Autowired
    private JwtProperties jwtProperties;
    /**
     * 用户登录
     *
     * @param userLoginDTO
     * @return
     */
    @Override
    public UserLoginVO login(UserLoginDTO userLoginDTO) {
        //发送请求给微信，获取openid
       String openId =getOpenId(userLoginDTO.getCode());
       if (ObjectUtils.isEmpty(openId)) {
           throw new LoginFailedException(MessageConstant.LOGIN_FAILED);
       }
        User user=userMapper.getByOpenId(openId);
       if (null==user){
           user=User.builder().openid(openId).createTime(LocalDateTime.now()).build();
           userMapper.insert(user);
       }
        //为微信用户生成jwt令牌
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.USER_ID,user.getId());

        String token = JwtUtil.createJWT(jwtProperties.getUserSecretKey(), jwtProperties.getUserTtl(), claims);
       UserLoginVO vo= UserLoginVO.builder().id(user.getId()).openid(openId).token(token)
                .build();
        return vo;
    }

    private String getOpenId(String code) {
        Map<String, String> paramMap=new HashMap<>();
        paramMap.put("appid",WeChatProperties.getAppid());
        paramMap.put("secret",WeChatProperties.getSecret());
        paramMap.put("js_code",code);
        paramMap.put("grant_type","authorization_code");
        //发送请求，获取JSON数据
        String resultString = Optional.ofNullable(HttpClientUtil.doGet(UserServiceImpl.WECHAT_LOGIN_URL,paramMap))
                .orElseThrow(()->new LoginFailedException(MessageConstant.LOGIN_FAILED));

        JSONObject jsonObject = JSONObject.parseObject(resultString);
        String errcode = jsonObject.getString("errcode");
        if (null!=errcode&&!"0".equals(errcode)) {
            throw  new LoginFailedException(MessageConstant.LOGIN_FAILED);
        }
        String openid = jsonObject.getString("openid");
        return openid;


    }


}
