package com.itmo.eva.controller;

import com.itmo.eva.common.BaseResponse;
import com.itmo.eva.common.ErrorCode;
import com.itmo.eva.common.ResultUtils;
import com.itmo.eva.exception.BusinessException;
import com.itmo.eva.model.vo.chart.ChartsVo;
import com.itmo.eva.service.charts.ChartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@Slf4j
@RequestMapping("/charts")
public class ChartsController {

    @Resource
    private ChartService chartService;

    @GetMapping("/get/static")
    public BaseResponse<ChartsVo> getStaticInfo() {
        ChartsVo staticInfo = chartService.getStaticInfo();
        if (staticInfo == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "参数为空");
        }
        return ResultUtils.success(staticInfo);
    }

}
