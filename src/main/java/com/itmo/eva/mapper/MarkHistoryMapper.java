package com.itmo.eva.mapper;

import com.itmo.eva.model.entity.MarkHistory;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.swagger.models.auth.In;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
* @author chenjiahan
* @description 针对表【e_mark_history(一级指标表)】的数据库操作Mapper
* @createDate 2023-01-25 14:27:22
* @Entity com.itmo.eva.model.entity.MarkHistory
*/
public interface MarkHistoryMapper extends BaseMapper<MarkHistory> {

    /**
     * 根据教师id查找
     * @param tid 教师id
     * @return 一级评价信息
     */
    @Select("select * from e_mark_history where tid = #{tid} and eid = {eid} and sid = {sid}")
    List<MarkHistory> getScoreByTidAndSidAndEid(Integer eid, Integer tid, Integer sid);

    /**
     * 根据eid来查找
     * @param eid 评测id
     * @return
     */
    @Select("select * from e_mark_history where eid = #{eid}")
    List<MarkHistory> getByEid(Integer eid);

    /**
     * 获取本次评测还有未完成的学生名单
     * @param eid 评测id
     * @return 未完成学生名单
     */
    @Select("select aid from e_mark_history where eid = #{eid} and state = 0")
    List<Integer> getByEidAndState(Integer eid);
}




