package com.itmo.eva.service.email;

import com.itmo.eva.model.dto.email.EmailSendRequest;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService{


    @Override
    public Boolean sendEmailToTeacher(EmailSendRequest emailSendRequest) {
        Long[] chineseTeacherId = emailSendRequest.getChineseTeacherId();
        Long[] russianTeacherId = emailSendRequest.getRussianTeacherId();

        return null;
    }
}
