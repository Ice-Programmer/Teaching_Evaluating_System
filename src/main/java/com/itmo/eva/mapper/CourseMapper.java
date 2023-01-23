package com.itmo.eva.mapper;

import com.itmo.eva.model.entity.Course;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;

/**
* @author chenjiahan
* @description 针对表【e_course(课程表)】的数据库操作Mapper
* @createDate 2023-01-23 10:58:56
* @Entity com.itmo.eva.model.entity.Course
*/
public interface CourseMapper extends BaseMapper<Course> {

    @Select("select * from e_course where cName = #{cName} and tid = #{tid}")
    Course getCourseByNameAndTeacher(String cName, Long tid);
}




