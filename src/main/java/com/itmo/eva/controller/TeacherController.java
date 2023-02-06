package com.itmo.eva.controller;

import com.itmo.eva.common.*;
import com.itmo.eva.exception.BusinessException;
import com.itmo.eva.model.dto.teacher.TeacherAddRequest;
import com.itmo.eva.model.dto.teacher.TeacherUpdateRequest;
import com.itmo.eva.model.vo.teacher.TeacherNameVo;
import com.itmo.eva.model.vo.teacher.TeacherVo;
import com.itmo.eva.service.TeacherService;
import com.itmo.eva.utils.DownLoadUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 教师接口
 */
@RestController
@Slf4j
@RequestMapping("/teacher")
public class TeacherController {

    @Resource
    private TeacherService teacherService;

    /**
     * 添加教师
     *
     * @param teacherAddRequest 添加请求体
     * @return 添加成功
     */
    @PostMapping("/add")
    public BaseResponse<Boolean> addTeacher(@RequestBody TeacherAddRequest teacherAddRequest) {
        if (teacherAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "添加参数为空");
        }
        Boolean save = teacherService.addTeacher(teacherAddRequest);

        if (!save) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "保存数据失败！");
        }

        return ResultUtils.success(true);
    }

    /**
     * 删除教师信息
     *
     * @param deleteRequest 删除请求体
     * @return 删除成功
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteTeacher(@RequestBody DeleteRequest deleteRequest) {
        Long id = deleteRequest.getId();
        if (id == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "id为空");
        }
        Boolean delete = teacherService.deleteTeacher(id);

        if (!delete) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "删除信息失败！");
        }

        return ResultUtils.success(true);
    }

    /**
     * 更新教师信息
     *
     * @param teacherUpdateRequest 更新请求体
     * @return 更新成功
     */
    @PostMapping("/update")
    public BaseResponse<Boolean> updateTeacher(@RequestBody TeacherUpdateRequest teacherUpdateRequest) {
        if (teacherUpdateRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }
        Boolean update = teacherService.updateTeacher(teacherUpdateRequest);

        if (!update) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "更新数据失败！");
        }

        return ResultUtils.success(true);
    }

    /**
     * 根据 id 获取
     *
     * @param idRequest id请求体
     * @return 教师信息
     */
    @PostMapping("/get")
    public BaseResponse<TeacherVo> getTeacherById(@RequestBody IdRequest idRequest) {
        if (idRequest == null || idRequest.getId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }
        Long id = idRequest.getId();
        TeacherVo teacherInfo = teacherService.getTeacherById(id);

        if (teacherInfo == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        return ResultUtils.success(teacherInfo);
    }

    /**
     * 获取列表
     *
     * @return 所有教师信息
     */
    @GetMapping("/list")
    public BaseResponse<List<TeacherVo>> listTeacher() {
        List<TeacherVo> teacherVoList = teacherService.listTeacher();
        if (teacherVoList == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        return ResultUtils.success(teacherVoList);
    }

    /**
     * Excel文件批量上传教师信息
     *
     * @param file excel
     * @return 保存成功
     */
    @PostMapping("/excel/import")
    public BaseResponse<Boolean> saveExcel(@RequestParam("file") MultipartFile file) {

        Boolean isImport = teacherService.excelImport(file);

        if (!isImport) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "保存文件失败！");
        }

        return ResultUtils.success(true);
    }

    /**
     * 下载示例Excel
     *
     * @param response 请求
     * @return 示例
     */
    @GetMapping("/excel/export")
    public void exportExcel(HttpServletResponse response) {
        String path = "C:\\excel\\教师信息模版";
        DownLoadUtil.uploadFile(response, path);
    }

    /**
     * 获取中方教师信息
     *
     * @return
     */
    @GetMapping("/get/china")
    public BaseResponse<List<TeacherNameVo>> getChineseTeacher() {
        List<TeacherNameVo> chineseTeacher = teacherService.getChineseTeacher();
        if (chineseTeacher == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        return ResultUtils.success(chineseTeacher);
    }

    /**
     * 获取俄方教师信息
     */
    @GetMapping("/get/russia")
    public BaseResponse<List<TeacherNameVo>> getRussianTeacher() {
        List<TeacherNameVo> russianTeacher = teacherService.getRussianTeacher();
        if (russianTeacher == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        return ResultUtils.success(russianTeacher);
    }


}
