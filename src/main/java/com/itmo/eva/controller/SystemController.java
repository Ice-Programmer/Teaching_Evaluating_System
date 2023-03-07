package com.itmo.eva.controller;

import com.itmo.eva.common.BaseResponse;
import com.itmo.eva.common.ErrorCode;
import com.itmo.eva.common.ResultUtils;
import com.itmo.eva.exception.BusinessException;
import com.itmo.eva.model.dto.system.SystemAddRequest;
import com.itmo.eva.model.dto.system.SystemDeleteRequest;
import com.itmo.eva.model.dto.system.SystemUpdateRequest;
import com.itmo.eva.model.vo.system.SystemVo;
import com.itmo.eva.service.SystemService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

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
    public BaseResponse<List<SystemVo>> getRussianSystem() {
        List<SystemVo> russianSystem = systemService.getRussianSystem();
        if (russianSystem == null) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR);
        }
        return ResultUtils.success(russianSystem);
    }

    /**
     * 获取中方评价体系
     */
    @GetMapping("/list/china")
    public BaseResponse<List<SystemVo>> getChineseSystem() {
        List<SystemVo> chinaSystem = systemService.getChineseSystem();
        if (chinaSystem == null) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR);
        }
        return ResultUtils.success(chinaSystem);
    }

    /**
     * 添加评测指标
     * @param systemAddRequest
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<Boolean> addSystem(@RequestBody SystemAddRequest systemAddRequest) {
        if (systemAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Boolean save = systemService.addSystem(systemAddRequest);
        if (!save) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "添加失败");
        }
        return ResultUtils.success(true);
    }

    /**
     * 修改评测指标
     */
    @PostMapping("/update")
    public BaseResponse<Boolean> updateSystem(@RequestBody SystemUpdateRequest systemUpdateRequest) {
        if (systemUpdateRequest == null || systemUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Boolean update = systemService.updateSystem(systemUpdateRequest);
        if (!update) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "更新失败");
        }
        return ResultUtils.success(true);
    }

    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteSystem(@RequestBody SystemDeleteRequest systemDeleteRequest) {
        if (systemDeleteRequest == null || systemDeleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Boolean delete = systemService.deleteSystem(systemDeleteRequest);
        if (!delete) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "删除失败");
        }
        return ResultUtils.success(true);
    }

}
