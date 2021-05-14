package com.myq.miaosha;

import com.myq.miaosha.mapper.UserMapper;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class MiaoshaApplicationTests {

    @Autowired
    UserMapper userMapper;

    @Test
    void contextLoads() {

    }


}
