package com.itmo.eva.mapper;

import com.itmo.eva.model.entity.AverageScore;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
* @author chenjiahan
* @description 针对表【e_average_score(平均分表)】的数据库操作Mapper
* @createDate 2023-01-25 16:02:56
* @Entity com.itmo.eva.model.entity.AverageScore
*/
public interface AverageScoreMapper extends BaseMapper<AverageScore> {

    @Select("select * from e_average_score where tid = #{tid}")
    List<AverageScore> getScoreByTid(Long tid);

}




