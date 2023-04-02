package com.itmo.eva.service.impl;

import com.itmo.eva.common.ErrorCode;
import com.itmo.eva.exception.BusinessException;
import com.itmo.eva.mapper.PositionMapper;
import com.itmo.eva.mapper.TitleMapper;
import com.itmo.eva.model.entity.Teacher;
import com.itmo.eva.model.vo.teacher.TeacherVo;
import com.itmo.eva.service.TeacherService;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;

import javax.annotation.Resource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

class TeacherServiceImplTest {

    @Resource
    private TeacherService teacherService;

    @Resource
    private PositionMapper positionMapper;

    @Resource
    private TitleMapper titleMapper;

    @Test
    void listTeacherSQL() {
        List<Teacher> teacherList = teacherService.list();
        if (teacherList == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "没有数据记录");
        }

        List<TeacherVo> teacherVoList = teacherList.stream().map((teacher) -> {
            TeacherVo teacherVo = new TeacherVo();
            BeanUtils.copyProperties(teacher, teacherVo);
            teacherVo.setPosition(positionMapper.getPositionNameById(teacher.getPosition()));
            teacherVo.setTitle(titleMapper.getTitleNameById(teacher.getTitle()));
            return teacherVo;
        }).collect(Collectors.toList());

        System.out.println(teacherList);
    }

    @Test
    void validatorTime() {
        String oriDateStr = "2022-10-21";
        String pattern = "yyyy-MM-dd";
        if (StringUtils.isBlank(oriDateStr) || StringUtils.isBlank(pattern)) {
            System.out.println(false);
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);

        try {
            Date date = dateFormat.parse(oriDateStr);
            System.out.println(oriDateStr.equals(dateFormat.format(date)));
        } catch (ParseException e) {
            System.out.println(false);
        }
    }

    private static final JavaMailSenderImpl sender = new JavaMailSenderImpl();

    static {

        //服务器
        sender.setHost("smtp.qq.com");
        //协议
        sender.setProtocol("smtps");
        //端口号
        sender.setPort(465);
        //邮箱账号
        sender.setUsername("2473159069@qq.com");
        //邮箱授权码
        sender.setPassword("pglxksrspsnudjhh");
        //编码
        sender.setDefaultEncoding("Utf-8");
        Properties p = new Properties();
        p.setProperty("mail.smtp.ssl.enable", "true");
        sender.setJavaMailProperties(p);
    }


    @Test
    void sendMail() throws MessagingException {
        //复杂邮件
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("2473159069@qq.com");
        message.setTo("22320328@hdu.edu.cn");
        message.setSubject("Happy New Year");
        message.setText("新年快乐！");
        String content = "陈琪凯" + "，你好, 您的验证码如下<br/>" + "250" + "<p> 您不需要回复这封邮件。<p/>";

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
        try {
            Date time = dateFormat.parse("2023-3-24 22-30-00");
            message.setSentDate(time);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        sender.send(message);

    }

}