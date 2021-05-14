package com.myq.miaosha.service;

import com.myq.miaosha.entity.OrderInfo;
import com.myq.miaosha.entity.User;
import com.myq.miaosha.vo.GoodsVo;
import org.springframework.transaction.annotation.Transactional;

import java.awt.image.BufferedImage;
import java.util.Random;

/**
 * @author 孟赟强
 * @date 2021/5/9-2:43
 */
public interface SeckillService {

    //保证这三个操作，减库存 下订单 写入秒杀订单是一个事物
    @Transactional
    public OrderInfo seckill(User user, GoodsVo goods);

    public long getSeckillResult(long userId, long goodsId);

    public void setGoodsOver(Long goodsId);

    public boolean getGoodsOver(long goodsId);

    /**
     * 创建验证码
     *
     * @param user
     * @param goodsId
     * @return
     */
    public BufferedImage createVerifyCode(User user, long goodsId);

    /**
     * 生成验证码，只含有+/-/*
     * <p>
     * 随机生成三个数字，然后生成表达式
     *
     * @param rdm
     * @return 验证码中的数学表达式
     */
    public String generateVerifyCode(Random rdm);

    /**
     * 使用ScriptEngine计算验证码中的数学表达式的值
     *
     * @param exp
     * @return
     */
    public int calc(String exp);

    /**
     * 检验检验码的计算结果
     *
     * @param user
     * @param goodsId
     * @param verifyCode
     * @return
     */
    public boolean checkVerifyCode(User user, long goodsId, int verifyCode);

    /**
     * 秒杀地址生成
     * @param user
     * @param goodsId
     * @return
     */
    public String createPath(User user, long goodsId);

    /**
     * 秒杀地址验证
     * @param user
     * @param goodsId
     * @param path
     * @return
     */
    public boolean checkPath(User user, long goodsId,String path);
}
