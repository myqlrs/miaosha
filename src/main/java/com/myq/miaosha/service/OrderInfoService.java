package com.myq.miaosha.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.myq.miaosha.entity.OrderInfo;
import com.myq.miaosha.entity.User;
import com.myq.miaosha.vo.GoodsVo;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 孟赟强
 * @since 2021-05-05
 */
public interface OrderInfoService extends IService<OrderInfo> {

    public OrderInfo getOrderById(long orderId);

    /**
     * 因为要同时分别在订单详情表和秒杀订单表都新增一条数据，所以要保证两个操作是一个事物
     */
    @Transactional
    public OrderInfo createOrder(User user, GoodsVo goods);

}
