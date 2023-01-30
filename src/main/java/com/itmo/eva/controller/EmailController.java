package com.itmo.eva.controller;

import com.itmo.eva.common.BaseResponse;
import com.itmo.eva.common.ErrorCode;
import com.itmo.eva.common.ResultUtils;
import com.itmo.eva.exception.BusinessException;
import com.itmo.eva.model.dto.email.EmailSendRequest;
import com.itmo.eva.service.email.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@Slf4j
@RequestMapping("/email")
public class EmailController {

    @Resource
    private EmailService emailService;

    @PostMapping("/send")
    public BaseResponse<Boolean> sendEmail(@RequestBody EmailSendRequest emailSendRequest) {
        Boolean email = emailService.sendEmailToTeacher(emailSendRequest);
        if (!email) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "发送邮件失败！");
        }

        return ResultUtils.success(true);
    }





}
