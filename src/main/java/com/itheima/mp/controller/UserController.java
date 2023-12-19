package com.itheima.mp.controller;


import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.itheima.mp.domain.dto.UserFormDTO;
import com.itheima.mp.domain.po.User;
import com.itheima.mp.domain.query.UserQuery;
import com.itheima.mp.domain.vo.UserVO;
import com.itheima.mp.service.IUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "用户管理接口")
@RequestMapping("/users")
@RestController
@RequiredArgsConstructor
public class UserController {


    private final IUserService userService;



    @PostMapping
    @ApiOperation("新增用户")
    public void saveUser(@RequestBody UserFormDTO userFormDTO) {
        // 1.转换DTO为PO
        User user = BeanUtil.copyProperties(userFormDTO, User.class);
        // 2.新增
        userService.save(user);
    }

    @DeleteMapping("/{id}")
    @ApiOperation("删除用户")
    public void removeUserById(@PathVariable("id") Long userId){
        userService.removeById(userId);
    }

    // @GetMapping("/{id}")
    // @ApiOperation("根据id查询用户")
    // public UserVO queryUserById(@PathVariable("id") Long userId){
    //     // 1.查询用户
    //     User user = userService.getById(userId);
    //     // 2.处理po转vo
    //     return BeanUtil.copyProperties(user, UserVO.class);
    // }
    // =====================需求：改造根据id用户查询的接口，查询用户的同时返回用户收货地址列表======================================
    @GetMapping("/{id}")
    @ApiOperation("根据id查询用户")
    public UserVO queryUserById(@PathVariable("id") Long userId){
        // 基于自定义service方法查询
        return userService.queryUserAndAddressById(userId);
    }

    // =====================需求：改造根据id用户查询的接口，查询用户的同时返回用户收货地址列表======================================

    @GetMapping
    @ApiOperation("根据id集合查询用户")
    public List<UserVO> queryUserByIds(@RequestParam("ids") List<Long> ids){
        // 1.查询用户
        List<User> users = userService.listByIds(ids);
        // 2.处理po转vo （集合用copyToList）
        return BeanUtil.copyToList(users, UserVO.class);
    }

    @PutMapping("{id}/deduction/{money}")
    @ApiOperation("扣减用户余额")
    public void deductBalance(@PathVariable("id") Long id, @PathVariable("money")Integer money){
        userService.deductBalance(id, money);
    }



    // ======================lambdaQuery 和 lambdaUpdate=====================================================================
    // 方式一：
    // @RequestParam(value = "username",required = false) 表示 传入的 username 可以为null

    // public List<UserVO> queryUsers(@RequestParam(value = "username",required = false)){
    // @GetMapping("/list")
    // @ApiOperation("根据复杂条件查询用户的接口")
    // public List<UserVO> queryUsers(UserQuery query){ // get请求不需要添加注解，spring会自动帮我们转换为路径参数
    //     // 1.组织条件
    //     String username = query.getName();
    //     Integer status = query.getStatus();
    //     Integer minBalance = query.getMinBalance();
    //     Integer maxBalance = query.getMaxBalance();
    //     LambdaQueryWrapper<User> wrapper = new QueryWrapper<User>().lambda()
    //             .like(username != null, User::getUsername, username)
    //             .eq(status != null, User::getStatus, status)
    //             .ge(minBalance != null, User::getBalance, minBalance)
    //             .le(maxBalance != null, User::getBalance, maxBalance);
    //     // 2.查询用户
    //     List<User> users = userService.list(wrapper);
    //     // 3.处理vo
    //     return BeanUtil.copyToList(users, UserVO.class);
    // }

    //方式二：
    // @GetMapping("/list")
    // @ApiOperation("根据复杂条件查询用户的接口")
    // public List<UserVO> queryUsers(UserQuery query){
    //     // 1.组织条件
    //     String username = query.getName();
    //     Integer status = query.getStatus();
    //     Integer minBalance = query.getMinBalance();
    //     Integer maxBalance = query.getMaxBalance();
    //     // 2.查询用户
    //     List<User> users = userService.lambdaQuery()
    //             .like(username != null, User::getUsername, username)
    //             .eq(status != null, User::getStatus, status)
    //             .ge(minBalance != null, User::getBalance, minBalance)
    //             .le(maxBalance != null, User::getBalance, maxBalance)
    //             .list();
    //     // 3.处理vo
    //     return BeanUtil.copyToList(users, UserVO.class);
    // }

   //方式三：
    @GetMapping("/list")
    @ApiOperation("根据复杂条件查询用户的接口")
    public List<UserVO> queryUsers(UserQuery query) { // get请求不需要添加注解，spring会自动帮我们转换为路径参数
        // 1.查询用户
        List<User> users = userService.queryUsers(query.getName(),query.getStatus(),query.getMinBalance(),query.getMaxBalance());
        // 2.处理po转vo （集合用copyToList）
        return BeanUtil.copyToList(users, UserVO.class);
    }



    @PutMapping("/lambdaUpdate/{id}/deduction/{money}")
    @ApiOperation("扣减用户余额，如果余额为0，状态改为2")
    public void deductBalanceLambdaUpdate(@PathVariable("id") Long id, @PathVariable("money")Integer money){
        userService.deductBalanceLambdaUpdate(id, money);
    }
}
