package com.myq.miaosha.redis;

/**
 * @author 孟赟强
 * @date 2021/4/29.
 */
public class OrderKey extends BasePrefix {

    public OrderKey(String prefix) {
        super(prefix);
    }
    public static OrderKey getSeckillOrderByUidGid = new OrderKey("seckill");
}
