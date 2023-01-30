package com.itmo.eva.service.email;

import com.itmo.eva.mapper.TeacherMapper;
import com.itmo.eva.model.dto.email.EmailSendRequest;
import com.itmo.eva.model.entity.Teacher;
import com.itmo.eva.utils.MailUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class EmailServiceImpl implements EmailService{

    @Resource
    private TeacherMapper teacherMapper;

    @Override
    public Boolean sendEmailToTeacher(EmailSendRequest emailSendRequest){
        Long[] chineseTeacherId = emailSendRequest.getChineseTeacherId();
        String chineseTime = emailSendRequest.getChineseTime();

        sendEmailBatch(chineseTeacherId, chineseTime, false);

        Long[] russianTeacherId = emailSendRequest.getRussianTeacherId();
        String russianTime = emailSendRequest.getRussianTime();

        sendEmailBatch(russianTeacherId, russianTime, true);

        return true;
    }

    /**
     * 批量发送邮件
     * @param teacherId 教师id
     * @param time 时间
     * @param Russian 是否发送给俄罗斯老师
     */
    private void sendEmailBatch(Long[] teacherId, String time, boolean Russian) {
        // 找出教师信息
        List<Teacher> teacherList = teacherMapper.selectBatchIds(Arrays.asList(teacherId));

        SimpleDateFormat dateFormat= new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");

        // 将字符串转为Date类型
        try {
            Date date = dateFormat.parse(time);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);

            if (Russian) {
                // 如果未俄罗斯邮件，时间往后推迟7小时发送
                calendar.add(Calendar.HOUR, 7);
            }

            MailUtil.sendEmail(teacherList, calendar.getTime());


        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }


}
