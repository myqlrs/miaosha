package com.myq.miaosha.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.myq.miaosha.entity.Goods;
import com.myq.miaosha.entity.GoodsSeckill;
import com.myq.miaosha.vo.GoodsVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author 孟赟强
 * @since 2021-05-05
 */
public interface GoodsMapper extends BaseMapper<Goods> {

    List<GoodsVo> listGoodsVo();

    //根据id获取秒杀商品信息
    GoodsVo getGoodsVoByGoodsId(@Param("goodsId") long goodsId);

    // 获取最新版本号
    Integer getVersionByGoodsId(@Param("goodsId") Long goodsId);

    //stock_count > 0 和 版本号实现乐观锁 防止超卖
    int reduceStockByVersion(GoodsSeckill sg);
}
