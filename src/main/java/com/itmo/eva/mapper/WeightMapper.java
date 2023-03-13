package com.itmo.eva.mapper;

import com.itmo.eva.model.entity.Weight;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;

/**
* @author chenjiahan
* @description 针对表【e_weight(权重表)】的数据库操作Mapper
* @createDate 2023-03-13 10:07:02
* @Entity com.itmo.eva.model.entity.Weight
*/
public interface WeightMapper extends BaseMapper<Weight> {

    @Select("select weight from e_weight where lid = #{sid}")
    Double getWeightBySid(Integer sid);
}




