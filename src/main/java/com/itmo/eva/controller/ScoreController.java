package com.itmo.eva.controller;

import com.itmo.eva.common.BaseResponse;
import com.itmo.eva.common.ErrorCode;
import com.itmo.eva.common.IdRequest;
import com.itmo.eva.common.ResultUtils;
import com.itmo.eva.exception.BusinessException;
import com.itmo.eva.model.dto.score.ScoreFilterRequest;
import com.itmo.eva.model.vo.ScoreHistoryVo;
import com.itmo.eva.service.rank.ScoreHistoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 分数排名接口
 */
@RestController
@Slf4j
@RequestMapping("/score")
public class ScoreController {

    @Resource
    private ScoreHistoryService scoreHistoryService;

    /**
     * 获取中方教师排名
     * @param scoreFilterRequest 排名请求体
     * @return
     */
    @PostMapping("/rank/chinese")
    public BaseResponse<List<ScoreHistoryVo>> getChineseRank(@RequestBody ScoreFilterRequest scoreFilterRequest) {

        List<ScoreHistoryVo> chineseScore = scoreHistoryService.getChineseScore(scoreFilterRequest);
        if (chineseScore.isEmpty()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "数据为空");
        }
        return ResultUtils.success(chineseScore);
    }

    /**
     * 获取俄方教师排名
     * @param scoreFilterRequest
     * @return
     */
    @PostMapping("/rank/russian")
    public BaseResponse<List<ScoreHistoryVo>> getRussianRank(@RequestBody ScoreFilterRequest scoreFilterRequest) {

        List<ScoreHistoryVo> russianScore = scoreHistoryService.getRussianScore(scoreFilterRequest);
        if (russianScore.isEmpty()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "数据为空");
        }
        return ResultUtils.success(russianScore);
    }

    /**
     * 导出中方教师排名信息
     */
    @PostMapping("/rank/chinese/export/excel")
    public BaseResponse<Boolean> getChineseExcel(@RequestBody ScoreFilterRequest scoreFilterRequest ,HttpServletResponse response) {
        if (scoreFilterRequest.getEid() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "数据为空");
        }

        scoreHistoryService.exportChineseExcel(response, scoreFilterRequest);

        return ResultUtils.success(true);
    }

    /**
     * 导出俄方教师排名信息
     */
    @PostMapping("/rank/russian/export/excel")
    public BaseResponse<Boolean> getRussianExcel(@RequestBody ScoreFilterRequest scoreFilterRequest ,HttpServletResponse response) {
        if (scoreFilterRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "数据为空");
        }
        scoreHistoryService.exportRussianExcel(response, scoreFilterRequest);

        return ResultUtils.success(true);
    }



}
