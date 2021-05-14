package com.myq.miaosha.redis;

/**
 * @author 孟赟强
 * @date 2021/4/21.
 *
 * 缓冲key前缀
 */
public interface KeyPrefix {

    /**
     * 有效期
     * @return
     */
    public int expireSeconds();

    /**
     * 前缀
     * @return
     */
    public String getPrefix();
}
