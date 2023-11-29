package com.ddpw.service.impl;

import com.ddpw.entity.UserInfo;
import com.ddpw.mapper.UserInfoMapper;
import com.ddpw.service.IUserInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author zxq
 * @since 2023-11-2
 */
@Service
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo> implements IUserInfoService {

}
