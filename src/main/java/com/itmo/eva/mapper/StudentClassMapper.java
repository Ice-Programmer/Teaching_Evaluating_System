package com.itmo.eva.mapper;

import com.itmo.eva.model.entity.StudentClass;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;

/**
* @author chenjiahan
* @description 针对表【e_class(职称表)】的数据库操作Mapper
* @createDate 2023-01-22 18:08:44
* @Entity com.itmo.eva.model.entity.StudentClass
*/
public interface StudentClassMapper extends BaseMapper<StudentClass> {

    @Select("select cid from e_class where id = #{id}")
    String getClassById(Integer id);

}




