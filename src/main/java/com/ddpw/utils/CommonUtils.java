package com.ddpw.utils;

import java.util.Random;

/**
 * 通用工具类
 *
 * @author Wuxy
 * @version 1.0
 * @ClassName CommonUtils
 * @since 2023/6/3 17:27
 */
public class CommonUtils {
    private static final String ALL_CHAR_NUM = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";


    /**
     * 获取指定长度的随机串
     *
     * @param length 指定长度
     * @return 返回指定长度的随机字符串
     */
    public static String getStringNumRandom(int length) {
        // 生成随机数字和字母
        Random random = new Random();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(ALL_CHAR_NUM.charAt(random.nextInt(ALL_CHAR_NUM.length())));
        }
        return sb.toString();
    }
}
