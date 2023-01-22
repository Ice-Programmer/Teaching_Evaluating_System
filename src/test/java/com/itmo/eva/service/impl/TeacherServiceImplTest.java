package com.itmo.eva.service.impl;

import com.itmo.eva.common.ErrorCode;
import com.itmo.eva.exception.BusinessException;
import com.itmo.eva.mapper.PositionMapper;
import com.itmo.eva.mapper.TitleMapper;
import com.itmo.eva.model.entity.Teacher;
import com.itmo.eva.model.vo.TeacherVo;
import com.itmo.eva.service.TeacherService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeanUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class TeacherServiceImplTest {

    @Resource
    private TeacherService teacherService;

    @Resource
    private PositionMapper positionMapper;

    @Resource
    private TitleMapper titleMapper;

    @Test
    void listTeacherSQL() {
        List<Teacher> teacherList = teacherService.list();
        if (teacherList == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "没有数据记录");
        }

        List<TeacherVo> teacherVoList = teacherList.stream().map((teacher) -> {
            TeacherVo teacherVo = new TeacherVo();
            BeanUtils.copyProperties(teacher, teacherVo);
            teacherVo.setPosition(positionMapper.getPositionNameById(teacher.getPosition()));
            teacherVo.setTitle(titleMapper.getTitleNameById(teacher.getTitle()));
            return teacherVo;
        }).collect(Collectors.toList());

        System.out.println(teacherList);
    }

}