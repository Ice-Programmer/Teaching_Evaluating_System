package com.itmo.eva.mapper;

import com.itmo.eva.model.entity.Evaluate;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;

/**
* @author chenjiahan
* @description 针对表【e_evaluate(评测表)】的数据库操作Mapper
* @createDate 2023-01-23 13:14:03
* @Entity com.itmo.eva.model.entity.Evaluate
*/
public interface EvaluateMapper extends BaseMapper<Evaluate> {

    @Select("select * from e_evaluate where name = #{name}")
    Evaluate getEvaluateByName(String name);

    @Select("select * from e_evaluate where status = 1")
    Evaluate getEvaluateByStatus();
}




