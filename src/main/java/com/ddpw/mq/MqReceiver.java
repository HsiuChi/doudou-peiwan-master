package com.ddpw.mq;

import com.rabbitmq.client.Channel;
import com.ddpw.entity.VoucherOrder;
import com.ddpw.service.IVoucherOrderService;
import com.ddpw.utils.MQConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * RabbitMQ 消息接收器类
 *
 * @author zxq
 * @version 1.0
 * @ClassName MqReceiver
 * @since 2023/11/14 10:21
 */
@Slf4j
@RabbitListener(queues = MQConstants.SECKILL_QUEUE, ackMode = "MANUAL")
@Component
public class MqReceiver {

    @Autowired
    IVoucherOrderService voucherOrderService;

    private final ExecutorService threadPool = Executors.newFixedThreadPool(16);

    @RabbitHandler
    public void receiveSeckillOrder(@Payload VoucherOrder voucherOrder, Channel channel, Message message) {
        log.info("接收到的订单消息：" + voucherOrder);
        threadPool.submit(() -> {
            try {
                voucherOrderService.createVoucherOrder(voucherOrder);
            } catch (Exception e1) {
                log.warn("订单处理异常，重新尝试。");
                try {
                    voucherOrderService.createVoucherOrder(voucherOrder);
                } catch (Exception e2) {
                    log.error("订单处理失败：", e2);
                    // todo: 第二次处理失败，则更改 Redis 中的数据（也可以将消息放入异常订单数据库或队列中特殊处理）-如回滚库存等操作。。。
                }
            }
            // 手动确认消费完成
            try {
                channel.basicAck(message.getMessageProperties().getDeliveryTag(), true);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

}
