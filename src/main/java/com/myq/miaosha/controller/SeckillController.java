package com.myq.miaosha.controller;

import com.google.common.util.concurrent.RateLimiter;
import com.myq.miaosha.entity.Order;
import com.myq.miaosha.entity.User;
import com.myq.miaosha.rabbitmq.MQSender;
import com.myq.miaosha.rabbitmq.SeckillMessage;
import com.myq.miaosha.redis.GoodsKey;
import com.myq.miaosha.redis.RedisService;
import com.myq.miaosha.result.CodeMsg;
import com.myq.miaosha.result.Result;
import com.myq.miaosha.service.*;
import com.myq.miaosha.vo.GoodsVo;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author 孟赟强
 * @date 2021/4/22.
 */
@Controller
@RequestMapping("/seckill")
public class SeckillController implements InitializingBean {

    @Autowired
    GoodsService goodsService;

    @Autowired
    OrderService orderService;

    @Autowired
    SeckillService seckillService;

    @Autowired
    RedisService redisService;

    @Autowired
    MQSender sender;

    //基于令牌桶算法的限流实现类
    RateLimiter rateLimiter = RateLimiter.create(10);

    //做标记，判断该商品是否被处理过了
    private HashMap<Long, Boolean> localOverMap = new HashMap<Long, Boolean>();

    /**
     * GET POST
     * 1、GET幂等,服务端获取数据，无论调用多少次结果都一样
     * 2、POST，向服务端提交数据，不是幂等
     * <p>
     * 将同步下单改为异步下单
     *
     * @param model
     * @param user
     * @param goodsId
     * @return
     */
    @RequestMapping(value = "/do_seckill", method = RequestMethod.POST)
    @ResponseBody
    public Result<Integer> list(Model model, User user, @RequestParam("goodsId") long goodsId) {

        if (!rateLimiter.tryAcquire(1000, TimeUnit.MILLISECONDS)) {
            return  Result.error(CodeMsg.ACCESS_LIMIT_REACHED);
        }

        if (user == null) {
            return Result.error(CodeMsg.SESSION_ERROR);
        }
        model.addAttribute("user", user);
        //内存标记，减少redis访问
        boolean over = localOverMap.get(goodsId);
        if (over) {
            return Result.error(CodeMsg.SECKILL_OVER);
        }
        //预减库存
        long stock = redisService.decr(GoodsKey.getGoodsStock, "" + goodsId);//10
        if (stock < 0) {
            afterPropertiesSet();
            long stock2 = redisService.decr(GoodsKey.getGoodsStock, "" + goodsId);//10
            if(stock2 < 0){
                localOverMap.put(goodsId, true);
                return Result.error(CodeMsg.SECKILL_OVER);
            }
        }
        //判断重复秒杀
        Order order = orderService.getOrderByUserIdGoodsId(user.getId(), goodsId);
        if (order != null) {
            return Result.error(CodeMsg.REPEATE_SECKILL);
        }
        //入队
        SeckillMessage message = new SeckillMessage();
        message.setUser(user);
        message.setGoodsId(goodsId);
        sender.sendSeckillMessage(message);
        return Result.success(0);//排队中
    }

    /**
     * 系统初始化,将商品信息加载到redis和本地内存
     */
    @Override
    public void afterPropertiesSet() {
        List<GoodsVo> goodsVoList = goodsService.listGoodsVo();
        if (goodsVoList == null) {
            return;
        }
        for (GoodsVo goods : goodsVoList) {
            redisService.set(GoodsKey.getGoodsStock, "" + goods.getId(), goods.getStockCount());
            //初始化商品都是没有处理过的
            localOverMap.put(goods.getId(), false);
        }
    }

    /**
     * orderId：成功
     * -1：秒杀失败
     * 0： 排队中
     */
    @RequestMapping(value = "/result", method = RequestMethod.GET)
    @ResponseBody
    public Result<Long> seckillResult(Model model, User user,
                                      @RequestParam("goodsId") long goodsId) {
        model.addAttribute("user", user);
        if (user == null) {
            return Result.error(CodeMsg.SESSION_ERROR);
        }
        long orderId = seckillService.getSeckillResult(user.getId(), goodsId);
        return Result.success(orderId);
    }

