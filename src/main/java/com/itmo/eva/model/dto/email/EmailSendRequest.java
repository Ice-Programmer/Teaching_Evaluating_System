package com.itmo.eva.model.dto.email;

import lombok.Data;

import java.io.Serializable;

@Data
public class EmailSendRequest implements Serializable {

    /**
     * 所有的teacherId
     */
    private Long[] teacherId;

    /**
     * 发送时间
     */
    private String time;
}
