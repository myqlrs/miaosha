package com.myq.miaosha.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.myq.miaosha.entity.GoodsSeckill;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 孟赟强
 * @since 2021-05-05
 */
public interface GoodsSeckillService extends IService<GoodsSeckill> {

    /**
     * 根据id查询秒杀商品信息
     */
    public GoodsSeckill selectSeckillGoodsById(long goodsId);

}
