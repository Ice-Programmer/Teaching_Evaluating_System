package com.itmo.eva.mapper;

import com.itmo.eva.model.entity.MarkHistory;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
* @author chenjiahan
* @description 针对表【e_mark_history(一级指标表)】的数据库操作Mapper
* @createDate 2023-01-25 14:27:22
* @Entity com.itmo.eva.model.entity.MarkHistory
*/
public interface MarkHistoryMapper extends BaseMapper<MarkHistory> {

    @Select("select * from e_mark_history where tid = #{tid}")
    List<MarkHistory> getScoreByTid(Integer tid);
}




