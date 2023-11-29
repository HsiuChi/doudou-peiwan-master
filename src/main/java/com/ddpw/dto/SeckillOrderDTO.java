package com.ddpw.dto;

import lombok.Builder;
import lombok.Data;

/**
 * 秒杀订单基本信息实体类
 *
 * @author Wuxy
 * @version 1.0
 * @ClassName SeckillOrderDTO
 * @since 2023/1/14 10:41
 */
@Data
@Builder
public class SeckillOrderDTO {

    private Integer userId;

    private Integer voucherId;

    private Long orderId;

}
