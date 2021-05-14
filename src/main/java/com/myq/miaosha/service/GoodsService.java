package com.myq.miaosha.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.myq.miaosha.entity.Goods;
import com.myq.miaosha.vo.GoodsVo;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 孟赟强
 * @since 2021-05-05
 */
public interface GoodsService extends IService<Goods> {

    /**
     * 查询商品列表
     *
     * @return
     */
    public List<GoodsVo> listGoodsVo();

    /**
     * 根据id查询指定商品
     *
     * @return
     */
    public GoodsVo getGoodsVoByGoodsId(long goodsId);

    /**
     * 减少库存，每次减一
     *
     * @return
     */
    public boolean reduceStock(GoodsVo goods);

}
