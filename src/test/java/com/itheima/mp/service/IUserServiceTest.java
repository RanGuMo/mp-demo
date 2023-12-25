package com.itheima.mp.service;

import com.baomidou.mybatisplus.extension.toolkit.Db;
import com.itheima.mp.domain.po.User;
import com.itheima.mp.domain.po.UserInfo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class IUserServiceTest {
    @Autowired
    private IUserService userService;

    @Test
    void testSaveUser(){
        User user = new User();
        user.setId(5L);
        user.setUsername("LiLei");
        user.setPassword("123");
        user.setPhone("18688990011");
        user.setBalance(200);
        // user.setInfo("{\"age\": 24, \"intro\": \"英文老师\", \"gender\": \"female\"}");
        UserInfo userInfo = new UserInfo();
        userInfo.setAge(24);
        userInfo.setIntro("英文老师");
        userInfo.setGender("female");
        user.setInfo(userInfo);
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());

        userService.save(user);
    }

    @Test
    void testQuery(){
        List<User> users = userService.listByIds(List.of(1L,2L,4L));
        users.forEach(System.out::println);
    }



   //==========================测试批量新增=================================

    @Test
    void testSaveOneByOne() {
        long b = System.currentTimeMillis();
        for (int i = 1; i <= 100000; i++) { // 新增10万条数据
            userService.save(buildUser(i));
        }
        long e = System.currentTimeMillis();
        System.out.println("耗时：" + (e - b));
    }

    private User buildUser(int i) {
        User user = new User();
        user.setUsername("user_" + i);
        user.setPassword("123");
        user.setPhone("" + (18688190000L + i));
        user.setBalance(2000);
        // user.setInfo("{\"age\": 24, \"intro\": \"英文老师\", \"gender\": \"female\"}");
        UserInfo userInfo = new UserInfo();
        userInfo.setAge(24);
        userInfo.setIntro("英文老师");
        userInfo.setGender("female");
        user.setInfo(userInfo);
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(user.getCreateTime());
        return user;
    }


    @Test
    void testSaveBatch() {
        // 准备10万条数据
        List<User> list = new ArrayList<>(1000); // 创建一个容量为1000的空ArrayList

        long b = System.currentTimeMillis(); // 记录开始时间
        for (int i = 1; i <= 1000000; i++) { // 循环100000次，即准备10万条数据
            list.add(buildUser(i)); // 将buildUser(i)的返回结果添加到list中

            // 每1000条批量插入一次
            if (i % 1000 == 0) { // 当i是1000的倍数时
                userService.saveBatch(list); // 调用userService的saveBatch方法，批量插入list中的数据
                list.clear(); // 清空list，以便下一次批量插入
            }
        }
        long e = System.currentTimeMillis(); // 记录结束时间
        System.out.println("耗时：" + (e - b)); // 输出耗时时间
    }
//==========================测试批量新增=================================
//==========================测试Db=================================
    @Test
    void testDbGet() {
        User user = Db.getById(1L, User.class);
        System.out.println(user);
    }

    @Test
    void testDbList() {
        // 利用Db实现复杂条件查询
        List<User> list = Db.lambdaQuery(User.class)
                .like(User::getUsername, "o")
                .ge(User::getBalance, 1000)
                .list();
        list.forEach(System.out::println);
    }

    @Test
    void testDbUpdate() {
        Db.lambdaUpdate(User.class)
                .set(User::getBalance, 2000)
                .eq(User::getUsername, "Rose");
    }


    //  ========================================枚举类型的使用===================================================================
    @Test
    void testService() {
        List<User> list = userService.list();
        list.forEach(System.out::println);
    }


}