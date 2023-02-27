package com.itmo.eva.controller;

import com.itmo.eva.common.BaseResponse;
import com.itmo.eva.common.ErrorCode;
import com.itmo.eva.common.IdRequest;
import com.itmo.eva.common.ResultUtils;
import com.itmo.eva.exception.BusinessException;
import com.itmo.eva.model.dto.teacher.TeacherIdentityQueryRequest;
import com.itmo.eva.model.dto.teacher.TeacherQueryRequest;
import com.itmo.eva.model.vo.chart.BasicChartsVo;
import com.itmo.eva.model.vo.chart.DetailChartVo;
import com.itmo.eva.model.vo.chart.ScoreVo;
import com.itmo.eva.model.vo.chart.TeacherRankChartVo;
import com.itmo.eva.model.vo.teacher.TeacherNameVo;
import com.itmo.eva.service.ChartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/charts")
public class ChartsController {

    @Resource
    private ChartService chartService;

    /**
     * 获取教师基本信息
     *
     * @return
     */
    @GetMapping("/get/static")
    public BaseResponse<BasicChartsVo> getStaticInfo() {
        BasicChartsVo staticInfo = chartService.getStaticInfo();
        if (staticInfo == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "参数为空");
        }
        return ResultUtils.success(staticInfo);
    }

    /**
     * 获取教师雷达图
     *
     * @param idRequest
     * @return
     */
    @PostMapping("/get/system/chart")
    public BaseResponse<List<DetailChartVo>> getTeacherDetailChart(@RequestBody IdRequest idRequest) {
        if (idRequest == null || idRequest.getId() <= 0L) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long tid = idRequest.getId();
        List<DetailChartVo> TeacherDetailChart = chartService.getDetailChartList(tid);
        return ResultUtils.success(TeacherDetailChart);
    }

    @PostMapping("/get/rank/chart")
    public BaseResponse<List<TeacherRankChartVo>> getTeacherRankChart(@RequestBody TeacherIdentityQueryRequest request) {
        if (request == null || request.getIdentity() < 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Integer identity = request.getIdentity();
        List<TeacherRankChartVo> teacherRankChart = chartService.getTeacherRankChart(identity);
        return ResultUtils.success(teacherRankChart);
    }

    @PostMapping("/get/teacher/detail")
    public BaseResponse<List<ScoreVo>> getTeacherDetailScoreChart(@RequestBody TeacherQueryRequest teacherQueryRequest) {
        if (teacherQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Integer eid = teacherQueryRequest.getEid();
        Integer tid = teacherQueryRequest.getTid();
        List<ScoreVo> teacherDetailScoreChart = chartService.getTeacherDetailScoreChart(eid, tid);
        return ResultUtils.success(teacherDetailScoreChart);
    }

    @GetMapping("/get/teacher/redline")
    public BaseResponse<List<TeacherNameVo>> getRedlineTeacher() {
        List<TeacherNameVo> redlineTeacher = chartService.getRedlineTeacher();
        return ResultUtils.success(redlineTeacher);
    }





}
