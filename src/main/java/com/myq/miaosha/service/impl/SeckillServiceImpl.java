package com.myq.miaosha.service.impl;

import com.myq.miaosha.entity.*;
import com.myq.miaosha.redis.RedisService;
import com.myq.miaosha.redis.SeckillKey;
import com.myq.miaosha.service.*;
import com.myq.miaosha.util.MD5Util;
import com.myq.miaosha.util.UUIDUtil;
import com.myq.miaosha.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

/**
 * @author 孟赟强
 * @date 2021/5/9-2:44
 */
@Service
public class SeckillServiceImpl implements SeckillService {

    @Autowired
    GoodsService goodsService;

    @Autowired
    OrderService orderService;

    @Autowired
    OrderInfoService orderInfoService;

    @Autowired
    RedisService redisService;

    // 用于生成验证码中的运算符
    private  char[] ops = new char[]{'+', '-', '*'};

    //保证这三个操作，减库存 下订单 写入秒杀订单是一个事物
    @Transactional
    public OrderInfo seckill(User user, GoodsVo goods){
        //减库存
        boolean success = goodsService.reduceStock(goods);
        if (success){
            //下订单 写入秒杀订单
            return orderInfoService.createOrder(user, goods);
        }else {
            setGoodsOver(goods.getId());
            return null;
        }
    }

    public long getSeckillResult(long userId, long goodsId){
        Order order = orderService.getOrderByUserIdGoodsId(userId, goodsId);
        if (order != null){
            return order.getOrderId();
        }else{
            boolean isOver = getGoodsOver(goodsId);
            if(isOver) {
                return -1;
            }else {
                return 0;
            }
        }
    }

    public void setGoodsOver(Long goodsId) {
        redisService.set(SeckillKey.isGoodsOver, ""+goodsId, true);
    }

    public boolean getGoodsOver(long goodsId) {
        return redisService.exists(SeckillKey.isGoodsOver, ""+goodsId);
    }

    /**
     * 创建验证码
     *
     * @param user
     * @param goodsId
     * @return
     */
    public BufferedImage createVerifyCode(User user, long goodsId) {

        if (user == null || goodsId <= 0) {
            return null;
        }

        // 验证码的宽高
        int width = 80;
        int height = 32;

        //create the image
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics g = image.getGraphics();
        // set the background color
        g.setColor(new Color(0xDCDCDC));
        g.fillRect(0, 0, width, height);
        // draw the border
        g.setColor(Color.black);
        g.drawRect(0, 0, width - 1, height - 1);
        // create a random instance to generate the codes
        Random rdm = new Random();
        // make some confusion
        for (int i = 0; i < 50; i++) {
            int x = rdm.nextInt(width);
            int y = rdm.nextInt(height);
            g.drawOval(x, y, 0, 0);
        }
        // generate a random code
        String verifyCode = generateVerifyCode(rdm);
        g.setColor(new Color(0, 100, 0));
        g.setFont(new Font("Candara", Font.BOLD, 24));
        g.drawString(verifyCode, 8, 24);
        g.dispose();

        // 计算表达式值，并把把验证码值存到redis中
        int expResult = calc(verifyCode);
        redisService.set(SeckillKey.seckillVerifyCode, user.getId() + "," + goodsId, expResult);
        //输出图片
        return image;
    }

    /**
     * 生成验证码，只含有+/-/*
     * <p>
     * 随机生成三个数字，然后生成表达式
     *
     * @param rdm
     * @return 验证码中的数学表达式
     */
    public String generateVerifyCode(Random rdm) {
        int num1 = rdm.nextInt(10);
        int num2 = rdm.nextInt(10);
        int num3 = rdm.nextInt(10);
        char op1 = ops[rdm.nextInt(3)];
        char op2 = ops[rdm.nextInt(3)];
        String exp = "" + num1 + op1 + num2 + op2 + num3;
        return exp;
    }

    /**
     * 使用ScriptEngine计算验证码中的数学表达式的值
     *
     * @param exp
     * @return
     */
    public int calc(String exp) {
        try {
            ScriptEngineManager manager = new ScriptEngineManager();
            ScriptEngine engine = manager.getEngineByName("JavaScript");
            return (Integer) engine.eval(exp);// 表达式计算
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 检验检验码的计算结果
     *
     * @param user
     * @param goodsId
     * @param verifyCode
     * @return
     */
    public boolean checkVerifyCode(User user, long goodsId, int verifyCode) {
        if (user == null || goodsId <= 0) {
            return false;
        }

        // 从redis中获取验证码计算结果
        Integer oldCode = redisService.get(SeckillKey.seckillVerifyCode, user.getId() + "," + goodsId, Integer.class);
        if (oldCode == null || oldCode - verifyCode != 0) {// !!!!!!
            return false;
        }

        // 如果校验不成功，则说明校验码过期
        redisService.delete(SeckillKey.seckillVerifyCode, user.getId() + "," + goodsId);
        return true;
    }

    /**
     * 秒杀地址生成
     * @param user
     * @param goodsId
     * @return
     */
    public String createPath(User user, long goodsId) {
        String str = MD5Util.md5(UUIDUtil.uuid() + "971203");
        redisService.set(SeckillKey.getSeckillPath,""+user.getId()+"_"+goodsId,str);
        return str;
    }

    /**
     * 秒杀地址验证
     * @param user
     * @param goodsId
     * @param path
     * @return
     */
    public boolean checkPath(User user, long goodsId,String path){
        if(user == null || path == null) {
            return false;
        }
        String pathOld = redisService.get(SeckillKey.getSeckillPath, ""+user.getId() + "_"+ goodsId, String.class);
        return path.equals(pathOld);
    }
}
