package com.itmo.eva.mapper;

import com.itmo.eva.model.entity.Teacher;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;

/**
 * @author chenjiahan
 * @description 针对表【e_teacher(教师表)】的数据库操作Mapper
 * @createDate 2023-01-21 13:17:49
 * @Entity com.itmo.eva.model.entity.Teacher
 */
public interface TeacherMapper extends BaseMapper<Teacher> {

    /**
     * 根据教师姓名和邮箱查询教师信息
     *
     * @param name 教师姓名
     * @param email 教师邮箱
     * @return 教师信息
     */
    @Select("select * from e_teacher where name = #{name} and email = #{email}")
    Teacher getTeacherByNameAndEmail(String name, String email);

    @Select("select name from e_teacher where id = #{tid}")
    String getNameById(Long tid);
}




