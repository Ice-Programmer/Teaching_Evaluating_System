package com.itmo.eva.service.impl;

import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itmo.eva.common.ErrorCode;
import com.itmo.eva.exception.BusinessException;
import com.itmo.eva.model.dto.student.StudentAddRequest;
import com.itmo.eva.model.dto.student.StudentUpdateRequest;
import com.itmo.eva.model.entity.Position;
import com.itmo.eva.model.entity.Student;
import com.itmo.eva.model.entity.Title;
import com.itmo.eva.model.enums.GenderEnum;
import com.itmo.eva.model.enums.IdentityEnum;
import com.itmo.eva.model.enums.MajorEnum;
import com.itmo.eva.model.vo.StudentVo;
import com.itmo.eva.service.StudentService;
import com.itmo.eva.mapper.StudentMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author chenjiahan
 * @description 针对表【e_student(学生表)】的数据库操作Service实现
 * @createDate 2023-01-22 10:14:33
 */
@Service
public class StudentServiceImpl extends ServiceImpl<StudentMapper, Student>
        implements StudentService {

    @Resource
    private StudentMapper studentMapper;


    /**
     * 增加学生
     *
     * @param studentAddRequest 学生请求体
     * @return 增加成功
     */
    @Override
    public Boolean addStudent(StudentAddRequest studentAddRequest) {
        if (studentAddRequest == null || ObjectUtils.isNull(studentAddRequest.getSid())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }

        // 判断学生是否存在 【根据学号】
        String sid = studentAddRequest.getSid();
        Student oldStudent = studentMapper.getStudentBySid(sid);
        return null;
    }

    @Override
    public Boolean deleteStudent(Long id) {
        return null;
    }

    @Override
    public Boolean updateStudent(StudentUpdateRequest studentUpdateRequest) {
        return null;
    }

    @Override
    public StudentVo getStudentById(Long id) {
        return null;
    }

    @Override
    public List<StudentVo> listStudent() {
        return null;
    }

    @Override
    public void validStudent(Student student, boolean add) {

        if (student == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String sid = student.getSid();
        String password = student.getPassword();
        String name = student.getName();
        Integer sex = student.getSex();
        Integer age = student.getAge();
        Integer major = student.getMajor();
        Integer cid = student.getCid();
        Integer grade = student.getGrade();

        // 判断是否为新增操作
        if (add) {
            if (StringUtils.isAnyBlank(name, sid, password) || ObjectUtils.isNull(sex, age, cid, major, grade)) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR);
            }
        }
        if (sex != null && !GenderEnum.getValues().contains(sex)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "性别不符合要求");
        }
        if (major != null && !MajorEnum.getValues().contains(major)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "专业不符合要求");
        }
        if (grade != null && !(grade > 0 && grade <= 8)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "年级不符合要求");
        }
    }
}




