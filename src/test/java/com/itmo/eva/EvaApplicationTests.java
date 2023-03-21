package com.itmo.eva;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.itmo.eva.mapper.*;
import com.itmo.eva.model.entity.System;
import com.itmo.eva.model.entity.*;
import com.itmo.eva.service.AdminService;
import com.itmo.eva.service.RedlineHistoryService;
import com.itmo.eva.service.ScoreHistoryService;
import com.itmo.eva.service.TeacherService;
import com.itmo.eva.utils.MailUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@SpringBootTest
@Slf4j
class EvaApplicationTests {

    @Resource
    private RedlineHistoryService redlineHistoryService;

    @Resource
    private TeacherService teacherService;

    @Resource
    private MarkHistoryMapper markHistoryMapper;

    @Resource
    private SystemMapper systemMapper;

    @Resource
    private TeacherMapper teacherMapper;

    @Resource
    private AverageScoreMapper averageScoreMapper;


    @Resource
    private StudentMapper studentMapper;

    @Resource
    private CourseMapper courseMapper;

    @Resource
    private WeightMapper weightMapper;

    @Resource
    private ScoreHistoryService scoreHistoryService;

    @Resource
    private EvaluateMapper evaluateMapper;

    @Test
    void contextLoads() {

    }

    @Test
    void getById() {
        String title = "测试批量发送多封邮件";
        String text = "zzy大sui哥";
//		cqkjmx@163.com
//		for (int i = 0; i < 10; i++) {
//			MailUtil.sendMail("3220103081@zju.edu.cn", title, text);
//		}
    }

    @Test
    void selectByBatch() {
        Long[] teacherId = {1L, 2L, 3L};

        List<Teacher> teacherList = teacherMapper.selectBatchIds(Arrays.asList(teacherId));
        Assertions.assertNotNull(teacherList);
    }

    @Test
    void average() {
        Integer eid = 1;
        // 从e_mark_history表中取所有的数据 【每个老师所有的一级评价分数】
        List<MarkHistory> markHistories = markHistoryMapper.getByEid(eid);
//		Map<Integer, Integer> markMap = markHistories.stream().collect(Collectors.toMap(MarkHistory::getSid, MarkHistory::getId));

        // 获取所有老师信息
        List<Teacher> teacherList = teacherMapper.selectList(null);

        // 遍历所有教师，来计算每一个老师对应所有一级指标的平均分
        for (Teacher teacher : teacherList) {
            Long tid = teacher.getId();     // 教师id
            Integer identity = teacher.getIdentity();   // 国籍
            // 获取该教师的一级评价内容
            List<System> systemList = systemMapper.getCountByKind(identity);
            for (System system : systemList) {
                Integer sid = system.getId(); // 一级评价id
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
     * 自动分发测评
     * <p>
     * 警告⚠️： 千万不要轻易运行，后果自负
     */
    @Test
    void handout() {
        List<Student> studentList = studentMapper.selectList(null);

        Integer evaluateId = 24;

        for (Student student : studentList) {
            // 取出grade和major，来查询学生的响应课程
            Integer grade = student.getGrade();
            Integer major = student.getMajor();
            Long studentId = student.getId();

            // 获取该学生的所有课程信息
            List<Course> courseList = courseMapper.getCourseByMajorAndGrade(major, grade);
            for (Course course : courseList) {
                // 取出教师id
                Long teacherId = course.getTid();
                Integer courseId = course.getId();

                // 取出教师的国籍
                Teacher teacher = teacherMapper.selectById(teacherId);
                Integer identity = teacher.getIdentity();

                // 查询教师的国籍，所对应的所有一级指标
                List<System> systemList = systemMapper.getCountByKind(identity);

                for (System system : systemList) {
                    Integer systemId = system.getId();
                    MarkHistory markHistory = new MarkHistory();
                    markHistory.setTid(teacherId.intValue());
                    markHistory.setCid(courseId);
                    markHistory.setEid(evaluateId);
                    markHistory.setScore(0);
                    markHistory.setSid(systemId);
                    markHistory.setAid(studentId.intValue());
                    markHistory.setState(0);
                    // 插入数据库
                    markHistoryMapper.insert(markHistory);
                }


            }
        }
    }

    @Test
    void getWeight() {
        Double weightBySid = weightMapper.getWeightBySid(1);
        log.info("权重为：{}", weightBySid);
//		LambdaQueryWrapper<Weight> queryWrapper = new LambdaQueryWrapper<>();
//		queryWrapper.eq(Weight::getLid, 1);
//		Weight weight = weightMapper.selectOne(queryWrapper);
//		log.info("权重为：{}", weight.getWeight());

    }

    @Test
    void getTotalScore() {
        scoreHistoryService.saveTotalScore(25);
    }

    @Test
    void endEvaluation() {
        log.info("校验评测中...");
        // 获取仍在进行中的评测
        Evaluate evaluateGoing = evaluateMapper.selectById(28);
        if (ObjectUtils.isEmpty(evaluateGoing)) {
            return;
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date nowTime = Calendar.getInstance().getTime();
        // 超出日期将评测的状态关闭
        evaluateGoing.setStatus(0);
//        evaluateMapper.updateById(evaluateGoing);
        // 计算该评测下所有教师的平均分(mark_history表 -> average_score表)
//        scoreHistoryService.calculateScoreAverage(28);
        log.info("{} 评测平均分计算完成", evaluateGoing.getName());
        // 统计红线指标
//        redlineHistoryService.recordRedline(evaluateGoing.getId());
        log.info("{} 评测已经结束，当前时间：{}", evaluateGoing.getName(), dateFormat.format(nowTime));
        // 将教师一级指标平均分折合成总分存入总分表中(mark_history表 -> score_history表)
        scoreHistoryService.saveTotalScore(evaluateGoing.getId());
    }
}
