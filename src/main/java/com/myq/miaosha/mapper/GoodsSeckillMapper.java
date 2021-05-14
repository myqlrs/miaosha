package com.myq.miaosha.mapper;
import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.myq.miaosha.entity.GoodsSeckill;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author 孟赟强
 * @since 2021-05-05
 */
public interface GoodsSeckillMapper extends BaseMapper<GoodsSeckill> {

    GoodsSeckill selectOneByGoodsId(@Param("goodsId")Long goodsId);
}
