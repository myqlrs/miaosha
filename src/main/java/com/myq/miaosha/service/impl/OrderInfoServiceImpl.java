package com.myq.miaosha.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.myq.miaosha.entity.*;
import com.myq.miaosha.mapper.OrderInfoMapper;
import com.myq.miaosha.mapper.OrderMapper;
import com.myq.miaosha.redis.OrderKey;
import com.myq.miaosha.redis.RedisService;
import com.myq.miaosha.service.OrderInfoService;
import com.myq.miaosha.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 孟赟强
 * @since 2021-05-05
 */
@Service
public class OrderInfoServiceImpl extends ServiceImpl<OrderInfoMapper, OrderInfo> implements OrderInfoService {

    @Autowired
    OrderMapper orderMapper;

    @Autowired
    OrderInfoMapper orderInfoMapper;

    @Autowired
    RedisService redisService;

    public OrderInfo getOrderById(long orderId) {
        return orderInfoMapper.selectById(orderId);
    }

    /**
     * 因为要同时分别在订单详情表和秒杀订单表都新增一条数据，所以要保证两个操作是一个事物
     */
    @Transactional
    public OrderInfo createOrder(User user, GoodsVo goods) {
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setCreateDate(new Date());
        orderInfo.setDeliveryAddrId(0L);
        orderInfo.setGoodsCount(1);
        orderInfo.setGoodsId(goods.getId());
        orderInfo.setGoodsName(goods.getGoodsName());
        orderInfo.setGoodsPrice(goods.getGoodsPrice());
        orderInfo.setOrderChannel(1);
        orderInfo.setStatus(0);
        orderInfo.setUserId(user.getId());
        orderInfoMapper.insert(orderInfo);

        Order seckillOrder = new Order();
        seckillOrder.setGoodsId(goods.getId());
        seckillOrder.setOrderId(orderInfo.getId());
        seckillOrder.setUserId(user.getId());
        orderMapper.insert(seckillOrder);

        redisService.set(OrderKey.getSeckillOrderByUidGid, "" + user.getId() + "_" + goods.getId(), seckillOrder);

        return orderInfo;
    }

}
