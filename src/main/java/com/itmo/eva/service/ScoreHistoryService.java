package com.itmo.eva.service;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.itmo.eva.model.dto.score.ScoreFilterRequest;
import com.itmo.eva.model.entity.ScoreHistory;
import com.baomidou.mybatisplus.extension.service.IService;
import com.itmo.eva.model.vo.ScoreHistoryVo;
import com.itmo.eva.model.vo.score.TeacherAllScoreVo;
import com.itmo.eva.model.vo.score.TeacherSecondScoreVo;
import com.itmo.eva.model.vo.score.TeacherSystemScoreVo;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @author chenjiahan
 * @description 针对表【e_score_history(总分表)】的数据库操作Service
 * @createDate 2023-01-25 13:28:07
 */
public interface ScoreHistoryService extends IService<ScoreHistory> {

    /**
     * 计算评测下的所有教师的平均分
     *
     * @param eid 评测id
     */
    void calculateScoreAverage(Integer eid);

    /**
     * 平均分保存到总分表中
     *
     * @param eid
     */
    void saveTotalScore(Integer eid);

    /**
     * 获取所有排名
     *
     * @param scoreFilterRequest
     * @return
     */
    List<TeacherAllScoreVo> getTeacherTotalRank(ScoreFilterRequest scoreFilterRequest);

    /**
     * 获取一级评价下的排名
     *
     * @param scoreFilterRequest
     * @return
     */
    List<TeacherSystemScoreVo> getTeacherFirstRank(ScoreFilterRequest scoreFilterRequest);

    /**
     * 获取二级评价
     */
    List<TeacherSecondScoreVo> getTeacherSecondScore(ScoreFilterRequest scoreFilterRequest);

    /**
     * 导出Excel表格
     *
     * @param response
     * @param scoreFilterRequest
     */
    void exportExcel(HttpServletResponse response, ScoreFilterRequest scoreFilterRequest);


    void allScoreRankExcelImport(HttpServletResponse response, ScoreFilterRequest scoreFilterRequest);
}