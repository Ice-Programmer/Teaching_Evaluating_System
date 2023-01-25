package com.itmo.eva.controller;

import com.itmo.eva.common.BaseResponse;
import com.itmo.eva.common.ResultUtils;
import com.itmo.eva.model.dto.teacher.TeacherEmailRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/email")
public class EmailController {

    @PostMapping("/send")
    public BaseResponse<Boolean> sendEmail(@RequestBody List<Long> teacherEmailRequests) {
        return ResultUtils.success(true);
    }




}
