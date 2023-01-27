package com.itmo.eva.mapper;

import com.itmo.eva.model.entity.Evaluate;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
* @author chenjiahan
* @description 针对表【e_evaluate(评测表)】的数据库操作Mapper
* @createDate 2023-01-23 13:14:03
* @Entity com.itmo.eva.model.entity.Evaluate
*/
public interface EvaluateMapper extends BaseMapper<Evaluate> {

    /**
     * 根据评测名称获取
     * @param name 评测名称
     * @return
     */
    @Select("select * from e_evaluate where name = #{name}")
    Evaluate getEvaluateByName(String name);

    /**
     * 获取正在进行中的评测
     * @return
     */
    @Select("select * from e_evaluate where status = 1")
    Evaluate getEvaluateByStatus();

    /**
     * 根据id来判断评测是否在进行中
     * @param eid
     * @return
     */
    @Select("select status from e_evaluate where id =#{eid}")
    Integer getStatusById(Integer eid);

    /**
     * 获取已经结束的所有评测信息
     */
    @Select("select * from e_evaluate where status = 0")
    List<Evaluate> getAllEndEvaluation();

}




