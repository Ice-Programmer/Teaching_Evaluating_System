package com.itmo.eva.mapper;

import com.itmo.eva.model.entity.Course;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
* @author chenjiahan
* @description 针对表【e_course(课程表)】的数据库操作Mapper
* @createDate 2023-01-23 10:58:56
* @Entity com.itmo.eva.model.entity.Course
*/
public interface CourseMapper extends BaseMapper<Course> {

    /**
     * 根据课程名称和教师id来查找课程信息
     * @param cName 课程名称
     * @param tid 教师id
     * @return 课程信息
     */
    @Select("select * from e_course where cName = #{cName} and tid = #{tid}")
    Course getCourseByNameAndTeacher(String cName, Long tid);

    @Select("select * from e_course where major = #{major} and grade = #{grade}")
    List<Course> getCourseByMajorAndGrade(Integer major, Integer grade);

    @Select("select * from e_course order by grade")
    List<Course> getOrderBySid();
}




