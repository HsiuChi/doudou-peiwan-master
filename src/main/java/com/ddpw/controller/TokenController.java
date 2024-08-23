package com.ddpw.controller;

import com.ddpw.service.ITokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author zxq
 * @version 1.0
 * @ClassName TokenController
 * @since 2023/6/3 17:25
 */
@RestController
public class TokenController {
    @Autowired
    private ITokenService tokenService;


    @GetMapping("token")
    public String getOrderToken() {

        return tokenService.getOrderToken();
    }
}
