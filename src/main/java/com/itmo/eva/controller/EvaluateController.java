package com.itmo.eva.controller;

import com.itmo.eva.common.*;
import com.itmo.eva.exception.BusinessException;
import com.itmo.eva.model.dto.evaluate.EvaluateAddRequest;
import com.itmo.eva.model.dto.evaluate.EvaluateUpdateRequest;
import com.itmo.eva.model.vo.EvaluateVo;
import com.itmo.eva.service.EvaluateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 评测接口
 */
@RestController
@Slf4j
@RequestMapping("/evaluate")
public class EvaluateController {

    @Resource
    private EvaluateService evaluateService;

    /**
     * 添加评测
     *
     * @param evaluateAddRequest 添加请求体
     * @return 添加成功
     */
    @PostMapping("/add")
    public BaseResponse<Boolean> addEvaluate(@RequestBody EvaluateAddRequest evaluateAddRequest) {
        if (evaluateAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "添加参数为空");
        }
        Boolean save = evaluateService.addEvaluate(evaluateAddRequest);

        if (!save) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "保存数据失败！");
        }

        return ResultUtils.success(true);
    }

    /**
     * 删除评测信息
     *
     * @param deleteRequest 删除请求体
     * @return 删除成功
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteEvaluate(@RequestBody DeleteRequest deleteRequest) {
        Long id = deleteRequest.getId();
        if (id == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "id为空");
        }
        Boolean delete = evaluateService.deleteEvaluate(id);

        if (!delete) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "删除信息失败！");
        }

        return ResultUtils.success(true);
    }

    /**
     * 更新评测信息
     *
     * @param evaluateUpdateRequest 更新请求体
     * @return 更新成功
     */
    @PostMapping("/update")
    public BaseResponse<Boolean> updateEvaluate(@RequestBody EvaluateUpdateRequest evaluateUpdateRequest) {
        if (evaluateUpdateRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }
        Boolean update = evaluateService.updateEvaluate(evaluateUpdateRequest);

        if (!update) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "更新数据失败！");
        }

        return ResultUtils.success(true);
    }

    /**
     * 根据 id 获取
     *
     * @param idRequest id请求体
     * @return 评测信息
     */
    @PostMapping("/get")
    public BaseResponse<EvaluateVo> getEvaluateById(@RequestBody IdRequest idRequest) {
        if (idRequest == null || idRequest.getId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }
        Long id = idRequest.getId();
        EvaluateVo evaluateInfo = evaluateService.getEvaluateById(id);

        if (evaluateInfo == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        return ResultUtils.success(evaluateInfo);
    }

    /**
     * 获取列表
     *
     * @return 所有评测信息
     */
    @GetMapping("/list")
    public BaseResponse<List<EvaluateVo>> listEvaluate() {
        List<EvaluateVo> evaluateVoList = evaluateService.listEvaluate();
        if (evaluateVoList == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }

        return ResultUtils.success(evaluateVoList);
    }


}
