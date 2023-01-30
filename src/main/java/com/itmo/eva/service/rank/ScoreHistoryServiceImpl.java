package com.itmo.eva.service.rank;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.math.BigDecimal;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itmo.eva.common.ErrorCode;
import com.itmo.eva.exception.BusinessException;
import com.itmo.eva.mapper.*;
import com.itmo.eva.model.dto.score.ScoreFilterRequest;
import com.itmo.eva.model.entity.*;
import com.itmo.eva.model.entity.System;
import com.itmo.eva.model.vo.ScoreHistoryVo;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.OptionalDouble;
import java.util.stream.Collectors;

/**
 * @author chenjiahan
 * @description 针对表【e_score_history(总分表)】的数据库操作Service实现
 * @createDate 2023-01-25 13:28:07
 */
@Service
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
    private RedlineMapper redlineMapper;


    /**
     * 判断是否存在分数
     */
    @PostConstruct
    public void init() {
        // 1.获取所有评测的id 【已经结束】
        List<Evaluate> allEndEvaluationEnd = evaluateMapper.getAllEndEvaluation();
        if (allEndEvaluationEnd == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "暂无相关信息");
        }
        List<Integer> evaluateIds = allEndEvaluationEnd.stream().map(Evaluate::getId).collect(Collectors.toList());
        for (Integer e : evaluateIds) {
            Integer isExit = averageScoreMapper.getByEid(e);
            // 判断是否为空
            if (isExit == null) {
                // 如果为空，计算该评测的平均分
                calculateAverageScore(e);
            }
        }
    }


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
            scoreHistoryVo.setEid(eid);
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
            List<AverageScore> scoreByTid = averageScoreMapper.getByEidAndTid(eid, tid);
            Map<Integer, Integer> detailScore = averageScoreMapper.getByEidAndTid(eid, tid).stream().collect(Collectors.toMap(AverageScore::getSid, AverageScore::getScore));

            Integer score = detailScore.values().stream().reduce(Integer::sum).orElse(0);
            scoreHistoryVo.setDetailScore(detailScore);
            scoreHistoryVo.setTid(tid.intValue());
            scoreHistoryVo.setScore(new BigDecimal(score / 10.0));
            scoreHistoryVo.setEid(eid);
            scoreHistoryVo.setTeacher(teacher.getName());
            scoreHistoryVo.setIdentity(teacher.getIdentity());
            historyVoList.add(scoreHistoryVo);
        }

        return historyVoList;
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
            String fileName = "中方分数排名表" + ".xlsx";
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
     * 计算平均分 【总分】
     *
     * @param eid 评测的id
     */
    private void calculateAverageScore(Integer eid) {
        // 获取所有老师信息
        List<Teacher> teacherList = teacherMapper.selectList(null);

        // 遍历所有教师，来计算每一个老师对应所有一级指标的平均分
        for (Teacher teacher : teacherList) {
            Long tid = teacher.getId();     // 教师id
            Integer identity = teacher.getIdentity();   // 国籍

            // 获取该教师的一级评价内容
            List<System> systemList = systemMapper.getCountByKind(identity);
            for (System system : systemList) {
                Integer sid = system.getSid(); // 一级评价id
                LambdaQueryWrapper<MarkHistory> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.eq(MarkHistory::getEid, eid)   // 某一次评测
                        .eq(MarkHistory::getTid, tid)       // 某一位老师
                        .eq(MarkHistory::getSid, sid);      // 某一个一级指标
                // 这位教师在这一个一级评论下的所有分数list
                List<Integer> scoreList = markHistoryMapper.selectList(queryWrapper).stream().map(MarkHistory::getScore).collect(Collectors.toList());
                // 该教师这一项一级指标的平均值
                OptionalDouble optionalAverage = scoreList.stream().mapToDouble(Integer::doubleValue).average();
                if (optionalAverage != null && optionalAverage.isPresent()) {
                    double average = optionalAverage.getAsDouble();
                    // 判断老师平均分数是否低于了红线指标
                    isRedLine(average, eid);
                    AverageScore averageScore = new AverageScore();
                    averageScore.setTid(tid);
                    averageScore.setScore((int) average);
                    averageScore.setSid(sid);
                    averageScore.setEid(eid);
                    averageScoreMapper.insert(averageScore);
                }
            }

        }

    }

    /**
     * 判断分数是否低于红线分数
     * @param average 某一级评价平均分
     * @param sid 一级评价id
     */
    private void isRedLine(double average, Integer sid) {
        RedlineHistory redlineHistory = new RedlineHistory();

        Redline redline = redlineMapper.getBySid(sid);
        BigDecimal score = redline.getScore();
        score.doubleValue();
        redlineHistory.setTid(0);
        redlineHistory.setGid(0);
        redlineHistory.setCid(0);
        redlineHistory.setScore(new BigDecimal("0"));
        redlineHistory.setHappen_time("");
    }
}




