package com.itmo.eva.model.dto.email;

import lombok.Data;

import java.io.Serializable;

@Data
public class EmailSendRequest implements Serializable {

    /**
     * 所有的中国teacherId
     */
    private Long[] chineseTeacherId;

    /**
     * 发送的中国时间
     */
    private String chineseTime;

    /**
     * 所有的俄罗斯teacherId
     */
    private Long[] russianTeacherId;

    /**
     * 发送的俄罗斯时间
     */
    private String russianTime;


}
