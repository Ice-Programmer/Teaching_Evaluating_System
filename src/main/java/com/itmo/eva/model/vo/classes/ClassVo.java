package com.itmo.eva.model.vo.classes;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class ClassVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 计算机班级
     */
    private List<ComputerClass> computerClass;

    /**
     * 自动化班级
     */
    private List<AutomationClass> automationClass;

}
