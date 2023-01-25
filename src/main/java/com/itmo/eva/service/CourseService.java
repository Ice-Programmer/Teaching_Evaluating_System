package com.itmo.eva.service;

import com.itmo.eva.model.dto.course.CourseAddRequest;
import com.itmo.eva.model.dto.course.CourseUpdateRequest;
import com.itmo.eva.model.entity.Course;
import com.baomidou.mybatisplus.extension.service.IService;
import com.itmo.eva.model.entity.Course;
import com.itmo.eva.model.vo.CourseVo;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
* @author chenjiahan
* @description 针对表【e_course(课程表)】的数据库操作Service
* @createDate 2023-01-23 10:58:56
*/
public interface CourseService extends IService<Course> {
    /**
     * 添加课程
     *
     * @param courseAddRequest 课程请求体
     * @return 添加成功
     */
    Boolean addCourse(CourseAddRequest courseAddRequest);

    /**
     * 删除课程
     *
     * @param id 删除id
     * @return 删除成功
     */
    Boolean deleteCourse(Long id);

    /**
     * 更新课程
     *
     * @param courseUpdateRequest 更新请求体
     * @return 更新成功
     */
    Boolean updateCourse(CourseUpdateRequest courseUpdateRequest);

    /**
     * 根据 id 获取
     *
     * @param id id
     * @return 课程信息
     */
    CourseVo getCourseById(Long id);

    /**
     * 获取课程列表
     */
    List<CourseVo> listCourse();


    /**
     * Excel文件批量上传课程信息
     * @param file excel文件
     * @return 保存成功
     */
    Boolean excelImport(MultipartFile file);

    /**
     * 校验
     *
     * @param course 课程信息
     * @param add     是否为创建校验
     */
    void validCourse(Course course, boolean add);

}