    /**
     * goods_detail.htm: $("#verifyCodeImg").attr("src", "/seckill/verifyCode?goodsId=" + $("#goodsId").val());
     * 使用HttpServletResponse的输出流返回客户端异步获取的验证码（异步获取的代码如上所示）
     *
     * @param response
     * @param user
     * @param goodsId
     * @return
     */
    @RequestMapping(value = "/verifyCode", method = RequestMethod.GET)
    @ResponseBody
    public Result<String> getMiaoshaVerifyCode(HttpServletResponse response, User user,
                                               @RequestParam("goodsId") long goodsId) {
        if (user == null)
            return Result.error(CodeMsg.SESSION_ERROR);

        // 创建验证码
        try {
            BufferedImage image = seckillService.createVerifyCode(user, goodsId);
            ServletOutputStream out = response.getOutputStream();
            // 将图片写入到resp对象中
            ImageIO.write(image, "JPEG", out);
            out.close();
            out.flush();
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error(CodeMsg.SECKILL_FAIL);
        }
    }

    /**
     * 获取秒杀地址
     * @param user
     * @param goodsId
     * @return
     */
    @RequestMapping(value = "/path", method = RequestMethod.GET)
    @ResponseBody
    public Result<String> getSeckillPath(User user, @RequestParam("goodsId") long goodsId,@RequestParam(value = "verifyCode", defaultValue = "0") int verifyCode) {
        if (user == null) {
            return Result.error(CodeMsg.USER_NO_LOGIN);
        }
        // 校验验证码
        boolean check = seckillService.checkVerifyCode(user, goodsId, verifyCode);
        if (!check)
            return Result.error(CodeMsg.REQUEST_ILLEGAL);// 检验不通过，请求非法
        String path = seckillService.createPath(user, goodsId);
        return Result.success(path);
    }

    /**
     * 3.0版
     * QPS:1658
     * 线程：5000 * 10
     * 隐藏秒杀接口
     * */
    @RequestMapping(value="/{path}/seckill_mq",method = RequestMethod.POST)
    @ResponseBody
    public Result<Integer> list2(Model model, User user, @RequestParam("goodsId") long goodsId,
                                 @PathVariable("path") String path) {

        if (!rateLimiter.tryAcquire(1000, TimeUnit.MILLISECONDS)) {
            return  Result.error(CodeMsg.ACCESS_LIMIT_REACHED);
        }

        if (user == null) {
            return Result.error(CodeMsg.SESSION_ERROR);
        }

        //验证path
        boolean checkPath = seckillService.checkPath(user, goodsId, path);
        if(!checkPath) {
            return Result.error(CodeMsg.REQUEST_ILLEGAL);
        }

        model.addAttribute("user", user);
        //内存标记，减少redis访问
        boolean over = localOverMap.get(goodsId);
        if (over) {
            return Result.error(CodeMsg.SECKILL_OVER);
        }
        //预减库存
        long stock = redisService.decr(GoodsKey.getGoodsStock, "" + goodsId);//10
        if (stock < 0) {
            afterPropertiesSet();
            long stock2 = redisService.decr(GoodsKey.getGoodsStock, "" + goodsId);//10
            if(stock2 < 0){
                localOverMap.put(goodsId, true);
                return Result.error(CodeMsg.SECKILL_OVER);
            }
        }
        //判断重复秒杀
        Order order = orderService.getOrderByUserIdGoodsId(user.getId(), goodsId);
        if (order != null) {
            return Result.error(CodeMsg.REPEATE_SECKILL);
        }
        //入队
        SeckillMessage message = new SeckillMessage();
        message.setUser(user);
        message.setGoodsId(goodsId);
        sender.sendSeckillMessage(message);
        return Result.success(0);//排队中
    }
}
