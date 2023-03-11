package com.itmo.eva.job;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.google.gson.Gson;

import com.google.gson.reflect.TypeToken;
import com.itmo.eva.mapper.EmailHistoryMapper;
import com.itmo.eva.mapper.TeacherMapper;
import com.itmo.eva.model.entity.EmailHistory;
import com.itmo.eva.model.entity.Teacher;
import com.itmo.eva.utils.MailUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Component
@Slf4j
public class EmailSendJob {

    @Resource
    private EmailHistoryMapper emailHistoryMapper;

    @Resource
    private TeacherMapper teacherMapper;

    /**
     * 每10分钟进行判断邮件发送
     * @throws ParseException
     */
    @Scheduled(cron = "0 */10 * * * ?")
    public void sendEmail() throws ParseException {
        log.info("邮件检测...");
        LambdaQueryWrapper<EmailHistory> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(EmailHistory::getState, 0);
        List<EmailHistory> emailList = emailHistoryMapper.selectList(queryWrapper);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
        if (emailList != null) {
            for (EmailHistory email : emailList) {
                Date sendTime = dateFormat.parse(email.getSubmit_time());
                Date nowTime = Calendar.getInstance().getTime();
                if (nowTime.after(sendTime)) {
                    // 执行邮件发送
                    Gson gson = new Gson();
                    String recipient = email.getRecipient();
                    Long[] teacherIds = gson.fromJson(recipient, new TypeToken<Long[]>(){}.getType());
                    List<Teacher> teacherList = teacherMapper.selectBatchIds(Arrays.asList(teacherIds));
                    try {
                        MailUtil.sendEmail(teacherList, sendTime);
                        email.setState(1);
                        emailHistoryMapper.updateById(email);
                    } catch (Exception e) {
                        log.error("邮件发送失败, 时间: {}", email.getSubmit_time());
                        email.setState(2);
                        // 返回错误信息
                        emailHistoryMapper.updateById(email);
                    }
                }
            }
        }
    }

}
