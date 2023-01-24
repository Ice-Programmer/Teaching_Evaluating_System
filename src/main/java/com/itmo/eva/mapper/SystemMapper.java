package com.itmo.eva.mapper;

import com.itmo.eva.model.entity.System;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
* @author chenjiahan
* @description 针对表【e_system(评价体系表)】的数据库操作Mapper
* @createDate 2023-01-24 10:02:52
* @Entity com.itmo.eva.model.entity.System
*/
public interface SystemMapper extends BaseMapper<System> {

    /**
     * 获取俄方二级评价
     */
    @Select("select name from e_system where kind = 0 and level = 2 and sid = #{sid}")
    List<String> getRussianSecondSystem(Integer sid);

    /**
     * 获取俄方一级评价
     */
    @Select("select * from e_system where level = 1 and kind = 0")
    List<System> getRussianFirstSystem();

    /**
     * 获取中方二级评价
     */
    @Select("select name from e_system where kind = 1 and level = 2 and sid = #{sid}")
    List<String> getChineseSecondSystem(Integer sid);

    /**
     * 获取中方一级评价
     */
    @Select("select * from e_system where level = 1 and kind = 1")
    List<System> getChineseFirstSystem();


}




