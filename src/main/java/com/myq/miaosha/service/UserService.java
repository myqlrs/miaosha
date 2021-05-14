package com.myq.miaosha.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.myq.miaosha.entity.User;
import com.myq.miaosha.vo.LoginVo;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 孟赟强
 * @since 2021-05-05
 */
public interface UserService extends IService<User> {

    /**
     * 缓存获取User
     *
     * @param id
     * @return
     */
    public User getById(long id);

    /**
     * 将token做为key，用户信息做为value 存入redis模拟session
     * 同时将token存入cookie，保存登录状态
     */
    public void addCookie(HttpServletResponse response, String token, User user);

    /**
     * 登录
     *
     * @param response
     * @param loginVo
     * @return
     */
    public String login(HttpServletResponse response, LoginVo loginVo);

    /**
     * 根据token获取用户信息
     */
    public User getByToken(HttpServletResponse response, String token);

    /**
     * 典型缓存同步场景：更新密码
     */
    public boolean updatePassword(String token, long id, String formPass);

    /**
     * 查询所有用户信息
     * @return
     */
    public List<User> listUser();

}
