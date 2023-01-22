package com.itmo.eva.mapper;

import com.itmo.eva.model.entity.Student;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;

/**
* @author chenjiahan
* @description 针对表【e_student(学生表)】的数据库操作Mapper
* @createDate 2023-01-22 10:14:33
* @Entity com.itmo.eva.model.entity.Student
*/
public interface StudentMapper extends BaseMapper<Student> {

    @Select("select * from e_student where sid = #{sid}")
    Student getStudentBySid(String sid);
}




