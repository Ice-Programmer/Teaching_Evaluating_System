package com.itmo.eva.mapper;

import com.itmo.eva.model.entity.Student;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
* @author chenjiahan
* @description 针对表【e_student(学生表)】的数据库操作Mapper
* @createDate 2023-01-22 10:14:33
* @Entity com.itmo.eva.model.entity.Student
*/
public interface StudentMapper extends BaseMapper<Student> {

    /**
     * 根据教师id寻找教师
     * @param sid
     * @return
     */
    @Select("select * from e_student where sid = #{sid}")
    Student getStudentBySid(String sid);

    /**
     * 年级自动加1
     */
    @Update("update e_student set grade = grade + 1")
    void addGradeByAuto();

    /**
     * 删除学期数大于8的学生
     */
    @Delete("delete * from e_student where grade > 8")
    void deleteStudentGradeThan8();
}




