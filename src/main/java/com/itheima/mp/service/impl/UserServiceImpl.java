package com.itheima.mp.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import com.itheima.mp.domain.dto.PageDTO;
import com.itheima.mp.domain.enums.UserStatus;
import com.itheima.mp.domain.po.Address;
import com.itheima.mp.domain.po.User;
import com.itheima.mp.domain.query.UserQuery;
import com.itheima.mp.domain.vo.AddressVO;
import com.itheima.mp.domain.vo.UserVO;
import com.itheima.mp.mapper.UserMapper;
import com.itheima.mp.service.IUserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {
    @Override
    public void deductBalance(Long id, Integer money) {
        // 1.查询用户
        User user = getById(id);
        // 2.判断用户状态
        if (user == null || user.getStatus() == UserStatus.FREEZE) {
            throw new RuntimeException("用户状态异常");
        }
        // 3.判断用户余额
        if (user.getBalance() < money) {
            throw new RuntimeException("用户余额不足");
        }
        // 4.扣减余额(这个deductMoneyById方法是自定义的)
        baseMapper.deductMoneyById(id, money);
    }

    @Override
    public List<User> queryUsers(String name, Integer status, Integer minBalance, Integer maxBalance) {
        return lambdaQuery()
                .like(name != null, User::getUsername, name)
                .eq(status != null, User::getStatus, status)
                .ge(minBalance != null, User::getBalance, minBalance)
                .le(maxBalance != null, User::getBalance, maxBalance)
                .list();

    }

    @Override
    @Transactional
    public void deductBalanceLambdaUpdate(Long id, Integer money) {
        // 1.查询用户
        User user = getById(id);
        // 2.判断用户状态
        if (user == null || user.getStatus() == UserStatus.FREEZE) {
            throw new RuntimeException("用户状态异常");
        }
        // 3.判断用户余额
        if (user.getBalance() < money) {
            throw new RuntimeException("用户余额不足");
        }
        // 4.扣减余额,并判断，如果余额为0，status 改为 2
        // 4.1.先计算剩余的余额
        int remainBalance = user.getBalance() - money;
        lambdaUpdate()
                .set(User::getBalance, remainBalance) // 更新余额
                .set(remainBalance == 0, User::getStatus, 2) // 动态判断，是否更新status
                .eq(User::getId, id)
                .eq(User::getBalance, user.getBalance()) // 乐观锁
                .update();
    }

    @Override
    public UserVO queryUserAndAddressById(Long userId) {
        // 1.查询用户
        User user = getById(userId);
        if (user == null || user.getStatus() == UserStatus.FREEZE) {
            throw new RuntimeException("用户状态异常");
        }
        // 2.查询收货地址
        List<Address> addresses = Db.lambdaQuery(Address.class).eq(Address::getUserId, userId).list();
        // 3.封装VO
        // 3.1 user的 po 转 vo
        UserVO userVO = BeanUtil.copyProperties(user, UserVO.class);
        // 3.2 地址的po 转vo
        if (CollUtil.isNotEmpty(addresses)) {
            userVO.setAddresses(BeanUtil.copyToList(addresses, AddressVO.class));
        }

        return userVO;
    }

    @Override
    public List<UserVO> queryUserAndAddressByIds(List<Long> ids) {
        // 1.查询用户
        List<User> users = listByIds(ids);
        if (CollUtil.isEmpty(users)) { // 如果为空 直接返回空列表
            return Collections.emptyList();
        }
        // 2.查询地址
        // 2.1. 获取用户id集合
        List<Long> userIds = users.stream().map(User::getId).collect(Collectors.toList());
        // 2.2. 根据用户id集合 查询地址
        List<Address> addresses = Db.lambdaQuery(Address.class).in(Address::getUserId, userIds).list();
        // 2.3.转地址VO
        List<AddressVO> addressVOList = BeanUtil.copyToList(addresses, AddressVO.class);
        // 2.4.用户地址集合 分组处理，一个用户对应一个地址集合，map结构
        Map<Long, List<AddressVO>> addressMap = new HashMap<>(0);
        if (CollUtil.isNotEmpty(addressVOList)) { // 根据用户id 进行分组
            addressMap = addressVOList.stream().collect(Collectors.groupingBy(AddressVO::getUserId));
        }
        // 3.转VO返回
        ArrayList<UserVO> list = new ArrayList<>(users.size());
        for (User user : users) {
            //  3.1. 转换User 的 po 为 vo
            UserVO userVO = BeanUtil.copyProperties(user, UserVO.class);
            list.add(userVO);
            //  3.2. 转换地址的VO
            userVO.setAddresses(addressMap.get(user.getId()));
        }
        return list;
    }

    @Override
    public PageDTO<UserVO> queryUsersPage(UserQuery query) {
        String name = query.getName();
        Integer status = query.getStatus();
        Integer maxBalance = query.getMaxBalance();
        Integer minBalance = query.getMinBalance();
        // 1.1.分页条件
        Page<User> page = Page.of(query.getPageNo(), query.getPageSize());
        // 1.2.排序条件
        if (StrUtil.isNotBlank(query.getSortBy())) { // 排序条件不为空
            page.addOrder(new OrderItem(query.getSortBy(), query.getIsAsc()));
        } else { // 排序条件为空，默认按照更新时间进行降序排序
            page.addOrder(new OrderItem("update_time", false));
        }
        // 2.分页查询
        Page<User> p = lambdaQuery()
                .like(name != null, User::getUsername, name)
                .eq(status != null, User::getStatus, status)
                .ge(minBalance != null, User::getBalance, minBalance)
                .le(maxBalance != null, User::getBalance, maxBalance)
                .page(page);

        // 3.封装VO结果
        PageDTO<UserVO> dto = new PageDTO<>();
        // 3.1.总条数
        dto.setTotal(p.getTotal());
        // 3.2.总页数
        dto.setPages(p.getPages());
        // 3.3.当前页数据
        List<User> records = p.getRecords();
        if (CollUtil.isEmpty(records)) {
            dto.setList(Collections.emptyList());
            return dto;
        }
        // 3.4.拷贝user的vo
        dto.setList(BeanUtil.copyToList(records, UserVO.class));
        // 4.返回
        return dto;
    }


}