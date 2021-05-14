package com.myq.miaosha.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.myq.miaosha.entity.Goods;
import com.myq.miaosha.entity.GoodsSeckill;
import com.myq.miaosha.mapper.GoodsMapper;
import com.myq.miaosha.service.GoodsService;
import com.myq.miaosha.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 孟赟强
 * @since 2021-05-05
 */
@Service
public class GoodsServiceImpl extends ServiceImpl<GoodsMapper, Goods> implements GoodsService {

    //乐观锁冲突最大重试次数
    private static final int DEFAULT_MAX_RETRIES = 5;

    @Autowired
    GoodsMapper goodsMapper;

    @Override
    public List<GoodsVo> listGoodsVo() {
        return goodsMapper.listGoodsVo();
    }

    @Override
    public GoodsVo getGoodsVoByGoodsId(long goodsId) {
        return goodsMapper.getGoodsVoByGoodsId(goodsId);
    }

    @Override
    public boolean reduceStock(GoodsVo goods) {
        int numAttempts = 0;
        int ret = 0;
        GoodsSeckill sg = new GoodsSeckill();
        sg.setGoodsId(goods.getId());
        sg.setVersion(goods.getVersion());
        do {
            numAttempts++;
            try {
                sg.setVersion(goodsMapper.getVersionByGoodsId(goods.getId()));
                ret = goodsMapper.reduceStockByVersion(sg);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (ret != 0)
                break;
        } while (numAttempts < DEFAULT_MAX_RETRIES);

        return ret > 0;
    }
}
