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

    /**
     * 根据教师id来查找
     * @param tid 教师id
     * @return 分数
     */
    @Select("select * from e_average_score where tid = #{tid}")
    List<AverageScore> getScoreByTid(Long tid);

    /**
     * 判断是否拥有数据
     */
    @Select("select COUNT(*) from e_average_score where eid = #{eid}")
    Integer getByEid(Integer eid);

    @Select("select * from e_average_score where eid = #{eid} and tid = #{tid}")
    List<AverageScore> getByEidAndTid(Integer eid, Long tid);

}




