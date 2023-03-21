package com.itmo.eva.service.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.math.BigDecimal;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itmo.eva.common.ErrorCode;
import com.itmo.eva.exception.BusinessException;
import com.itmo.eva.mapper.*;
import com.itmo.eva.model.dto.score.ScoreFilterRequest;
import com.itmo.eva.model.dto.system.SecondSystemQueryRequest;
import com.itmo.eva.model.entity.*;
import com.itmo.eva.model.entity.System;
import com.itmo.eva.model.vo.ScoreHistoryVo;
import com.itmo.eva.model.vo.score.TeacherAllScoreVo;
import com.itmo.eva.model.vo.score.TeacherSecondScoreVo;
import com.itmo.eva.model.vo.score.TeacherSystemScoreVo;
import com.itmo.eva.model.vo.system.FirstSystemScoreVo;
import com.itmo.eva.model.vo.system.SecondSystemScoreVo;
import com.itmo.eva.service.ScoreHistoryService;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author chenjiahan
 * @description 针对表【e_score_history(总分表)】的数据库操作Service实现
 * @createDate 2023-01-25 13:28:07
 */
@Service
@Slf4j
public class ScoreHistoryServiceImpl extends ServiceImpl<ScoreHistoryMapper, ScoreHistory>
        implements ScoreHistoryService {

    @Resource
    private EvaluateMapper evaluateMapper;

    @Resource
    private AverageScoreMapper averageScoreMapper;

    @Resource
    private MarkHistoryMapper markHistoryMapper;

    @Resource
    private TeacherMapper teacherMapper;

    @Resource
    private SystemMapper systemMapper;

    @Resource
    private TableMapper tableMapper;

    @Resource
    private WeightMapper weightMapper;

    /**
     * 获取教师总分数
     * 从average_score表中取数据
     *
     * @param identity
     * @return
     */
    private List<TeacherAllScoreVo> getTeacherRank(Integer identity) {
        List<TeacherAllScoreVo> teacherAllScoreVoList = new ArrayList<>();
        // 获取教师id
        List<Integer> teacherIdList = teacherMapper.getTeacherIdsByIdentity(identity);
        // 获取一级评价指标
        List<System> systemList = systemMapper.getCountByKind(identity);
        LambdaQueryWrapper<AverageScore> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(AverageScore::getTid, teacherIdList);
        List<AverageScore> allTeacherScoreList = averageScoreMapper.selectList(queryWrapper);
        // 教师id -> 教师分数细则
        Map<Long, List<AverageScore>> teacherScoreMap = allTeacherScoreList.stream().collect(Collectors.groupingBy(AverageScore::getTid));
        for (Long teacherId : teacherScoreMap.keySet()) {
            List<AverageScore> teacherScoreList = teacherScoreMap.get(teacherId);
            List<FirstSystemScoreVo> firstSystemScoreVoList = new ArrayList<>();
            for (System system : systemList) {
                Integer sid = system.getId();
                // 获取教师在一级评价下的所有分数
                List<Integer> teacherAllSystemScoreList = teacherScoreList.stream().filter(teacher -> Objects.equals(teacher.getSid(), sid)).map(AverageScore::getScore).collect(Collectors.toList());
                // 计算教师该一级评价
                double averageScore = teacherAllSystemScoreList.stream().mapToDouble(Integer::doubleValue).average().orElse(0);
                FirstSystemScoreVo firstSystemScoreVo = new FirstSystemScoreVo();
                firstSystemScoreVo.setName(system.getName());
                firstSystemScoreVo.setSid(sid);
                firstSystemScoreVo.setScore(averageScore);
                firstSystemScoreVoList.add(firstSystemScoreVo);
            }
            String teacherName = teacherMapper.getNameById(teacherId);
            TeacherAllScoreVo teacherAllScoreVo = new TeacherAllScoreVo();
            teacherAllScoreVo.setName(teacherName);
            teacherAllScoreVo.setScoreList(firstSystemScoreVoList);
            // 计算总分
            double totalScore = firstSystemScoreVoList.stream().mapToDouble(FirstSystemScoreVo::getScore).sum();
            teacherAllScoreVo.setTotalScore(totalScore);
            teacherAllScoreVoList.add(teacherAllScoreVo);
        }

        return teacherAllScoreVoList;
    }

    /**
     * 获取教师具体分数(各项二级指标分数）
     * 从average_score表和second_score中取数据
     *
     * @return
     */
    public List<TeacherSystemScoreVo> getTeacherFirstRank(ScoreFilterRequest scoreFilterRequest) {
        Integer eid = scoreFilterRequest.getEid();
        Integer sid = scoreFilterRequest.getSid();
        Integer identity = scoreFilterRequest.getIdentity();

        // 判断评测是否在进行中
        Evaluate evaluate = evaluateMapper.selectById(eid);
        if (evaluate == null || evaluate.getStatus() == 1) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "评测不存在或正在进行中");
        }

        List<TeacherSystemScoreVo> teacherSystemScoreVoList = new ArrayList<>();
        // 获取教师id
        List<Integer> teacherIdList = teacherMapper.getTeacherIdsByIdentity(identity);
        // 获取该一级评价下所有的二级评价指标
        List<System> secondSystemList = systemMapper.getSecondSystemBySid(sid);
        // 获取教师表中一级指标数据
        LambdaQueryWrapper<AverageScore> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AverageScore::getEid, eid)
                .eq(AverageScore::getSid, sid)
                .in(AverageScore::getTid, teacherIdList);
        // 获取所有教师一级评价所有分数
        List<AverageScore> teacherFirstScoreList = averageScoreMapper.selectList(queryWrapper);
        // 获取教师中所有二级指标分数
        Map<Long, List<AverageScore>> teacherFirstScoreMap = teacherFirstScoreList.stream().collect(Collectors.groupingBy(AverageScore::getTid));
        for (Long teacherId : teacherFirstScoreMap.keySet()) {
            TeacherSystemScoreVo teacherSystemScoreVo = new TeacherSystemScoreVo();
            List<SecondSystemScoreVo> secondSystemScoreList = new ArrayList<>();

            // 计算所有二级指标平均分数
            for (System system : secondSystemList) {
                SecondSystemScoreVo secondSystemScore = new SecondSystemScoreVo();

                Integer secondId = system.getId();
                SecondSystemQueryRequest secondSystemQueryRequest = new SecondSystemQueryRequest();
                secondSystemQueryRequest.setSecondId(secondId);
                secondSystemQueryRequest.setTeacherId(teacherId.intValue());
                secondSystemQueryRequest.setTableName("e_second_score_" + eid);
                // 查找教师二级指标分数
                List<Integer> secondScoreList = tableMapper.getSecond(secondSystemQueryRequest);
                double averageScore = secondScoreList.stream().mapToDouble(Integer::doubleValue).average().orElse(0);
                secondSystemScore.setName(system.getName());
                secondSystemScore.setSid(secondId);
                secondSystemScore.setScore(averageScore);

                secondSystemScoreList.add(secondSystemScore);
            }

            // 获取该教师下本次评测所有一级评价分数吧
            List<Integer> teacherFirstScore = teacherFirstScoreMap.get(teacherId).stream().map(AverageScore::getScore).collect(Collectors.toList());
            double averageScore = teacherFirstScore.stream().mapToDouble(Integer::doubleValue).average().orElse(0);

            String teacherName = teacherMapper.getNameById(teacherId);
            teacherSystemScoreVo.setName(teacherName);
            teacherSystemScoreVo.setScoreList(secondSystemScoreList);
            teacherSystemScoreVo.setTotalScore(averageScore);
            teacherSystemScoreVoList.add(teacherSystemScoreVo);
        }


        return teacherSystemScoreVoList;

    }

    /**
     * 获取教师二级指标
     * 从average_score表和second_score中取数据
     *
     * @return
     */
    @Override
    public List<TeacherSecondScoreVo> getTeacherSecondScore(ScoreFilterRequest scoreFilterRequest) {
        Integer eid = scoreFilterRequest.getEid();
        Integer secondId = scoreFilterRequest.getSecondId();
        Integer identity = scoreFilterRequest.getIdentity();
        // 判断评测是否在进行中
        Evaluate evaluate = evaluateMapper.selectById(eid);
        if (evaluate == null || evaluate.getStatus() == 1) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "评测不存在或正在进行中");
        }
        List<TeacherSecondScoreVo> teacherSecondScoreVoList = new ArrayList<>();
        String tableName = "e_second_score_" + eid;
        String systemName = systemMapper.getChineseNameById(secondId);
        // 获取教师id
        List<Integer> teacherIdList = teacherMapper.getTeacherIdsByIdentity(identity);
        for (Integer teacherId : teacherIdList) {
            TeacherSecondScoreVo teacherSecondScoreVo = new TeacherSecondScoreVo();
            SecondSystemQueryRequest secondSystemQueryRequest = new SecondSystemQueryRequest();
            secondSystemQueryRequest.setSecondId(secondId);
            secondSystemQueryRequest.setTeacherId(teacherId);
            secondSystemQueryRequest.setTableName(tableName);
            // 获取该教师所有二级评测分数
            List<Integer> secondScore = tableMapper.getSecond(secondSystemQueryRequest);
            double averageScore = secondScore.stream().mapToDouble(Integer::doubleValue).average().orElse(0);
            String teacherName = teacherMapper.getNameById(teacherId.longValue());
            teacherSecondScoreVo.setName(teacherName);
            teacherSecondScoreVo.setSystemName(systemName);
            teacherSecondScoreVo.setScore(averageScore);

            teacherSecondScoreVoList.add(teacherSecondScoreVo);
        }
        return teacherSecondScoreVoList;
    }

    @Override
    public void exportExcel(HttpServletResponse response, ScoreFilterRequest scoreFilterRequest) {
        // 根据scoreFilterRequest的传值来判断值
        Integer mode = this.isMode(scoreFilterRequest);
        switch (mode) {
            case 1:
                this.allScoreRankExcelImport(response, scoreFilterRequest);
                break;
            case 2:
                this.FirstScoreRankExcelImport(response, scoreFilterRequest);
                break;
            case 3:
                this.secondScoreRankExcelImport(response, scoreFilterRequest);
                break;
        }
    }


    /**
     * 计算平均分
     * mark_history表 => score_history表
     *
     * @param eid 评测id
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void calculateScoreAverage(Integer eid) {
        // 1. 获取所有教师信息
        List<Teacher> teacherList = teacherMapper.selectList(null);

        // 2. 遍历所有教师，计算每个教师的一级评论分数
        for (Teacher teacher : teacherList) {
            // 3. 获取教师国籍，来锁定教师所属的一级评论
            Integer identity = teacher.getIdentity();
            int teacherId = teacher.getId().intValue();
            // 该教师下的所有一级评论
            List<System> firstSystemList = systemMapper.getCountByKind(identity);
            for (System firstSystem : firstSystemList) {
                Integer systemId = firstSystem.getId();
                LambdaQueryWrapper<MarkHistory> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.eq(MarkHistory::getEid, eid)
                        .eq(MarkHistory::getTid, teacherId)
                        .eq(MarkHistory::getSid, systemId)
                        .eq(MarkHistory::getState, 1);      // 保证平均分不会参杂未评价信息导致分数误差
                // 4. 获取教师某个一级评价下的所有数据
                List<MarkHistory> markHistoryList = markHistoryMapper.selectList(queryWrapper);
                if (CollectionUtils.isEmpty(markHistoryList)) {
                    log.error("在id为{}的评测中，{}教师平均分计算中，无{}这一级评价的相关分数", eid, teacher.getName(), firstSystem.getName());
                    continue;
                }
                // 取出所有分数
                List<Integer> scoreList = markHistoryList.stream().map(MarkHistory::getScore).collect(Collectors.toList());
                // 5. 计算平均分
                OptionalDouble optionalAverage = scoreList.stream().mapToDouble(Integer::doubleValue).average();
                // 将平均分储存到average_score表中
                if (optionalAverage != null && optionalAverage.isPresent()) {
                    double average = optionalAverage.getAsDouble();     // 平均分
                    AverageScore averageScore = new AverageScore();
                    averageScore.setTid((long) teacherId);
                    averageScore.setSid(systemId);
                    averageScore.setScore((int) average);
                    averageScore.setEid(eid);
                    averageScoreMapper.insert(averageScore);
                }
            }
        }
    }

    /**
     * 保存数据到总分表中
     * e_mark_history => e_score_history表中
     *
     * @param eid
     */
    @Override
    public void saveTotalScore(Integer eid) {
        // 取出所有的权重
        List<Weight> weightList = weightMapper.selectList(null);
        Map<Integer, BigDecimal> weightMap = weightList.stream().collect(Collectors.toMap(Weight::getLid, Weight::getWeight));
        LambdaQueryWrapper<MarkHistory> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(MarkHistory::getEid, eid)
                .eq(MarkHistory::getState, 1);
        // 取出所有教师数据
        List<MarkHistory> markScoreList = markHistoryMapper.selectList(queryWrapper);
        // 根据班级分类总数据
        Map<Integer, List<MarkHistory>> markScoreByCourseMap = markScoreList.stream().collect(Collectors.groupingBy(MarkHistory::getCid));
        for (Integer courseId : markScoreByCourseMap.keySet()) {
            List<MarkHistory> courseAllScoreList = markScoreByCourseMap.get(courseId);
            // 根据教师分类课程数据
            Map<Integer, List<MarkHistory>> CourseScoreByTeacherMap = courseAllScoreList.stream().collect(Collectors.groupingBy(MarkHistory::getTid));
            for (Integer teacherId : CourseScoreByTeacherMap.keySet()) {
                Teacher teacher = teacherMapper.selectById(teacherId);
                // 获取教师该课程下所有的课程一级评价分数
                List<MarkHistory> courseScoreList = CourseScoreByTeacherMap.get(teacherId);
                // 获取教师国籍
                Integer identity = teacher.getIdentity();
                // 教师的一级指标
                List<System> firstSystem = systemMapper.getCountByKind(identity);
                // 根据权重计算一级评价合计总分
                double totalScore = 0;
                for (System system : firstSystem) {
                    Integer sid = system.getId();
                    List<Integer> systemScoreList = courseScoreList.stream()
                            .filter(courseScore -> Objects.equals(courseScore.getSid(), sid))
                            .map(MarkHistory::getScore)
                            .collect(Collectors.toList());
                    // 计算出一级评价平均分
                    double score = systemScoreList.stream().mapToDouble(Integer::doubleValue).average().orElse(0);
                    // 一级评价对应的权重
                    double weight = weightMap.get(sid).doubleValue();
                    // 将所有一级评价分数计算入总分
                    totalScore += weight * score;
                }
                ScoreHistory scoreHistory = new ScoreHistory();
                scoreHistory.setTid(teacherId);
                scoreHistory.setCid(courseId);
                scoreHistory.setScore(new BigDecimal(totalScore));
                scoreHistory.setEid(eid);
                scoreHistory.setIdentity(identity);
                // 将数据插入表中
                this.save(scoreHistory);
            }
        }

    }

    @Override
    public List<TeacherAllScoreVo> getTeacherTotalRank(ScoreFilterRequest scoreFilterRequest) {
        Integer mode = this.isMode(scoreFilterRequest);
        if (mode != 1) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        return this.getTeacherRank(scoreFilterRequest.getIdentity());
    }


    private Integer isMode(ScoreFilterRequest scoreFilterRequest) {
        List<Integer> identityList = Arrays.asList(1, 0);
        Integer eid = scoreFilterRequest.getEid();
        Integer sid = scoreFilterRequest.getSid();
        Integer secondId = scoreFilterRequest.getSecondId();
        Integer identity = scoreFilterRequest.getIdentity();
        if (ObjectUtils.isNull(eid, sid, secondId) && identityList.contains(identity)) {
            return 1;
        }
        if (ObjectUtils.isNull(secondId) && identityList.contains(identity)) {
            return 2;
        }
        return 3;
    }

    /**
     * 导出教师总体Excel
     *
     * @param response
     * @param scoreFilterRequest
     */
    private void allScoreRankExcelImport(HttpServletResponse response, ScoreFilterRequest scoreFilterRequest) {
        Integer identity = scoreFilterRequest.getIdentity();
        // 获取教师所有信息
        List<TeacherAllScoreVo> teacherTotalRankList = this.getTeacherRank(identity);
        // 获取教师一级评价指标信息
        List<System> systemList = systemMapper.getCountByKind(identity);
        if (teacherTotalRankList.isEmpty()) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        // 建立Excel对象，封装数据
        response.setCharacterEncoding("UTF-8");
        // 创建Excel对象
        XSSFWorkbook wb = new XSSFWorkbook();
        // 创建sheet对象
        XSSFSheet sheet = wb.createSheet("教师总分排名信息表");
        // 创建表头
        XSSFRow xssfRow = sheet.createRow(0);
        xssfRow.createCell(0).setCellValue("教师姓名");
        for (int i = 1; i <= systemList.size(); i++) {
            xssfRow.createCell(i).setCellValue(systemList.get(i - 1).getName());
        }
        xssfRow.createCell(systemList.size() + 1).setCellValue("总分");
        // 遍历数据，封装Excel工作对象
        for (TeacherAllScoreVo scoreVo : teacherTotalRankList) {
            XSSFRow dataRow = sheet.createRow(sheet.getLastRowNum() + 1);
            int index = 0;
            dataRow.createCell(index).setCellValue(scoreVo.getName());
            List<FirstSystemScoreVo> scoreList = scoreVo.getScoreList();
            for (FirstSystemScoreVo firstSystemScoreVo : scoreList) {
                dataRow.createCell(++index).setCellValue(firstSystemScoreVo.getScore());
            }
            dataRow.createCell(++index).setCellValue(scoreVo.getTotalScore());
        }
        try {
            // 输出Excel文件
            String filename = "教师分数排名.xlsx";
            response.reset();
            response.addHeader("Access-Control-Expose-Headers", "filetype");
            response.setCharacterEncoding("UTF-8");
            response.setHeader("Content-Disposition", "attachment; fileName=" + java.net.URLEncoder.encode(filename, "UTF-8"));
            OutputStream output = response.getOutputStream();
            wb.write(output);
            output.close();
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "模版下载失败");
        }
    }

    private void FirstScoreRankExcelImport(HttpServletResponse response, ScoreFilterRequest scoreFilterRequest) {
        // 获取教师分数信息
        List<TeacherSystemScoreVo> teacherFirstRankList = this.getTeacherFirstRank(scoreFilterRequest);
        // 获取教师二级评价指标
        List<System> systemList = systemMapper.getSecondSystemBySid(scoreFilterRequest.getSid());
        if (teacherFirstRankList.isEmpty()) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        // 建立Excel对象，封装数据
        response.setCharacterEncoding("UTF-8");
        // 创建Excel对象
        XSSFWorkbook wb = new XSSFWorkbook();
        // 创建sheet对象
        XSSFSheet sheet = wb.createSheet("教师一级指标排名信息表");
        // 创建表头
        XSSFRow xssfRow = sheet.createRow(0);
        xssfRow.createCell(0).setCellValue("教师姓名");
        for (int i = 1; i <= systemList.size(); i++) {
            xssfRow.createCell(i).setCellValue(systemList.get(i - 1).getName());
        }
        xssfRow.createCell(systemList.size() + 1).setCellValue("总分");
        // 遍历数据，封装Excel工作对象
        for (TeacherSystemScoreVo scoreVo : teacherFirstRankList) {
            XSSFRow dataRow = sheet.createRow(sheet.getLastRowNum() + 1);
            int index = 0;
            dataRow.createCell(index).setCellValue(scoreVo.getName());
            List<SecondSystemScoreVo> scoreList = scoreVo.getScoreList();
            for (SecondSystemScoreVo secondSystemScoreVo : scoreList) {
                dataRow.createCell(++index).setCellValue(secondSystemScoreVo.getScore());
            }
            dataRow.createCell(++index).setCellValue(scoreVo.getTotalScore());
        }
        try {
            //输出Excel文件
            String filename = "教师一级分数分数排名.xlsx";
            response.reset();
            response.addHeader("Access-Control-Expose-Headers", "filetype");
            response.setCharacterEncoding("UTF-8");
            response.setHeader("Content-Disposition", "attachment; fileName=" + java.net.URLEncoder.encode(filename, "UTF-8"));
            OutputStream output = response.getOutputStream();
            wb.write(output);
            output.close();
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "模版下载失败");
        }
    }

    private void secondScoreRankExcelImport(HttpServletResponse response, ScoreFilterRequest scoreFilterRequest) {
        List<TeacherSecondScoreVo> teacherSecondScoreList = this.getTeacherSecondScore(scoreFilterRequest);
        String systemName = teacherSecondScoreList.get(0).getSystemName();
        if (teacherSecondScoreList.isEmpty()) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        // 建立Excel对象，封装数据
        response.setCharacterEncoding("UTF-8");
        // 创建Excel对象
        XSSFWorkbook wb = new XSSFWorkbook();
        // 创建sheet对象
        XSSFSheet sheet = wb.createSheet("教师二级指标排名信息表");
        // 创建表头
        XSSFRow xssfRow = sheet.createRow(0);
        xssfRow.createCell(0).setCellValue("教师姓名");
        xssfRow.createCell(1).setCellValue(systemName);
        for (TeacherSecondScoreVo scoreVo : teacherSecondScoreList) {
            XSSFRow dataRow = sheet.createRow(sheet.getLastRowNum() + 1);
            dataRow.createCell(0).setCellValue(scoreVo.getName());
            dataRow.createCell(1).setCellValue(scoreVo.getScore());
        }
        try {
            //输出Excel文件
            String filename = "教师二级分数分数排名.xlsx";
            response.reset();
            response.addHeader("Access-Control-Expose-Headers", "filetype");
            response.setCharacterEncoding("UTF-8");
            response.setHeader("Content-Disposition", "attachment; fileName=" + java.net.URLEncoder.encode(filename, "UTF-8"));
            OutputStream output = response.getOutputStream();
            wb.write(output);
            output.close();
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "模版下载失败");
        }
    }

}
/**
 * 计算平均分的方法
 * 每个老师的每一个以及指标都应当又一个平均分  =》   从mark表中取老师的所有数据
 * 平均分计算完成后将教师的所有一级评价平均分全部存到e_average表中，用eid来标记
 * 排名就根据每一个一级指标的平均分来得出总分传给前端
 * 平均分计算完成后根据红线数据来进行判断教师是否超过红线指标并记录
 */



