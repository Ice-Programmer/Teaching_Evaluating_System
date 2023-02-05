package com.itmo.eva.service;

import com.itmo.eva.model.dto.evaluate.EvaluateAddRequest;
import com.itmo.eva.model.dto.evaluate.EvaluateUpdateRequest;
import com.itmo.eva.model.entity.Evaluate;
import com.baomidou.mybatisplus.extension.service.IService;
import com.itmo.eva.model.vo.Evaluation.EvaluateVo;
import com.itmo.eva.model.vo.Evaluation.StudentCompletionVo;
import com.itmo.eva.model.vo.StudentVo;
import com.sun.org.apache.xpath.internal.operations.Bool;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
* @author chenjiahan
* @description 针对表【e_evaluate(评测表)】的数据库操作Service
* @createDate 2023-01-23 13:14:03
*/
public interface EvaluateService extends IService<Evaluate> {
    /**
     * 添加评测
     *
     * @param evaluateAddRequest 评测请求体
     * @return 添加成功
     */
    Boolean addEvaluate(EvaluateAddRequest evaluateAddRequest);

    /**
     * 删除评测
     *
     * @param id 删除id
     * @return 删除成功
     */
    Boolean deleteEvaluate(Long id);

    /**
     * 更新评测
     *
     * @param evaluateUpdateRequest 更新请求体
     * @return 更新成功
     */
    Boolean updateEvaluate(EvaluateUpdateRequest evaluateUpdateRequest);

    /**
     * 根据 id 获取
     *
     * @param id id
     * @return 评测信息
     */
    EvaluateVo getEvaluateById(Integer id);

    /**
     * 获取评测列表
     */
    List<EvaluateVo> listEvaluate();

    /**
     * 更改评测状态
     */
    Boolean updateStatus(Integer eid, String token);


    /**
     * 获取完成学生情况
     */
    StudentCompletionVo listStudentCompletion(Integer eid);

    Boolean exportUndoneStudentExcel(Integer eid, HttpServletResponse response);

    /**
     * 校验
     *
     * @param evaluate 评测信息
     * @param add     是否为创建校验
     */
    void validEvaluate(Evaluate evaluate, boolean add);

}
