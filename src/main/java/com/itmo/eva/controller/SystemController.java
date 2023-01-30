package com.itmo.eva.controller;

import com.itmo.eva.common.ErrorCode;
import com.itmo.eva.exception.BusinessException;
import com.itmo.eva.model.vo.system.SystemVo;
import com.itmo.eva.service.system.SystemService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/system")
public class SystemController {

    @Resource
    private SystemService systemService;

    /**
     * 获取俄方评价体系
     */
    @GetMapping("/list/russia")
    public List<SystemVo> getRussianSystem() {
        List<SystemVo> russianSystem = systemService.getRussianSystem();
        if (russianSystem == null) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR);
        }
        return russianSystem;
    }

    /**
     * 获取中方评价体系
     */
    @GetMapping("/list/china")
    public List<SystemVo> getChineseSystem() {
        List<SystemVo> chinaSystem = systemService.getChineseSystem();
        if (chinaSystem == null) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR);
        }
        return chinaSystem;
    }




}
