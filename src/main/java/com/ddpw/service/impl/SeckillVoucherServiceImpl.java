package com.ddpw.service.impl;

import com.ddpw.entity.SeckillVoucher;
import com.ddpw.mapper.SeckillVoucherMapper;
import com.ddpw.service.ISeckillVoucherService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 秒杀优惠券表，与优惠券是一对一关系 服务实现类
 * </p>
 *
 * @author zxq
 * @since 2023-11-2
 */
@Service
public class SeckillVoucherServiceImpl extends ServiceImpl<SeckillVoucherMapper, SeckillVoucher> implements ISeckillVoucherService {

}
