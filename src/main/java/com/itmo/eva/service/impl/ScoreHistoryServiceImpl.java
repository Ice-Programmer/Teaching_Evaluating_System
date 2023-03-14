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
     * 获取中方老师所有的分数
     *
     * @return 中方分数
     */
    @Override
    public List<ScoreHistoryVo> getChineseScore(ScoreFilterRequest scoreFilterRequest) {

        // 获取当前评测eid
        Integer eid = scoreFilterRequest.getEid();
        // 判断当前评测是否正在进行
        Integer status = evaluateMapper.getStatusById(eid);
        if (status == 1) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "评测正在进行中");
        }
        // 获取所有的中国老师
        List<Teacher> chineseTeacher = teacherMapper.getChineseTeacher();
        List<ScoreHistoryVo> historyVoList = new ArrayList<>();
        for (Teacher teacher : chineseTeacher) {
            ScoreHistoryVo scoreHistoryVo = new ScoreHistoryVo();
            // 获取教师id
            Long tid = teacher.getId();
            // 在average表中找到对应的所有一级评价分数
            Map<Integer, Integer> detailScore = averageScoreMapper.getByEidAndTid(eid, tid).stream().collect(Collectors.toMap(AverageScore::getSid, AverageScore::getScore));

            Integer score = detailScore.values().stream().reduce(Integer::sum).orElse(0);
            scoreHistoryVo.setDetailScore(detailScore);
            scoreHistoryVo.setTid(tid.intValue());
            scoreHistoryVo.setTeacher(teacher.getName());
            scoreHistoryVo.setScore(new BigDecimal(score / 10.0));
            scoreHistoryVo.setIdentity(teacher.getIdentity());
            historyVoList.add(scoreHistoryVo);
        }

        return historyVoList;
    }

    /**
     * 获取俄方老师所有分数
     *
     * @return 俄方分数
     */
    @Override
    public List<ScoreHistoryVo> getRussianScore(ScoreFilterRequest scoreFilterRequest) {
        Integer eid = scoreFilterRequest.getEid();
        // 判断当前评测是否正在进行
        Integer status = evaluateMapper.getStatusById(eid);
        if (status == 1) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "评测正在进行中");
        }

        // 获取所有的俄方老师
        List<Teacher> russianTeacher = teacherMapper.getRussianTeacher();
        List<ScoreHistoryVo> historyVoList = new ArrayList<>();
        for (Teacher teacher : russianTeacher) {
            ScoreHistoryVo scoreHistoryVo = new ScoreHistoryVo();
            // 获取教师id
            Long tid = teacher.getId();
            // 在average表中找到对应的所有一级评价分数
            Map<Integer, Integer> detailScore = averageScoreMapper.getByEidAndTid(eid, tid).stream().collect(Collectors.toMap(AverageScore::getSid, AverageScore::getScore));

            Integer score = detailScore.values().stream().reduce(Integer::sum).orElse(0);
            scoreHistoryVo.setDetailScore(detailScore);
            scoreHistoryVo.setTid(tid.intValue());
            scoreHistoryVo.setScore(new BigDecimal(score / 10.0));
            scoreHistoryVo.setTeacher(teacher.getName());
            scoreHistoryVo.setIdentity(teacher.getIdentity());
            historyVoList.add(scoreHistoryVo);
        }

        return historyVoList;
    }


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


    /**
     * 导出中方教师排名
     *
     * @param response           响应
     * @param scoreFilterRequest 评测id
     */
    @Override
    public void exportChineseExcel(HttpServletResponse response, ScoreFilterRequest scoreFilterRequest) {
        // 查询所有中方教师排名
        List<ScoreHistoryVo> chineseRank = getChineseScore(scoreFilterRequest);
        if (chineseRank.isEmpty()) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "暂无该评测相关排名");
        }
        // 建立Excel对象，封装数据
        response.setCharacterEncoding("UTF-8");
        // 创建Excel对象
        XSSFWorkbook wb = new XSSFWorkbook();
        // 创建sheet对象
        XSSFSheet sheet = wb.createSheet("中方教师分数排名表");
        // 创建表头
        XSSFRow xssfRow = sheet.createRow(0);
        xssfRow.createCell(0).setCellValue("教师姓名");
        xssfRow.createCell(1).setCellValue("助学态度");
        xssfRow.createCell(2).setCellValue("助学效果");
        xssfRow.createCell(3).setCellValue("总分");
        // 遍历数据，封装Excel工作对象
        for (ScoreHistoryVo score : chineseRank) {
            XSSFRow dataRow = sheet.createRow(sheet.getLastRowNum() + 1);
            // 教师名称
            String teacherName = score.getTeacher();
            // todo 后面这里可能会改造成随着管理员对一级指标的更改，动态更改Excel单元格列展示的内容
            Map<Integer, Integer> detailScore = score.getDetailScore();
            // 第1个一级评价
            Integer first = detailScore.get(1);
            // 第2个一级评价
            Integer second = detailScore.get(2);
            // 总分
            BigDecimal totalScore = score.getScore();
            dataRow.createCell(0).setCellValue(teacherName);
            dataRow.createCell(1).setCellValue(first / 10.0);
            dataRow.createCell(2).setCellValue(second / 10.0);
            dataRow.createCell(3).setCellValue((RichTextString) totalScore);
        }
        // 设置Excel名字，数据类型编码
        try {
            //输出Excel文件
            String filename = "教师分数排名表.xlsx";
            response.reset();
            response.addHeader("Access-Control-Expose-Headers", "filetype");
            response.setCharacterEncoding("UTF-8");
            response.setHeader("Content-Disposition", "attachment; fileName=" + java.net.URLEncoder.encode(filename, "UTF-8"));
            OutputStream output = response.getOutputStream();
            wb.write(output);
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 导出俄方教师排名
     *
     * @param response           响应
     * @param scoreFilterRequest 评测id
     */
    @Override
    public void exportRussianExcel(HttpServletResponse response, ScoreFilterRequest scoreFilterRequest) {
        // 查询所有中方教师排名
        List<ScoreHistoryVo> russianScore = getRussianScore(scoreFilterRequest);
        if (russianScore.isEmpty()) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "暂无该评测相关排名");
        }
        // 建立Excel对象，封装数据
        response.setCharacterEncoding("UTF-8");
        // 创建Excel对象
        XSSFWorkbook wb = new XSSFWorkbook();
        // 创建sheet对象
        XSSFSheet sheet = wb.createSheet("俄方教师分数排名表");
        // 创建表头
        XSSFRow xssfRow = sheet.createRow(0);
        xssfRow.createCell(0).setCellValue("教师姓名");
        xssfRow.createCell(1).setCellValue("教学态度");
        xssfRow.createCell(2).setCellValue("教学能力");
        xssfRow.createCell(3).setCellValue("师生交流");
        xssfRow.createCell(4).setCellValue("教学效果");
        xssfRow.createCell(5).setCellValue("教学内容");
        xssfRow.createCell(6).setCellValue("总分");

        // 遍历数据，封装Excel工作对象
        for (ScoreHistoryVo score : russianScore) {
            XSSFRow dataRow = sheet.createRow(sheet.getLastRowNum() + 1);
            // 教师名称
            String teacherName = score.getTeacher();
            // todo 后面这里可能会改造成随着管理员对一级指标的更改，动态更改Excel单元格列展示的内容
            Map<Integer, Integer> detailScore = score.getDetailScore();
            // 第1个一级评价
            Integer first = detailScore.get(1);
            // 第2个一级评价
            Integer second = detailScore.get(2);
            // 第3个一级评价
            Integer third = detailScore.get(3);
            // 第4个一级评价
            Integer fourth = detailScore.get(4);
            // 第5个一级评价
            Integer fifth = detailScore.get(5);
            // 总分
            BigDecimal totalScore = score.getScore();
            dataRow.createCell(0).setCellValue(teacherName);
            dataRow.createCell(1).setCellValue(first / 10.0);
            dataRow.createCell(2).setCellValue(second / 10.0);
            dataRow.createCell(3).setCellValue(third / 10.0);
            dataRow.createCell(4).setCellValue(fourth / 10.0);
            dataRow.createCell(5).setCellValue(fifth / 10.0);
            dataRow.createCell(6).setCellValue(totalScore.toString());
        }
        // 建立输出流，输出浏览器文件
        OutputStream os = null;
        // 设置Excel名字，数据类型编码
        try {
            String folderPath = "C:\\excel";
            //创建上传文件目录
            File folder = new File(folderPath);
            //如果文件夹不存在创建对应的文件夹
            if (!folder.exists()) {
                folder.mkdirs();
            }
            //设置文件名
            String fileName = "俄方分数排名表" + ".xlsx";
            String savePath = folderPath + File.separator + fileName;
            OutputStream fileOut = new FileOutputStream(savePath);
            wb.write(fileOut);
            fileOut.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (os != null)
                    os.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
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

}


/**
 * 计算平均分的方法
 * 每个老师的每一个以及指标都应当又一个平均分  =》   从mark表中取老师的所有数据
 * 平均分计算完成后将教师的所有一级评价平均分全部存到e_average表中，用eid来标记
 * 排名就根据每一个一级指标的平均分来得出总分传给前端
 * 平均分计算完成后根据红线数据来进行判断教师是否超过红线指标并记录
 */



