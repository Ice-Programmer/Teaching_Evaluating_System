package com.itmo.eva.service.impl;

import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itmo.eva.common.ErrorCode;
import com.itmo.eva.exception.BusinessException;
import com.itmo.eva.mapper.PositionMapper;
import com.itmo.eva.mapper.TeacherMapper;
import com.itmo.eva.mapper.TitleMapper;
import com.itmo.eva.model.dto.teacher.TeacherAddRequest;
import com.itmo.eva.model.dto.teacher.TeacherUpdateRequest;
import com.itmo.eva.model.entity.Position;
import com.itmo.eva.model.entity.Teacher;
import com.itmo.eva.model.entity.Title;
import com.itmo.eva.model.enums.GenderEnum;
import com.itmo.eva.model.enums.IdentityEnum;
import com.itmo.eva.model.enums.MajorEnum;
import com.itmo.eva.model.vo.TeacherVo;
import com.itmo.eva.service.TeacherService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
* @author chenjiahan
* @description 针对表【e_teacher(教师表)】的数据库操作Service实现
* @createDate 2023-01-21 13:17:49
*/
@Service
public class TeacherServiceImpl extends ServiceImpl<TeacherMapper, Teacher>
    implements TeacherService {

    @Resource
    private TeacherMapper teacherMapper;

    @Resource
    private PositionMapper positionMapper;

    @Resource
    private TitleMapper titleMapper;

    /**
     * 添加教师
     * @param teacherAddRequest 教师请求体
     * @return 添加成功
     */
    @Override
    public Boolean addTeacher(TeacherAddRequest teacherAddRequest) {
        if (teacherAddRequest == null) {
           throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }

        // 判断该教师是否存在
        String name = teacherAddRequest.getName();
        String email = teacherAddRequest.getEmail();
        if (StringUtils.isAnyBlank(name, email)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Teacher oldTeacher = teacherMapper.getTeacherByNameAndEmail(name, email);
        if (oldTeacher != null) {
            throw new BusinessException(ErrorCode.DATA_REPEAT, "教师信息已存在");
        }
        Teacher teacher = new Teacher();
        BeanUtils.copyProperties(teacherAddRequest, teacher);
        this.validTeacher(teacher, true);   // 校验数据
        boolean save = this.save(teacher);

        return save;
    }

    /**
     * 删除教师
     * @param id 删除id
     * @return 删除成功
     */
    @Override
    public Boolean deleteTeacher(Long id) {
        // 判断是否存在
        Teacher oldTeacher = this.getById(id);
        if (oldTeacher == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "教师不存在");
        }
        boolean remove = this.removeById(id);

        return remove;
    }

    /**
     * 更新教师
     * @param teacherUpdateRequest 更新请求体
     * @return 更新成功
     */
    @Override
    public Boolean updateTeacher(TeacherUpdateRequest teacherUpdateRequest) {
        if (teacherUpdateRequest == null || teacherUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Teacher teacher = new Teacher();
        BeanUtils.copyProperties(teacherUpdateRequest, teacher);
        // 参数校验
        this.validTeacher(teacher, false);
        Long id = teacherUpdateRequest.getId();
        // 判断是否存在
        Teacher oldTeacher = this.getById(id);
        if (oldTeacher == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "教师信息不存在");
        }
        boolean update = this.updateById(teacher);

        return update;
    }

    /**
     * 根据 id 获取
     * @param id id
     * @return 教师信息
     */
    @Override
    public TeacherVo getTeacherById(Long id) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        Teacher teacher = this.getById(id);
        // 判空
        if (teacher == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "教师信息不存在");
        }
        TeacherVo teacherInfo = new TeacherVo();
        BeanUtils.copyProperties(teacher, teacherInfo);
        teacherInfo.setPosition(positionMapper.getPositionNameById(teacher.getPosition()));
        teacherInfo.setTitle(titleMapper.getTitleNameById(teacher.getTitle()));

        return teacherInfo;
    }

    /**
     * 获取教师列表
     * @return 教师列表
     */
    @Override
    public List<TeacherVo> listTeacher() {
        List<Teacher> teacherList = this.list();
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

        return teacherVoList;
    }

    /**
     * 数据校验
     * @param teacher 教师信息
     * @param add 是否为创建校验
     */
    @Override
    public void validTeacher(Teacher teacher, boolean add) {
        if (teacher == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String name = teacher.getName();
        Integer sex = teacher.getSex();
        Integer age = teacher.getAge();
        Integer position = teacher.getPosition();
        Integer title = teacher.getTitle();
        Integer major = teacher.getMajor();
        String email = teacher.getEmail();
        Integer identity = teacher.getIdentity();
        List<Position> positionList = positionMapper.selectList(null);
        Map<Integer, String> positionMap = positionList.stream().collect(Collectors.toMap(Position::getId, Position::getName));
        List<Title> titleList = titleMapper.selectList(null);
        Map<Integer, String> titleMap = titleList.stream().collect(Collectors.toMap(Title::getId, Title::getName));
        // 判断是否为新增操作
        if (add) {
            if (StringUtils.isAnyBlank(name, email) || ObjectUtils.isNull(sex, age, position, title, identity, major)) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR);
            }
        }
        if (sex != null && !GenderEnum.getValues().contains(sex)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "性别不符合要求");
        }
        if (identity != null && !IdentityEnum.getValues().contains(identity)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "国籍不符合要求");
        }
        if (major != null && !MajorEnum.getValues().contains(major)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "专业不符合要求");
        }
        if (position != null && !positionMap.containsKey(position)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "职位不符合要求");
        }
        if (title != null && !titleMap.containsKey(title)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "职称不符合规范");
        }
    }
}




