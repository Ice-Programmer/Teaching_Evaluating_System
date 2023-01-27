package com.itmo.eva.service.email;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itmo.eva.model.dto.email.EmailSendRequest;

public interface EmailService {


    /**
     * 分发邮件
     * @param emailSendRequest 邮件请求体
     * @return 分发成功
     */
    Boolean sendEmailToTeacher(EmailSendRequest emailSendRequest);
}
