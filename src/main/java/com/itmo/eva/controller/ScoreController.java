package com.itmo.eva.controller;

import com.itmo.eva.common.BaseResponse;
import com.itmo.eva.common.ErrorCode;
import com.itmo.eva.common.IdRequest;
import com.itmo.eva.common.ResultUtils;
import com.itmo.eva.exception.BusinessException;
import com.itmo.eva.model.vo.ScoreHistoryVo;
import com.itmo.eva.service.rank.ScoreHistoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
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
     * @param idRequest
     * @return
     */
    @PostMapping("/rank/chinese")
    public BaseResponse<List<ScoreHistoryVo>> getChineseRank(@RequestBody IdRequest idRequest) {
        Long eid = idRequest.getId();
        List<ScoreHistoryVo> chineseScore = scoreHistoryService.getChineseScore(eid.intValue());
        if (chineseScore.isEmpty()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "数据为空");
        }
        return ResultUtils.success(chineseScore);
    }

    /**
     * 获取俄方教师排名
     * @param idRequest
     * @return
     */
    @PostMapping("/rank/russian")
    public BaseResponse<List<ScoreHistoryVo>> getRussianRank(@RequestBody IdRequest idRequest) {
        Long eid = idRequest.getId();
        List<ScoreHistoryVo> russianScore = scoreHistoryService.getRussianScore(eid.intValue());
        if (russianScore.isEmpty()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "数据为空");
        }
        return ResultUtils.success(russianScore);
    }



}
