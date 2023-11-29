package com.xydp.service;

import com.xydp.dto.Result;
import com.xydp.entity.ShopType;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author zxq
 * @since 2023-11-12
 */
public interface IShopTypeService extends IService<ShopType> {

    Result queryTypeList();
}
