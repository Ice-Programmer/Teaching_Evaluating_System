package com.itmo.eva.controller;

import com.itmo.eva.common.BaseResponse;
import com.itmo.eva.common.ErrorCode;
import com.itmo.eva.common.ResultUtils;
import com.itmo.eva.exception.BusinessException;
import com.itmo.eva.model.dto.email.EmailSendRequest;
import com.itmo.eva.model.vo.EmailHistoryVo;
import com.itmo.eva.service.email.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/email")
public class EmailController {

    @Resource
    private EmailService emailService;

    @PostMapping("/send")
    public BaseResponse<Boolean> sendEmail(@RequestBody EmailSendRequest emailSendRequest, @RequestHeader("token") String token) {
        if (ObjectUtils.isEmpty(emailSendRequest)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Boolean email = emailService.sendEmailToTeacher(emailSendRequest, token);
        if (!email) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "发送邮件失败！");
        }

        return ResultUtils.success(true);
    }


    /**
     * 获取邮件发送记录
     * @return 发送记录
     */
    @GetMapping("/get/info")
    public BaseResponse<List<EmailHistoryVo>> getEmailSendInfo() {
        List<EmailHistoryVo> emailSendInfo = emailService.getEmailSendInfo();

        if (CollectionUtils.isEmpty(emailSendInfo)) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "暂无发送记录");
        }

        return ResultUtils.success(emailSendInfo);
    }



}
