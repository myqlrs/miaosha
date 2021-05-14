package com.myq.miaosha.vo;

import com.myq.miaosha.entity.OrderInfo;

/**
 * @author 孟赟强
 * @date 2021/4/28.
 */
public class OrderDetailVo {
    private GoodsVo goods;
    private OrderInfo order;
    public GoodsVo getGoods() {
        return goods;
    }
    public void setGoods(GoodsVo goods) {
        this.goods = goods;
    }
    public OrderInfo getOrder() {
        return order;
    }
    public void setOrder(OrderInfo order) {
        this.order = order;
    }
}
