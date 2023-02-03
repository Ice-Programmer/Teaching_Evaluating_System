package com.itmo.eva.service.email;

import com.itmo.eva.model.dto.email.EmailSendRequest;
import com.itmo.eva.model.vo.EmailHistoryVo;

import java.util.List;

public interface EmailService {


    /**
     * 分发邮件
     *
     * @param emailSendRequest 邮件请求体
     * @param token token
     * @return 分发成功
     */
    Boolean sendEmailToTeacher(EmailSendRequest emailSendRequest, String token);

    /**
     * 获取发送邮件记录
     * @return 邮件发送记录
     */
    List<EmailHistoryVo> getEmailSendInfo();
}
