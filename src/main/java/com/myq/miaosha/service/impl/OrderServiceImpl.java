package com.myq.miaosha.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.myq.miaosha.entity.Order;
import com.myq.miaosha.mapper.OrderMapper;
import com.myq.miaosha.redis.OrderKey;
import com.myq.miaosha.redis.RedisService;
import com.myq.miaosha.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 孟赟强
 * @since 2021-05-05
 */
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {
    @Autowired
    RedisService redisService;

    public Order getOrderByUserIdGoodsId(long userId, long goodsId) {
        return redisService.get(OrderKey.getSeckillOrderByUidGid, "" + userId + "_" + goodsId, Order.class);
    }

}
