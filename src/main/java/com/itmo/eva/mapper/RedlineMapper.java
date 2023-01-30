package com.itmo.eva.mapper;

import com.itmo.eva.model.entity.Redline;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;

/**
 * @author chenjiahan
 * @description 针对表【e_redline(红线指标表)】的数据库操作Mapper
 * @createDate 2023-01-29 20:37:22
 * @Entity com.itmo.eva.model.entity.Redline
 */
public interface RedlineMapper extends BaseMapper<Redline> {

    /**
     * 根据一级评价指标id来获取红线
     *
     * @param sid 一级评价
     * @return 红线
     */
    @Select("select * from e_redline where sid = #{sid}")
    Redline getBySid(Integer sid);
}




