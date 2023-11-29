package com.xydp.prevent;

import cn.hutool.crypto.digest.MD5;
import com.xydp.exception.BusinessException;
import com.xydp.utils.IPUtils;
import com.xydp.utils.RedisConstants;
import com.xydp.utils.UserHolder;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static com.xydp.utils.RedisConstants.ACCESS_LIMIT_IP_KEY;
import static com.xydp.utils.RedisConstants.ACCESS_LIMIT_USER_KEY;

/**
 * 接口防刷切面处理实现类
 *
 * @author Wuxy
 * @version 1.0
 * @ClassName PreventAop
 * @since 2023/1/13 16:38
 */
@Aspect // 声明当前类是一个切面
@Component
@Slf4j
public class PreventAop {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private RedissonClient redissonClient;

    /**
     * 定义防刷切入点
     */
    @Pointcut("@annotation(com.xydp.prevent.Prevent)")
    public void pointcut() {

    }

    /**
     * 目标方法执行前的进行的逻辑
     */
    @Before("pointcut()")
    public void joinPoint(JoinPoint joinPoint) throws NoSuchMethodException {
        // 这里主要限制每一个 IP 和 User 在规定时间内的最大访问次数
        // ip 锁 和 user 锁
        RLock ipLock = null, userLock = null;
        boolean isIp = false, isUser = false;
        try {
            // 得到方法签名
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            // 得到具体方法
            Method method = joinPoint.getTarget().getClass().getMethod(signature.getName(), signature.getParameterTypes());

            // 得到 Prevent 注解
            Prevent prevent = method.getAnnotation(Prevent.class);

            // 获得过期时间
            long expire = prevent.value();
            // 获得限制次数
            int count = prevent.count();
            // 获得提示消息
            String message = prevent.message();

            // 获得 http 请求
            ServletRequestAttributes attributes = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes());
            assert attributes != null;
            HttpServletRequest request = attributes.getRequest();

            HttpServletResponse response = attributes.getResponse();
            assert response != null;
            response.setContentType("application/json;charset=UTF-8");

            // todo：以下操作可以使用 lua 脚本进行实现，从而保证操作的原子性以及降低网络开销
            // 得到限制 ip 的 key
            String ipKey = ACCESS_LIMIT_IP_KEY + IPUtils.getIpAddr(request) + "-" + request.getRequestURI();
            // 得到限制 user 的 key
            String userKey = ACCESS_LIMIT_USER_KEY + UserHolder.getUser().getId() + "-" + request.getRequestURI();

            // 获取 ip 锁
            ipLock = redissonClient.getLock("lock:" + ipKey);
            isIp = ipLock.tryLock();
            if (!isIp) { // User - 相同用户在 Redis 已经限制了
                throw new BusinessException("相同IP不能在同一时刻下单！");
            }
            // 获取 User 锁
            userLock = redissonClient.getLock("lock:" + userKey);
            isUser = userLock.tryLock();
            if (!isUser) {
                throw new BusinessException("相同用户不能在同一时刻下单！");
            }
            // 取得在限定时间内的访问次数
            String ipVal = stringRedisTemplate.opsForValue().get(ipKey);
            int ipCount = ipVal == null ? 0 : Integer.parseInt(ipVal);
            String userVal = stringRedisTemplate.opsForValue().get(userKey);
            int userCount = userVal == null ? 0 : Integer.parseInt(userVal);
            if (ipCount < count && userCount < count) {
                if (ipCount == 0) {
                    stringRedisTemplate.opsForValue().set(ipKey, Integer.toString(1), expire, TimeUnit.SECONDS);
                } else {
                    stringRedisTemplate.opsForValue().increment(ipKey, 1);
                }
                if (userCount == 0) {
                    stringRedisTemplate.opsForValue().set(userKey, Integer.toString(1), expire, TimeUnit.SECONDS);
                } else {
                    stringRedisTemplate.opsForValue().increment(userKey, 1);
                }
            } else {
                message = "".equals(message) ? "相同IP或用户在" + expire + "秒内达到了最大访问次数：" + count : message;
                throw new BusinessException(message);
            }
        } finally {
            // 去释放已经拿到的锁
            if (isIp) {
                ipLock.unlock();
            }
            if (isUser) {
                userLock.unlock();
            }
        }
    }

    /**
     * 定义防止重复提交切入点
     */
    @Pointcut("@annotation(com.xydp.prevent.RepeatSubmit)")
    public void repeatSubmitPointCut() {

    }

    /**
     * 回绕通知
     */
    @Around("repeatSubmitPointCut()")
    public Object around(ProceedingJoinPoint pjp) throws Throwable {
        // 获取当前 http request 对象
        HttpServletRequest request = ((ServletRequestAttributes)
                Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        // 获取当前登录用户id
        Long userId = UserHolder.getUser().getId();
        // 用于记录最终结果
        boolean result;
        // 得到方法签名
        MethodSignature signature = (MethodSignature) pjp.getSignature();
        // 得到具体方法
        Method method = pjp.getTarget().getClass().getMethod(signature.getName(), signature.getParameterTypes());

        // 得到 @RepeatSubmit 注解
        RepeatSubmit repeatSubmit = method.getAnnotation(RepeatSubmit.class);

        // 防重类型判断
        String type = repeatSubmit.limitType().name();
        if (type.equalsIgnoreCase(RepeatSubmit.Type.PARAM.name())) {
            // 方式一，参数形式防重提交
            long lockTime = repeatSubmit.lockTime();
            String ipAddr = IPUtils.getIpAddr(request);
            MethodSignature methodSignature = (MethodSignature) pjp.getSignature();
            method = methodSignature.getMethod();
            String className = method.getDeclaringClass().getName();
            String key = "order-server:repeat_submit:" + Arrays.toString(DigestUtils.md5Digest(String.format("%s-%s-%s-%s", ipAddr, className, method, userId).getBytes()));
            // 加锁

            // 这种也可以 setnx 的
            // result  = redisTemplate.opsForValue().setIfAbsent(key, "1", lockTime, TimeUnit.SECONDS);

            // 使用redisson
            RLock lock = redissonClient.getLock(key);
            // 尝试加锁，最多等待0秒，上锁以后5秒自动解锁 [lockTime默认为5s, 可以自定义]
            result = lock.tryLock(0, lockTime, TimeUnit.SECONDS);
        } else {
            // 方式二，令牌形式防重提交
            String requestToken = request.getHeader("request-token");
            if (StringUtils.isEmpty(requestToken)) {
                throw new BusinessException("token is blank!");
            }
            String key = String.format(RedisConstants.SUBMIT_ORDER_TOKEN_KEY, userId, requestToken);

            // 直接删除，删除成功则表示完成
            result = Boolean.TRUE.equals(stringRedisTemplate.delete(key));
        }

        if (!result) {
            log.error("请求重复提交");
            log.info("环绕通知中");
            return null;
        }

        // 执行方法
        return pjp.proceed();
    }
}
