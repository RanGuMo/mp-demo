package com.itheima.mp.mapper;

import com.itheima.mp.domain.po.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;

@SpringBootTest
class UserMapperTest {

    @Autowired
    private UserMapper userMapper;

    @Test
    void testInsert() {
        User user = new User();
        user.setId(5L);
        user.setUsername("Lucy");
        user.setPassword("123");
        user.setPhone("18688990011");
        user.setBalance(200);
        user.setInfo("{\"age\": 24, \"intro\": \"英文老师\", \"gender\": \"female\"}");
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        // userMapper.saveUser(user); //mybatis
        userMapper.insert(user); //mybatisPlus
    }

    @Test
    void testSelectById() {
        // User user = userMapper.queryUserById(5L); //mybatis
        User user = userMapper.selectById(5L);//mybatisPlus
        System.out.println("user = " + user);
    }


    @Test
    void testQueryByIds() {
        // List<User> users = userMapper.queryUserByIds(List.of(1L, 2L, 3L, 4L)); //mybatis
        List<User> users = userMapper.selectBatchIds(List.of(1L, 2L, 3L, 4L));//mybatisPlus
        users.forEach(System.out::println);
    }

    @Test
    void testUpdateById() {
        User user = new User();
        user.setId(5L);
        user.setBalance(20000);
        // userMapper.updateUser(user); //mybatis
        userMapper.updateById(user);//mybatisPlus
    }

    @Test
    void testDeleteUser() {
        // userMapper.deleteUser(5L); //mybatis
        userMapper.deleteById(5L); //mybatisPlus
    }
}