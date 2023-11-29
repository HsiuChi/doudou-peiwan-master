package com.xydp.service.impl;

import com.xydp.service.ITokenService;
import com.xydp.utils.CommonUtils;
import com.xydp.utils.RedisConstants;
import com.xydp.utils.UserHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.concurrent.TimeUnit;

/**
 * @author Wuxy
 * @version 1.0
 * @ClassName TokenServiceImpl
 * @since 2023/6/3 17:34
 */
public class ITokenServiceImpl implements ITokenService {
    @Autowired
    private StringRedisTemplate redisTemplate;


    @Override
    public String getOrderToken() {
        // 获取登录用户账号
        Long userId = UserHolder.getUser().getId();
        // 随机获取32位的 数字+字母 作为token
        String token = CommonUtils.getStringNumRandom(32);
        // key的组成
        String key = String.format(RedisConstants.SUBMIT_ORDER_TOKEN_KEY, userId, token);
        // 设置防重令牌的有效时间是 30 分钟
        redisTemplate.opsForValue().set(key, String.valueOf(Thread.currentThread().getId()), 30, TimeUnit.MINUTES);

        return token;
    }
}
