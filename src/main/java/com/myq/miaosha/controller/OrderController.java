package com.myq.miaosha.controller;


import com.myq.miaosha.entity.OrderInfo;
import com.myq.miaosha.entity.User;
import com.myq.miaosha.redis.RedisService;
import com.myq.miaosha.result.CodeMsg;
import com.myq.miaosha.result.Result;
import com.myq.miaosha.service.*;
import com.myq.miaosha.vo.GoodsVo;
import com.myq.miaosha.vo.OrderDetailVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author 孟赟强
 * @since 2021-05-05
 */
@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    UserService userService;

    @Autowired
    RedisService redisService;

    @Autowired
    OrderService orderService;

    @Autowired
    OrderInfoService orderInfoService;

    @Autowired
    GoodsService goodsService;

    @RequestMapping("/detail")
    @ResponseBody
    public Result<OrderDetailVo> info(Model model, User user,
                                      @RequestParam("orderId") long orderId) {
        if(user == null) {
            return Result.error(CodeMsg.SESSION_ERROR);
        }
        OrderInfo order = orderInfoService.getOrderById(orderId);
        if(order == null) {
            return Result.error(CodeMsg.ORDER_NOT_EXIST);
        }
        long goodsId = order.getGoodsId();
        GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
        OrderDetailVo vo = new OrderDetailVo();
        vo.setOrder(order);
        vo.setGoods(goods);
        return Result.success(vo);
    }

}
