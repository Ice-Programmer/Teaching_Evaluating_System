package com.itmo.eva.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
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
import com.itmo.eva.model.enums.GradeEnum;
import com.itmo.eva.model.enums.MajorEnum;
import com.itmo.eva.model.vo.course.CourseVo;
import com.itmo.eva.service.CourseService;
import com.itmo.eva.utils.EnumUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author chenjiahan
 * @description 针对表【e_course(课程表)】的数据库操作Service实现
 * @createDate 2023-01-23 10:58:56
 */
@Service
@Slf4j
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
    @Transactional(rollbackFor = Exception.class)
    public Boolean addCourse(CourseAddRequest courseAddRequest) {
        if (courseAddRequest == null || ObjectUtils.isNull(courseAddRequest.getTid())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }
        // 判断课程是否存在 【根据教师和名称】
        String cName = courseAddRequest.getCName();
        List<Long> teacherIdList = courseAddRequest.getTid();
        for (Long tid : teacherIdList) {
            Course oldCourse = courseMapper.getCourseByNameAndTeacher(cName, tid);
            if (oldCourse != null) {
                throw new BusinessException(ErrorCode.DATA_REPEAT, "课程信息已存在");
            }
            Course course = new Course();

            BeanUtils.copyProperties(courseAddRequest, course);
            course.setTid(tid);
            // 校验数据
            this.validCourse(course, true);
            this.save(course);
        }
        return true;
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
        List<Long> teacherIdList = courseUpdateRequest.getTid();

        if (CollectionUtils.isEmpty(teacherIdList)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long id = courseUpdateRequest.getId();
        // 只有一个tid，更改当前用户
        if (teacherIdList.size() == 1) {
            Course oldCourse = this.getById(id);
            if (oldCourse == null) {
                throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "修改课程不存在");
            }
            Long tid = teacherIdList.get(0);
            Course course = new Course();
            BeanUtils.copyProperties(courseUpdateRequest, course);
            // 判断该课程下是否已经有该教师
            LambdaQueryWrapper<Course> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Course::getCName, course.getCName());
            List<Course> courseList = this.list(queryWrapper);
            List<Long> tidList = courseList.stream().map(Course::getTid).collect(Collectors.toList());
            if (tidList.contains(tid)) {
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "该课程下已经有该教师");
            }
            course.setTid(tid);
            this.validCourse(course, false);
            return this.updateById(course);
        }

        // 新增教师信息【剔除第一个tid】
        CourseAddRequest courseAddRequest = new CourseAddRequest();
        BeanUtils.copyProperties(courseUpdateRequest, courseAddRequest);
        teacherIdList.remove(0);
        courseAddRequest.setTid(teacherIdList);
        return this.addCourse(courseAddRequest);
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
        courseInfo.setGrade(GradeEnum.getEnumByValue(course.getGrade()).getGrade());
        courseInfo.setTeacher(teacherMapper.getNameById(course.getTid()));

        return courseInfo;
    }

    /**
     * 获取列表
     *
     * @return 课程列表
     */
    @Override
    public List<CourseVo> listCourse() {
        List<Course> courseList = this.baseMapper.getOrderBySid();
        if (courseList == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "没有数据记录");
        }

        List<Teacher> teacherList = teacherMapper.selectList(null);
        Map<Long, String> teacherMap = teacherList.stream().collect(Collectors.toMap(Teacher::getId, Teacher::getName));

        Map<String, List<Course>> courseByNameOrder = courseList.stream().collect(Collectors.groupingBy(Course::getCName));
        List<CourseVo> courseVoList = courseList.stream().map((course) -> {
            CourseVo courseVo = new CourseVo();
            BeanUtils.copyProperties(course, courseVo);
            courseVo.setTeacher(teacherMap.get(course.getTid()));
            courseVo.setGrade(GradeEnum.getEnumByValue(course.getGrade()).getGrade());
            return courseVo;
        }).collect(Collectors.toList());
        // todo 将课程名称相同的排列在一起

        return courseVoList;
    }

    /**
     * excel批量保存课程信息
     *
     * @param file excel文件
     * @return 保存成功
     */
    @Override
    public Boolean excelImport(MultipartFile file) {
        // 1.判断文件是否为空
        if (file.isEmpty()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请重新上传文件");
        }


        XSSFWorkbook wb = null;
        try {
            // 2.POI 获取Excel数据
            wb = new XSSFWorkbook(file.getInputStream());
            XSSFSheet sheet = wb.getSheetAt(0);

            // 3.定义程序集合来接收文件内容
            List<Course> courseList = new ArrayList<>();
            XSSFRow row = null;

            List<Teacher> teacherList = teacherMapper.selectList(null);
            Map<String, Long> teacherMap = teacherList.stream().collect(Collectors.toMap(Teacher::getName, Teacher::getId));

            String[] strs = {"getGrade", "getValue"};
            Map<Object, String> gradeMap = EnumUtils.EnumToMap(GradeEnum.class, strs);

            //4.接收数据 装入集合中
            for (int i = 1; i < sheet.getPhysicalNumberOfRows(); i++) {
                row = sheet.getRow(i);
                Course course = new Course();
                String cName = row.getCell(0).getStringCellValue();
                String eName = row.getCell(1).getStringCellValue();
                String major = row.getCell(2).getStringCellValue();
                String teacher = row.getCell(3).getStringCellValue();
                String grade = row.getCell(4).getStringCellValue();
                if ("".equals(cName)) {
                    continue;
                }
                if (!"计算机科学与技术".equals(major) && !"自动化".equals(major)) {
                    String error = "在第" + i + "行，专业错误";
                    throw new BusinessException(ErrorCode.PARAMS_ERROR, error);
                }
                if (!gradeMap.containsKey(grade)) {
                    String error = "在第" + i + "行，年级错误";
                    throw new BusinessException(ErrorCode.PARAMS_ERROR, error);
                }
                // 统一用中文逗号
                if (teacher.contains("，")) {
                    String[] teachers = teacher.split("，");
                    Long teacherId = null;
                    for (String e : teachers) {
                        Course course1 = new Course();
                        teacherId = teacherMap.get(e);
                        course1.setCName(cName);
                        course1.setEName(eName);
                        course1.setMajor(major.equals("自动化") ? 1 : 0);
                        course1.setTid(teacherId);
                        course1.setGrade(Integer.valueOf(gradeMap.get(grade)));
                        this.validCourse(course1, true);
                        Course oldCourse = baseMapper.getCourseByNameAndTeacher(cName, teacherId);
                        if (oldCourse != null) {
                            String error = "在第" + i + "行，数据已存在";
                            throw new BusinessException(ErrorCode.PARAMS_ERROR, error);
                        }
                        courseList.add(course1);
                    }
                    continue;
                }
                Long teacherId = teacherMap.get(teacher);
                course.setCName(cName);
                course.setEName(eName);
                course.setMajor(major.equals("自动化") ? 1 : 0);
                course.setTid(teacherId);
                course.setGrade(Integer.valueOf(gradeMap.get(grade)));
                // 判断数据是否已经存在 【根据名称和教师id进行查询】
                Course oldCourse = baseMapper.getCourseByNameAndTeacher(cName, teacherId);
                if (oldCourse != null) {
                    String error = "在第" + i + "行，数据已存在";
                    log.info(error);
                    continue;
                }
                this.validCourse(course, true);
                courseList.add(course);
            }

            // 将列表保存到数据库中
            boolean save = this.saveBatch(courseList);

            if (!save) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "保存文件失败");
            }
            // 保存成功
            return true;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // todo 可以通过选择课程的方式批量到处课程信息

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