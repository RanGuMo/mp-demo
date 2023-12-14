package com.itheima.mp.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itheima.mp.domain.po.User;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import javax.management.Query;
import java.util.List;

// public interface UserMapper{
//
//     void saveUser(User user);
//
//     void deleteUser(Long id);
//
//     void updateUser(User user);
//
//     User queryUserById(@Param("id") Long id);
//
//     List<User> queryUserByIds(@Param("ids") List<Long> ids);
// }

public interface UserMapper extends BaseMapper<User>{
    User queryById(@Param("id") Long id);

    @Select("UPDATE  user SET balance = balance -#{money} ${ew.customSqlSegment}")
    void deductBalanceByIds(@Param("money") int money, @Param("ew") QueryWrapper<User> wrapper);


    // @Select("SELECT u.* FROM user u INNER JOIN address a ON u.id = a.user_id ${ew.customSqlSegment}")
    List<User> queryUserByWrapper(@Param("ew")QueryWrapper<User> wrapper);

    @Update("UPDATE user SET balance = balance - #{money} WHERE id = #{id}")
    void deductMoneyById(Long id, Integer money);
}


