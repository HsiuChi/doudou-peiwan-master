package com.ddpw.config;

import com.ddpw.utils.MQConstants;
import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 消息队列配置类：
 * <p>
 * 实际上，RabbitMQ 支持两种模式：
 *     <ol>
 *         <li>工作队列模式：即只有 Provider、Consumer 和 Queue，此时绑定的是默认的 Exchange </li>
 *         <li>
 *             Pub/Sub模式：即有 Provider、Consumer、Queue 和 Exchange，其中又分为：
 *             <ul>
 *                 <li>Fanout模式：将消息队列与交换机进行绑定（RoutingKey设为默认），Provider 将消息发送到 Exchange，然后 Exchange 会将消息发送到所有绑定的消息队列中。</li>
 *                 <li>Direct模式：将消息队列与交换机进行绑定并指定 RoutingKey，Provider 将消息发送到 Exchange，然后 Exchange 会将消息发送到所有指定 RoutingKey 的消息队列中。</li>
 *                 <li>Topic模式：将消息队列与交换机进行绑定并指定 RoutingKey（可以使用通配符），Provider 将消息发送到 Exchange，然后 Exchange 会将消息发送到所有符合 RoutingKey 规则的消息队列中。</li>
 *             </ul>
 *         </li>
 *     </ol>
 *     本质上都一样，主要角色都有 Provider、Consumer、Queue、Exchange、RoutingKey。
 * </p>
 *
 * @author zxq
 * @version 1.0
 * @ClassName MQConfig
 * @since 2023/4/13 23:42
 */
@Configuration
public class MQConfig {

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public Queue seckillQueue() {
        return QueueBuilder.durable(MQConstants.SECKILL_QUEUE).build();
    }

    @Bean
    public Exchange seckillExchange() {
        return ExchangeBuilder.directExchange(MQConstants.SECKILL_EXCHANGE).durable(true).build();
    }

    @Bean
    public Binding bindingSeckill(Queue seckillQueue, Exchange seckillExchange) {
        return BindingBuilder.bind(seckillQueue).to(seckillExchange).with(MQConstants.SECKILL_ROUTING_KEY).noargs();
    }

}
