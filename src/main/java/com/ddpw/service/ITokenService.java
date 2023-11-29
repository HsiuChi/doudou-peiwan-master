package com.ddpw.service;

import org.springframework.stereotype.Service;

/**
 * Token 服务类接口
 *
 * @author Wuxy
 * @version 1.0
 * @ClassName TokenService
 * @since 2023/6/3 17:32
 */
@Service
public interface ITokenService {

    /**
     * 下单前获取令牌用户防重提交
     *
     * @return 返回生成的 token
     */
    String getOrderToken();

}
