package com.xydp.controller;

import com.xydp.dto.Result;
import com.xydp.prevent.Prevent;
import com.xydp.service.IVoucherOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 订单管理
 *
 * @author zxq
 * @since 2023-11-2
 */
@RestController
@RequestMapping("/voucher-order")
public class VoucherOrderController {

    @Autowired
    private IVoucherOrderService voucherOrderService;

    /**
     * 秒杀下单功能
     *
     * @param voucherId 优惠券id
     * @return 执行响应结果
     */
    @PostMapping("seckill/{id}")
    @Prevent(value = 10, count = 5)
    public Result seckillVoucher(@PathVariable("id") Long voucherId) {
        return voucherOrderService.seckillVoucher(voucherId);
    }
}
