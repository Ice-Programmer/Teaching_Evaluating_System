package com.itmo.eva.service;

import com.itmo.eva.model.dto.score.ScoreFilterRequest;
import com.itmo.eva.model.entity.ScoreHistory;
import com.baomidou.mybatisplus.extension.service.IService;
import com.itmo.eva.model.vo.ScoreHistoryVo;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
* @author chenjiahan
* @description 针对表【e_score_history(总分表)】的数据库操作Service
* @createDate 2023-01-25 13:28:07
*/
public interface ScoreHistoryService extends IService<ScoreHistory> {

    /**
     * 获取所有中方教师分数
     * @return 中方分数
     */
    List<ScoreHistoryVo> getChineseScore(ScoreFilterRequest scoreFilterRequest);

    /**
     * 获取所有俄方教师分数
     * @return 俄方分数
     */
    List<ScoreHistoryVo> getRussianScore(ScoreFilterRequest scoreFilterRequest);

    /**
     * 导出中方教师排名
     *
     * @param response 响应
     * @param scoreFilterRequest 评测id
     */
    void exportChineseExcel(HttpServletResponse response, ScoreFilterRequest scoreFilterRequest);

    /**
     * 导出俄方教师排名
     *
     * @param response 响应
     * @param scoreFilterRequest 评测id
     */
    void exportRussianExcel(HttpServletResponse response, ScoreFilterRequest scoreFilterRequest);

    /**
     * 计算评测下的所有教师的平均分
     * @param eid 评测id
     */
    void calculateScoreAverage(Integer eid);
}
