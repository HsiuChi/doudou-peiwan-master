package com.ddpw.mq;

import com.ddpw.entity.VoucherOrder;
import com.ddpw.utils.MQConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * RabbitMQ 消息发送器
 *
 * @author zxq
 * @version 1.0
 * @ClassName MqSender
 * @since 2023/9/18 10:21
 */
@Slf4j
@Component
public class MqSender {

    @Autowired
    RabbitTemplate rabbitTemplate;


    /**
     * 发送秒杀订单消息，这里需要保证可靠传递性，失败重传，消息发送到队列失败，进行消息回退
     *
     * @param voucherOrder 秒杀订单信息
     * @param reliable     是否保证可靠传输模式
     */
    public void sendSeckillMessage(VoucherOrder voucherOrder, boolean reliable) {
        log.info("发送消息：" + voucherOrder);
        if (reliable) {
            // 定义确认回调机制，当 publisher 将消息发送到 exchange 失败，则重新发送一次
            rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
                if (!ack) { // 如果发送失败，则重新再次发送
                    log.error("消息发送失败，错误原因：{}，再次发送。", cause);
                    // 重新发送消息
                    rabbitTemplate.convertAndSend(MQConstants.SECKILL_EXCHANGE, MQConstants.SECKILL_ROUTING_KEY,
                            voucherOrder); //, new CorrelationData(voucherOrder.getId().toString())
                }
            });
            // 设置交换机处理消息的模式
            rabbitTemplate.setMandatory(true);
            // 设置退回函数，当 exchange 将消息发送到队列失败时，会自动将消息退回给 publisher
            rabbitTemplate.setReturnCallback((message, replyCode, replyText, exchange, routingKey) ->
                    log.error("交换机发送消息到队列失败，错误原因：{}，执行将消息退回到 publisher 操作。", replyText));
        }
        // 发送消息（默认为消息持久化）
        rabbitTemplate.convertAndSend(MQConstants.SECKILL_EXCHANGE, MQConstants.SECKILL_ROUTING_KEY,
                voucherOrder); // , new CorrelationData(voucherOrder.getId().toString())
    }

}
