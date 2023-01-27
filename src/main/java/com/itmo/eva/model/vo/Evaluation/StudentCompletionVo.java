package com.itmo.eva.model.vo.Evaluation;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 学生评测完成情况
 */
@Data
public class StudentCompletionVo implements Serializable {

    /**
     * 完成学生名单
     */
    private List<StudentEvaVo> studentDone;

    /**
     * 未完成学生名单
     */
    private List<StudentEvaVo> studentUndone;

}
