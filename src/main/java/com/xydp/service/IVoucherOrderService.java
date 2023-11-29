package com.xydp.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xydp.dto.Result;
import com.xydp.entity.VoucherOrder;

/**
 * <p>
 *  优惠券订单服务类接口
 * </p>
 *
 * @author Wuxy
 * @since 2021-12-22
 */
public interface IVoucherOrderService extends IService<VoucherOrder> {

    Result seckillVoucher(Long voucherId);

    void createVoucherOrder(VoucherOrder voucherOrder);

}
