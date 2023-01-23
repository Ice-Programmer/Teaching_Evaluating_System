package com.itmo.eva.controller;

import com.itmo.eva.common.BaseResponse;
import com.itmo.eva.common.DeleteRequest;
import com.itmo.eva.common.ErrorCode;
import com.itmo.eva.common.ResultUtils;
import com.itmo.eva.exception.BusinessException;
import com.itmo.eva.model.dto.course.CourseAddRequest;
import com.itmo.eva.model.dto.course.CourseUpdateRequest;
import com.itmo.eva.model.vo.CourseVo;
import com.itmo.eva.service.CourseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 课程接口
 */
@RestController
@Slf4j
@RequestMapping("/course")
public class CourseController {

    @Resource
    private CourseService courseService;

    /**
     * 添加课程
     *
     * @param courseAddRequest 添加请求体
     * @return 添加成功
     */
    @PostMapping("/add")
    public BaseResponse<Boolean> addCourse(@RequestBody CourseAddRequest courseAddRequest) {
        if (courseAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "添加参数为空");
        }
        Boolean save = courseService.addCourse(courseAddRequest);

        if (!save) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "保存数据失败！");
        }

        return ResultUtils.success(true);
    }

    /**
     * 删除课程信息
     *
     * @param deleteRequest 删除请求体
     * @return 删除成功
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteCourse(@RequestBody DeleteRequest deleteRequest) {
        Long id = deleteRequest.getId();
        if (id == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "id为空");
        }
        Boolean delete = courseService.deleteCourse(id);

        if (!delete) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "删除信息失败！");
        }

        return ResultUtils.success(true);
    }

    /**
     * 更新课程信息
     *
     * @param courseUpdateRequest 更新请求体
     * @return 更新成功
     */
    @PostMapping("/update")
    public BaseResponse<Boolean> updateCourse(@RequestBody CourseUpdateRequest courseUpdateRequest) {
        if (courseUpdateRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }
        Boolean update = courseService.updateCourse(courseUpdateRequest);

        if (!update) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "更新数据失败！");
        }

        return ResultUtils.success(true);
    }

    /**
     * 根据 id 获取
     *
     * @param id id请求体
     * @return 课程信息
     */
    @GetMapping("/get")
    public BaseResponse<CourseVo> getCourseById(Long id) {
        if (id == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }
        CourseVo courseInfo = courseService.getCourseById(id);

        if (courseInfo == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        return ResultUtils.success(courseInfo);
    }

    /**
     * 获取列表
     *
     * @return 所有课程信息
     */
    @GetMapping("/list")
    public BaseResponse<List<CourseVo>> listCourse() {
        List<CourseVo> courseVoList = courseService.listCourse();
        if (courseVoList == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }

        return ResultUtils.success(courseVoList);
    }


}
