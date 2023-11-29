package com.ddpw.controller;


import com.ddpw.dto.Result;
import com.ddpw.service.IShopTypeService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 店铺类型管理
 *
 * @author zxq
 * @since 2023-11-2
 */
@RestController
@RequestMapping("/shop-type")
public class ShopTypeController {
    @Resource
    private IShopTypeService typeService;

    @GetMapping("list")
    public Result queryTypeList() {
        // 直接访问数据库写法
        /*List<ShopType> typeList = typeService
                .query().orderByAsc("sort").list();
        return Result.ok(typeList);*/

        return typeService.queryTypeList();
    }
}
