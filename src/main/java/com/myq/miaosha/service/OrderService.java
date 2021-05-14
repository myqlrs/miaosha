package com.myq.miaosha.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.myq.miaosha.entity.Order;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 孟赟强
 * @since 2021-05-05
 */
public interface OrderService extends IService<Order> {

    public Order getOrderByUserIdGoodsId(long userId, long goodsId);

}
