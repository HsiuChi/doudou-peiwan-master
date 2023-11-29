package com.xydp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xydp.entity.Voucher;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author zxq
 * @since 2023-11-2
 */
public interface VoucherMapper extends BaseMapper<Voucher> {

    /**
     * 根据店铺 ID 查询所有的优惠券
     *
     * @param shopId 店铺 ID
     * @return 优惠券列表 {@link List}<{@link Voucher}>
     */
    List<Voucher> queryVoucherOfShop(@Param("shopId") Long shopId);
}
