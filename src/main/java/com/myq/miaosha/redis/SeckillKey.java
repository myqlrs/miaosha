package com.myq.miaosha.redis;

/**
 * @author 孟赟强
 * @date 2021/4/29.
 */
public class SeckillKey extends BasePrefix {

    private SeckillKey(String prefix, int i) {
        super(i,prefix);
    }

    public static SeckillKey isGoodsOver = new SeckillKey("go", 60);
    public static SeckillKey getSeckillPath = new SeckillKey("sp",60);
    // 验证码5分钟有效
    public static SeckillKey seckillVerifyCode = new SeckillKey("seckillVerifyCode", 300);
}
