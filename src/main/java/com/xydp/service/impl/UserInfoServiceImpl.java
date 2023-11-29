package com.xydp.service.impl;

import com.xydp.entity.UserInfo;
import com.xydp.mapper.UserInfoMapper;
import com.xydp.service.IUserInfoService;
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
