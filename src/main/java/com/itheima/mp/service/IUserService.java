package com.itheima.mp.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.mp.domain.po.User;

import java.util.List;

public interface IUserService extends IService<User> {
    void deductBalance(Long id, Integer money);

    List<User> queryUsers(String name, Integer status, Integer minBalance, Integer maxBalance);

    void deductBalanceLambdaUpdate(Long id, Integer money);
    // 拓展自定义方法
}