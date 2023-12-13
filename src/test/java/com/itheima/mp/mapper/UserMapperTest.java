package com.itheima.mp.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.itheima.mp.domain.po.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
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
        userMapper.insert(user); // mybatisPlus
    }

    @Test
    void testSelectById() {
        // User user = userMapper.queryUserById(5L); //mybatis
        User user = userMapper.selectById(5L);// mybatisPlus
        System.out.println("user = " + user);
    }


    @Test
    void testQueryByIds() {
        // List<User> users = userMapper.queryUserByIds(List.of(1L, 2L, 3L, 4L)); //mybatis
        List<User> users = userMapper.selectBatchIds(List.of(1L, 2L, 3L, 4L));// mybatisPlus
        users.forEach(System.out::println);
    }

    @Test
    void testUpdateById() {
        User user = new User();
        user.setId(5L);
        user.setBalance(20000);
        // userMapper.updateUser(user); //mybatis
        userMapper.updateById(user);// mybatisPlus
    }

    @Test
    void testDeleteUser() {
        // userMapper.deleteUser(5L); //mybatis
        userMapper.deleteById(5L); // mybatisPlus
    }

    @Test
    void testQuery() {
        User user = userMapper.queryById(1L);
        System.out.println("user = " + user);
    }

    // 查询出名字中带o的，存款大于等于1000元的人。
    @Test
    void testQueryWrapper() {
        // 1.构建查询条件 where name like "%o%" AND balance >= 1000
        QueryWrapper<User> wrapper = new QueryWrapper<User>()
                .select("id", "username", "info", "balance")
                .like("username", "o")
                .ge("balance", 1000);
        // 2.查询数据
        List<User> users = userMapper.selectList(wrapper);
        users.forEach(System.out::println);

    }

    // 更新用户名为jack的用户的余额为2000
    @Test
    void testUpdateByQueryWrapper() {
        // 1.构建查询条件 where name = "Jack"
        QueryWrapper<User> wrapper = new QueryWrapper<User>()
                .eq("username", "Jack");
        // 2.更新数据，user中非null字段都会作为set语句
        User user = new User();
        user.setBalance(2000);
        userMapper.update(user, wrapper);
    }

    // 更新id为1,2,4的用户的余额，扣200
    @Test
    void testUpdateWrapper() {
        // List<Long> ids = new ArrayList<>(Arrays.asList(1L, 2L, 4L)); //JDK8 可以使用这种方式
        List<Long> ids = List.of(1L, 2L, 4L); // JDK9 之后才有的of方法
        // 1.生成SQL
        UpdateWrapper<User> userUpdateWrapper = new UpdateWrapper<User>()
                .setSql("balance = balance -200")
                .in("id", ids); // WHERE id in (1, 2, 4)

        // 2.更新，注意第一个参数可以给null，也就是不填更新字段和数据，
        // 而是基于UpdateWrapper中的setSql来更新
        userMapper.update(null, userUpdateWrapper);
    }

   //======================== LambdaQueryWrapper ==================================================================
   //  查询出名字中带o的，存款大于等于1000元的人。
    @Test
    void testLambdaQueryWrapper() {
        // 1.构建查询条件 where name like "%o%" AND balance >= 1000
        LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<User>()
                .select(User::getId, User::getUsername, User::getInfo, User::getBalance)
                .like(User::getUsername, "o")
                .ge(User::getBalance, 1000);
        // 2.查询数据
        List<User> users = userMapper.selectList(userLambdaQueryWrapper);
        users.forEach(System.out::println);
    }

    // 更新用户名为jack的用户的余额为2000
    @Test
    void testLambdaUpdateByQueryWrapper() {
        // 1.构建查询条件 where name = "Jack"
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<User>()
                .eq(User::getUsername, "Jack");
        // 2.更新数据，user中非null字段都会作为set语句
        User user = new User();
        user.setBalance(2000);
        userMapper.update(user, wrapper);
    }
    //===============================自定义sql=========================================================
    // 更新id为1,2,4的用户的余额，扣200
    @Test
    void testCustomWrapper() {
        // List<Long> ids = new ArrayList<>(Arrays.asList(1L, 2L, 4L)); //JDK8 可以使用这种方式
        List<Long> ids = List.of(1L, 2L, 4L); // JDK9 之后才有的of方法
        // 1.准备自定义查询条件
        QueryWrapper<User> wrapper = new QueryWrapper<User>()
                // .setSql("balance = balance -200")
                .in("id", ids); // WHERE id in (1, 2, 4)

        // 2.调用mapper的自定义方法，直接传递Wrapper
        userMapper.deductBalanceByIds(200, wrapper);
    }

    //=====================用自定义sql完成联表查询===========================================================================
    @Test
    void testCustomJoinWrapper() {
    //     1.准备自定义的查询条件
        QueryWrapper<User> wrapper = new QueryWrapper<User>()
                .in("u.id",List.of(1L,2L,4L))
                .eq("a.city","北京");
    //     2.调用mapper 的自定义方法
       List<User> users =  userMapper.queryUserByWrapper(wrapper);
        users.forEach(System.out::println);
    }

}