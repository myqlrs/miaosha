package com.myq.miaosha.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;

/**
 * <p>
 *
 * </p>
 *
 * @author 孟赟强
 * @since 2021-05-05
 */
@TableName("sk_order")
public class Order implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long userId;

    private Long orderId;

    private Long goodsId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }
    public Long getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Long goodsId) {
        this.goodsId = goodsId;
    }

    @Override
    public String toString() {
        return "Order{" +
            "id=" + id +
            ", userId=" + userId +
            ", orderId=" + orderId +
            ", goodsId=" + goodsId +
        "}";
    }
}
