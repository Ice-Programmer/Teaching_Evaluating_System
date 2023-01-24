package com.itmo.eva.service.impl;

import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itmo.eva.common.ErrorCode;
import com.itmo.eva.exception.BusinessException;
import com.itmo.eva.mapper.CourseMapper;
import com.itmo.eva.mapper.TeacherMapper;
import com.itmo.eva.model.dto.course.CourseAddRequest;
import com.itmo.eva.model.dto.course.CourseUpdateRequest;
import com.itmo.eva.model.entity.Course;
import com.itmo.eva.model.entity.Teacher;
import com.itmo.eva.model.enums.MajorEnum;
import com.itmo.eva.model.vo.CourseVo;
import com.itmo.eva.service.CourseService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author chenjiahan
 * @description 针对表【e_course(课程表)】的数据库操作Service实现
 * @createDate 2023-01-23 10:58:56
 */
@Service
public class CourseServiceImpl extends ServiceImpl<CourseMapper, Course>
        implements CourseService {

    @Resource
    private CourseMapper courseMapper;

    @Resource
    private TeacherMapper teacherMapper;

    /**
     * 添加课程
     *
     * @param courseAddRequest 课程请求体
     * @return 添加成功
     */
    @Override
    public Boolean addCourse(CourseAddRequest courseAddRequest) {
        if (courseAddRequest == null || ObjectUtils.isNull(courseAddRequest.getTid())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }
        // 判断课程是否存在 【根据教师和名称】
        String cName = courseAddRequest.getCName();
        Long tid = courseAddRequest.getTid();
        Course oldCourse = courseMapper.getCourseByNameAndTeacher(cName, tid);
        if (oldCourse != null) {
            throw new BusinessException(ErrorCode.DATA_REPEAT, "课程信息已存在");
        }
        Course course = new Course();

        BeanUtils.copyProperties(courseAddRequest, course);
        // 校验数据
        this.validCourse(course, true);
        boolean save = this.save(course);

        return save;
    }

    /**
     * 删除课程
     *
     * @param id 删除id
     * @return 删除成功
     */
    @Override
    public Boolean deleteCourse(Long id) {
        // 判断是否存在
        Course oldCourse = this.getById(id);
        if (oldCourse == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "课程不存在");
        }
        boolean remove = this.removeById(id);

        return remove;
    }

    /**
     * 更新课程
     *
     * @param courseUpdateRequest 更新请求体
     * @return 更新成功
     */
    @Override
    public Boolean updateCourse(CourseUpdateRequest courseUpdateRequest) {
        if (courseUpdateRequest == null || courseUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 判断是否存在
        Integer id = courseUpdateRequest.getId();
        Course oldCourse = this.getById(id);
        if (oldCourse == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "课程信息不存在");
        }
        Course course = new Course();
        BeanUtils.copyProperties(courseUpdateRequest, course);
        // 参数校验
        this.validCourse(course, false);

        boolean update = this.updateById(course);

        return update;
    }

    /**
     * 根据 id 获取
     *
     * @param id id
     * @return 课程信息
     */
    @Override
    public CourseVo getCourseById(Long id) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        Course course = this.getById(id);
        // 判空
        if (course == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "学生信息不存在");
        }
        CourseVo courseInfo = new CourseVo();
        // 获取教师名称
        BeanUtils.copyProperties(course, courseInfo);
        courseInfo.setTeacher(teacherMapper.getNameById(course.getTid()));

        return courseInfo;
    }

    /**
     * 获取列表
     * @return 课程列表
     */
    @Override
    public List<CourseVo> listCourse() {
        List<Course> courseList = this.list();
        if (courseList == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "没有数据记录");
        }

        List<Teacher> teacherList = teacherMapper.selectList(null);
        Map<Long, String> teacherMap = teacherList.stream().collect(Collectors.toMap(Teacher::getId, Teacher::getName));

        List<CourseVo> courseVoList = courseList.stream().map((course) -> {
            CourseVo courseVo = new CourseVo();
            BeanUtils.copyProperties(course, courseVo);
            courseVo.setTeacher(teacherMap.get(course.getTid()));
            return courseVo;
        }).collect(Collectors.toList());

        return courseVoList;
    }

    /**
     * 获取课程列表
     */
    @Override
    public void validCourse(Course course, boolean add) {
        if (course == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String cName = course.getCName();
        String eName = course.getEName();
        Integer major = course.getMajor();
        Long tid = course.getTid();
        Integer grade = course.getGrade();
        List<Teacher> teacherList = teacherMapper.selectList(null);
        Map<Long, String> teacherMap = teacherList.stream().collect(Collectors.toMap(Teacher::getId, Teacher::getName));

        // 判断是否为新增操作
        if (add) {
            if (StringUtils.isAnyBlank(cName, eName) || ObjectUtils.isNull(tid, major, grade)) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "数据含空");
            }
        }
        if (major != null && !MajorEnum.getValues().contains(major)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "专业不符合要求");
        }
        if (grade != null && !(grade > 0 && grade <= 8)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "年级不符合要求");
        }
        if (tid != null && !teacherMap.containsKey(tid)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "教师id不符合规范");
        }

    }
}




