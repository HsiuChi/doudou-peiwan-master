package com.ddpw.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ddpw.dto.Result;
import com.ddpw.entity.VoucherOrder;
import com.ddpw.mapper.VoucherOrderMapper;
import com.ddpw.mq.MqSender;
import com.ddpw.service.ISeckillVoucherService;
import com.ddpw.service.IVoucherOrderService;
import com.ddpw.utils.RedisIdWorker;
import com.ddpw.utils.UserHolder;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * <p>
 * 优惠券订单服务实现类
 * </p>
 *
 * @author Wuxy
 * @since 2023/1/12
 */
@Slf4j
@Service
public class VoucherOrderServiceImpl extends ServiceImpl<VoucherOrderMapper, VoucherOrder> implements IVoucherOrderService {
    @Resource
    private ISeckillVoucherService seckillVoucherService;

    @Resource
    private RedisIdWorker redisIdWorker;

    @Resource
    private RedissonClient redissonClient;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private MqSender mqSender;

    private IVoucherOrderService proxy;

    private static final DefaultRedisScript<Long> SECKILL_SCRIPT;

    static {
        SECKILL_SCRIPT = new DefaultRedisScript<>();
        SECKILL_SCRIPT.setLocation(new ClassPathResource("seckill.lua"));
        SECKILL_SCRIPT.setResultType(Long.class);
    }

    @Deprecated
    private final ExecutorService SECKILL_ORDER_EXECUTOR = Executors.newSingleThreadExecutor();

    @PostConstruct
    private void init() {
//        SECKILL_ORDER_EXECUTOR.submit(new VoucherOrderHandler());
    }

    // =========================== lua脚本 + 阻塞队列（改造成消息队列） =================================

    @Deprecated
    private final BlockingQueue<VoucherOrder> orderTasks = new ArrayBlockingQueue<>(1024 * 1024);


    /**
     * 实现秒杀优惠券下单（配合阻塞队列进行异步订单创建），需要提前将优惠券信息导入 Redis 中
     * <p>
     * 具体过程：使用lua脚本实现 -- 通过阻塞队列实现 异步下单，提高并发效率
     *     <ol>
     *     <li>先利用Redis完成库存余量、一人一单判断，完成抢单业务</li>
     *     <li>再将下单业务放入阻塞队列，利用独立线程异步下单</li>
     * </ol>
     * </p>
     */
    @Override
    public Result seckillVoucher(Long voucherId) {
        Long userId = UserHolder.getUser().getId();
        // 1.执行lua脚本
        Long result = stringRedisTemplate.execute(
                SECKILL_SCRIPT,
                Collections.emptyList(),
                voucherId.toString(),
                userId.toString(),
                Long.toString(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
        );
        int r = result == null ? 0 : result.intValue();
        // 2.判断结果是否为0
        if (r != 0) {
            // 2.1 不为0，代表没有购买资格
            String errorMsg = r == 1 ? "秒杀尚未开始！" : r == 2 ? "秒杀已经结束！" : r == 3 ? "库存不足！" : "不能重复下单！";
            log.warn(errorMsg);
            return Result.fail(errorMsg);
        }
        // 2.2 为0，有购买资格，把下单信息保存到阻塞队列中
        // 2.3 订单id
        long orderId = redisIdWorker.nextId("order");
        VoucherOrder voucherOrder = new VoucherOrder();
        voucherOrder.setId(orderId);
        // 2.4 用户id
        voucherOrder.setUserId(userId);
        // 2.5 代金券id
        voucherOrder.setVoucherId(voucherId);

////        // 保存阻塞队列 -- 通过线程池 异步执行 数据库读写任务
//        orderTasks.add(voucherOrder);
////        // 获取当前类代理对象并赋值
//        proxy = (IVoucherOrderService) AopContext.currentProxy();

        // 发送到消息队列
        mqSender.sendSeckillMessage(voucherOrder, false);

        // 3.返回订单id
        return Result.ok(voucherId);
    }

    @Deprecated
    private class VoucherOrderHandler implements Runnable {

        @Override
        public void run() {
            while (true) {
                try {
                    // 1.获取队列中的订单信息
                    VoucherOrder voucherOrder = orderTasks.take();
                    // 2.创建订单
                    handleVoucherOrder(voucherOrder);
                } catch (Exception e) {
                    log.error("处理订单异常", e);
                }
            }
        }
    }

    @Deprecated
    private void handleVoucherOrder(VoucherOrder voucherOrder) {
        // 1.获取用户
        Long userId = voucherOrder.getUserId();
        // 2.创建锁对象
        RLock redisLock = redissonClient.getLock("lock:order:" + userId);
        // 3.获取锁
        boolean isLock = redisLock.tryLock();
        // 4.判断获取锁是否成功
        if (!isLock) {
            // 获取锁失败，直接放回失败或者重试
            log.error("不允许重复下单！");
            return;
        }
        try {
            proxy.createVoucherOrder(voucherOrder);
        } finally {
            // 释放锁
            redisLock.unlock();
        }
    }

    @Transactional
    @Override
    public void createVoucherOrder(VoucherOrder voucherOrder) {
        Long userId = voucherOrder.getUserId();
        Long voucherId = voucherOrder.getVoucherId();
        // 创建锁对象
        RLock redisLock = redissonClient.getLock("order" + userId); // name 不应该和其他键同名
        boolean isLock = redisLock.tryLock();
        if (!isLock) {
            // 获取锁失败，直接放回失败或者重试
            log.error("不允许重复下单！");
            return;
        }
        try {
            // 5.1 查询订单
            Integer count = query().eq("user_id", userId).eq("voucher_id", voucherId).count();
            // 5.2 判断是否存在
            if (count > 0) {
                // 用户已经购买过了
                log.error("该用户已经购买过一次！");
                return;
            }
            // 6.扣减库存
            boolean success = seckillVoucherService.update()
                    .setSql("stock = stock - 1")
                    .eq("voucher_id", voucherId)
//                .eq("stock", voucher.getStock()) // CAS法实现乐观锁，解决并发问题（失败率高）
                    .gt("stock", 0) // CAS优化，只要库存大于0就可以秒杀成功
                    .update();

            if (!success) {
                // 扣减失败
                log.error("库存不足！");
                return;
            }
            // 保存订单
            save(voucherOrder);
        } finally {
            // 释放锁
            redisLock.unlock();
        }
    }
}
