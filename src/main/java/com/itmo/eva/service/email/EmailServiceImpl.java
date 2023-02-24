package com.itmo.eva.service.email;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.google.gson.Gson;
import com.itmo.eva.common.ErrorCode;
import com.itmo.eva.exception.BusinessException;
import com.itmo.eva.mapper.AdminMapper;
import com.itmo.eva.mapper.EmailHistoryMapper;
import com.itmo.eva.mapper.TeacherMapper;
import com.itmo.eva.model.dto.email.EmailSendRequest;
import com.itmo.eva.model.entity.Admin;
import com.itmo.eva.model.entity.EmailHistory;
import com.itmo.eva.model.enums.EmailStateEnum;
import com.itmo.eva.model.vo.EmailHistoryVo;
import com.itmo.eva.utils.JwtUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmailServiceImpl implements EmailService{

    @Resource
    private TeacherMapper teacherMapper;

    @Resource
    private AdminMapper adminMapper;

    @Resource
    private EmailHistoryMapper emailHistoryMapper;

    @Override
    public Boolean sendEmailToTeacher(EmailSendRequest emailSendRequest, String token){
        // 对传回来的token进行解析 -> 解析出token中对应用户的id
        DecodedJWT decodedJWT = JwtUtil.decodeToken(token);
        Integer id = Integer.valueOf(decodedJWT.getClaim("id").asString());

        // 获取操作人信息
        Admin admin = adminMapper.selectById(id);
        String username = admin.getUsername();

        Long[] chineseTeacherId = emailSendRequest.getChineseTeacherId();
        String chineseTime = emailSendRequest.getChineseTime();

        sendEmailBatch(chineseTeacherId, chineseTime, false, username);

        Long[] russianTeacherId = emailSendRequest.getRussianTeacherId();
        String russianTime = emailSendRequest.getRussianTime();

        sendEmailBatch(russianTeacherId, russianTime, true, username);

        return true;
    }

    @Override
    public List<EmailHistoryVo> getEmailSendInfo() {
        List<EmailHistory> emailHistoryList = emailHistoryMapper.selectList(null);
        List<EmailHistoryVo> emailHistoryVoList = emailHistoryList.stream().map(emailHistory -> {
            EmailHistoryVo emailHistoryVo = new EmailHistoryVo();
            BeanUtils.copyProperties(emailHistory, emailHistoryVo);
            Integer state = emailHistory.getState();
            emailHistoryVo.setState(EmailStateEnum.getEnumByValue(state).getText());
            return emailHistoryVo;
        }).collect(Collectors.toList());

        if (CollectionUtils.isEmpty(emailHistoryList)) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "暂无发送信息");
        }
        return emailHistoryVoList;
    }

    /**
     * 批量发送邮件
     * @param teacherId 教师id
     * @param time 时间
     * @param Russian 是否发送给俄罗斯老师
     */
    private void sendEmailBatch(Long[] teacherId, String time, boolean Russian, String username) {

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

            // 记录发送邮件记录
            EmailHistory emailHistory = new EmailHistory();
            emailHistory.setName(username);
            emailHistory.setOperation("提交了意见反馈");
            emailHistory.setState(0);
            String operationTime = dateFormat.format(Calendar.getInstance().getTime());
            emailHistory.setOperationTime(operationTime);
            String sendTime = dateFormat.format(calendar.getTime());
            emailHistory.setSubmit_time(sendTime);
            Gson gson = new Gson();
            String recipient = gson.toJson(teacherId);
            emailHistory.setRecipient(recipient);

            emailHistoryMapper.insert(emailHistory);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }


}
