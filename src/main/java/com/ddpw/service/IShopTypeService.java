package com.ddpw.service;

import com.ddpw.dto.Result;
import com.ddpw.entity.ShopType;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author zxq
 * @since 2023-5-12
 */
public interface IShopTypeService extends IService<ShopType> {

    Result queryTypeList();
}
