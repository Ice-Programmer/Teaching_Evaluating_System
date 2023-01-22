package com.itmo.eva.mapper;

import com.itmo.eva.model.entity.Title;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;

/**
* @author chenjiahan
* @description 针对表【e_title(职称表)】的数据库操作Mapper
* @createDate 2023-01-21 15:38:58
* @Entity com.itmo.eva.model.entity.Title
*/
public interface TitleMapper extends BaseMapper<Title> {

    @Select("select name from e_title where id = #{id}")
    String getTitleNameById(Integer id);
}




