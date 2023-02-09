package com.itmo.eva.job;

import com.itmo.eva.mapper.StudentMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 自动增加学生学期
 */
@Component
@Slf4j
public class StudentTimer {
    @Resource
    private StudentMapper studentMapper;

    /**
     * 自动增加学期
     */
    @Scheduled(cron = "0 0 0 1 2,8 ?")
    private void configureTasks() {
        // 学期自动加1
        studentMapper.addGradeByAuto();
        log.info("学期数自动+1");

        // 删除学期数大于8的学生
        studentMapper.deleteStudentGradeThan8();
        log.info("自动删除已不在学校学生");
    }
}
