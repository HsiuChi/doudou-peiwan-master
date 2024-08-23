package com.ddpw.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ddpw.dto.LoginFormDTO;
import com.ddpw.dto.Result;
import com.ddpw.entity.User;

import javax.servlet.http.HttpSession;

/**
 * <p>
 *  用户服务类
 * </p>
 *
 * @author zxq
 * @since 2023-5-12
 */
public interface IUserService extends IService<User> {

    Result sendCode(String phone, HttpSession session);

    Result login(LoginFormDTO loginForm, HttpSession session);

    Result sign();

    Result signCount();
}
