package com.itmo.eva.mapper;

import com.itmo.eva.model.entity.Position;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;

/**
* @author chenjiahan
* @description 针对表【e_position(职位表)】的数据库操作Mapper
* @createDate 2023-01-21 14:13:23
* @Entity com.itmo.eva.model.entity.Position
*/
public interface PositionMapper extends BaseMapper<Position> {

    @Select("select name from e_position where id = #{id}")
    String getPositionNameById(Integer id);
}




