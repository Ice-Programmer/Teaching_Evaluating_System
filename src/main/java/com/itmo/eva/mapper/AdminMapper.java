package com.itmo.eva.mapper;

import com.itmo.eva.model.entity.Admin;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;

/**
* @author chenjiahan
* @description 针对表【e_admin(管理员表)】的数据库操作Mapper
* @createDate 2023-01-21 10:48:59
* @Entity com.itmo.eva.model.entity.Admin
*/
public interface AdminMapper extends BaseMapper<Admin> {

    /**
     * 查询用户
     * @param username 账号
     * @param password 密码
     * @return 用户信息
     */
    @Select("select * from e_admin where username = #{username} and password = #{password}")
    Admin queryUserByUsernameAndPassword(String username, String password);
}




