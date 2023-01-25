package com.itmo.eva.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itmo.eva.common.ErrorCode;
import com.itmo.eva.exception.BusinessException;
import com.itmo.eva.mapper.EvaluateMapper;
import com.itmo.eva.model.dto.evaluate.EvaluateAddRequest;
import com.itmo.eva.model.dto.evaluate.EvaluateUpdateRequest;
import com.itmo.eva.model.entity.Evaluate;
import com.itmo.eva.model.vo.EvaluateVo;
import com.itmo.eva.model.vo.StudentVo;
import com.itmo.eva.service.EvaluateService;
import com.itmo.eva.utils.SpecialUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
* @author chenjiahan
* @description 针对表【e_evaluate(评测表)】的数据库操作Service实现
* @createDate 2023-01-23 13:14:03
*/
@Service
public class EvaluateServiceImpl extends ServiceImpl<EvaluateMapper, Evaluate>
    implements EvaluateService {

    @Resource
    private EvaluateMapper evaluateMapper;

    /**
     * 添加评测
     *
     * @param evaluateAddRequest 评测请求体
     * @return 添加成功
     */
    @Override
    public Boolean addEvaluate(EvaluateAddRequest evaluateAddRequest) {
        if (evaluateAddRequest == null || StringUtils.isBlank(evaluateAddRequest.getName())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空！");
        }
        // 判断评测是否存在 【按照名称查询】
        String name = evaluateAddRequest.getName();
        Evaluate oldEvaluate = evaluateMapper.getEvaluateByName(name);
        if (oldEvaluate != null) {
            throw new BusinessException(ErrorCode.DATA_REPEAT, "已存在该测评！");
        }
        // 判断是否有正在进行的评测
        Evaluate evaluateGoing = evaluateMapper.getEvaluateByStatus();
        if (evaluateGoing != null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "已有正在进行的测评！");
        }

        Evaluate evaluate = new Evaluate();
        BeanUtils.copyProperties(evaluateAddRequest, evaluate);

        // 校验数据
        this.validEvaluate(evaluate, true);
        boolean save = this.save(evaluate);

        return save;
    }

    /**
     * 删除评测
     *
     * @param id 删除id
     * @return 删除成功
     */
    @Override
    public Boolean deleteEvaluate(Long id) {
        // 判断是否存在
        Evaluate oldEvaluate = this.getById(id);
        if (oldEvaluate == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "评测不存在");
        }
        // todo 删除该评测下所有的一级评测和结果
        boolean remove = this.removeById(id);

        return remove;
    }

    /**
     * 更新课程
     *
     * @param evaluateUpdateRequest 更新请求体
     * @return 更新成功
     */
    @Override
    public Boolean updateEvaluate(EvaluateUpdateRequest evaluateUpdateRequest) {
        if (evaluateUpdateRequest == null || evaluateUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 判断是否存在
        Integer id =  evaluateUpdateRequest.getId();
        Evaluate oldEvaluate = this.getById(id);
        if (oldEvaluate == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "评测不存在");
        }
        Evaluate evaluate = new Evaluate();
        BeanUtils.copyProperties(evaluateUpdateRequest, evaluate);

        // 参数校验
        this.validEvaluate(evaluate, false);
        boolean update = this.updateById(evaluate);

        return update;
    }

    /**
     * 根据 id 获取
     *
     * @param id id
     * @return 评测信息
     */
    @Override
    public EvaluateVo getEvaluateById(Long id) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "id错误");
        }
        Evaluate evaluate = this.getById(id);
        // 判空
        if (evaluate == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "评测信息不存在");
        }
        EvaluateVo evaluateInfo = new EvaluateVo();
        BeanUtils.copyProperties(evaluate, evaluateInfo);

        return evaluateInfo;
    }

    /**
     * 获取评测列表
     */
    @Override
    public List<EvaluateVo> listEvaluate() {
        List<Evaluate> evaluateList = this.list();
        if (evaluateList == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "没有数据记录");
        }

        List<EvaluateVo> evaluateVoList = evaluateList.stream().map((evaluate) -> {
            EvaluateVo evaluateVo = new EvaluateVo();
            BeanUtils.copyProperties(evaluate, evaluateVo);
            return evaluateVo;
        }).collect(Collectors.toList());
        return evaluateVoList;
    }

    /**
     * 获取完成学生
     * @param eid 评测id
     * @return 完成学生列表
     */
    @Override
    public List<StudentVo> listStudentDone(Integer eid) {
        return null;
    }

    /**
     * 校验
     *
     * @param evaluate 评测信息
     * @param add     是否为创建校验
     */
    @Override
    public void validEvaluate(Evaluate evaluate, boolean add) {
        if (evaluate == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String name = evaluate.getName();
        String create_time = evaluate.getCreate_time();
        String start_time = evaluate.getStart_time();
        String e_time = evaluate.getE_time();
        if (add) {
            if (StringUtils.isAnyBlank(name, create_time, start_time, e_time)) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "数据含空");
            }
        }
    }
}




