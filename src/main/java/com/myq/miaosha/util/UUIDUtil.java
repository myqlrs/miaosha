package com.myq.miaosha.util;

import java.util.UUID;

/**
 * @author 孟赟强
 * @date 2021/4/22.
 * <p>
 * 唯一id生成类
 */
public class UUIDUtil {

    public static String uuid() {
        return UUID.randomUUID().toString().replace("-", "");
    }

}
