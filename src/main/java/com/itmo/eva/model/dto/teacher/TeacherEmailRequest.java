package com.itmo.eva.model.dto.teacher;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class TeacherEmailRequest implements Serializable {

    /**
     * 要发送老师的所有id
     */
    private Long teacherId;

}
