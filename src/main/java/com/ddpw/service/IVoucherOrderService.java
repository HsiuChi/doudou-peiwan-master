package com.ddpw.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ddpw.dto.Result;
import com.ddpw.entity.VoucherOrder;

/**
 * <p>
 *  优惠券订单服务类接口
 * </p>
 *
 * @author zxq
 * @since 2021-5-22
 */
public interface IVoucherOrderService extends IService<VoucherOrder> {

    Result seckillVoucher(Long voucherId);

    void createVoucherOrder(VoucherOrder voucherOrder);

}
