package com.itmo.eva.service.impl;

import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itmo.eva.common.ErrorCode;
import com.itmo.eva.exception.BusinessException;
import com.itmo.eva.mapper.StudentClassMapper;
import com.itmo.eva.mapper.StudentMapper;
import com.itmo.eva.model.dto.student.StudentAddRequest;
import com.itmo.eva.model.dto.student.StudentUpdateRequest;
import com.itmo.eva.model.entity.Student;
import com.itmo.eva.model.entity.StudentClass;
import com.itmo.eva.model.enums.GenderEnum;
import com.itmo.eva.model.enums.MajorEnum;
import com.itmo.eva.model.vo.StudentVo;
import com.itmo.eva.service.StudentService;
import com.itmo.eva.utils.SpecialUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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

    @Resource
    private StudentClassMapper studentClassMapper;

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
        if (oldStudent != null) {
            throw new BusinessException(ErrorCode.DATA_REPEAT, "学生信息已存在");
        }
        Student student = new Student();
        BeanUtils.copyProperties(studentAddRequest, student);
        String password = DigestUtils.md5DigestAsHex(student.getSid().getBytes(StandardCharsets.UTF_8));
        student.setPassword(password);
        // 校验数据
        this.validStudent(student, true);
        boolean save = this.save(student);

        return save;
    }

    /**
     * 删除学生
     *
     * @param id 删除id
     * @return 删除成功
     */
    @Override
    public Boolean deleteStudent(Long id) {
        // 判断是否存在
        Student oldStudent = this.getById(id);
        if (oldStudent == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "学生不存在");
        }
        boolean remove = this.removeById(id);

        return remove;
    }

    /**
     * 更新学生
     *
     * @param studentUpdateRequest 更新请求体
     * @return 更新成功
     */
    @Override
    public Boolean updateStudent(StudentUpdateRequest studentUpdateRequest) {
        if (studentUpdateRequest == null || studentUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 判断是否存在
        Long id = studentUpdateRequest.getId();
        Student oldStudent = this.getById(id);
        if (oldStudent == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "学生信息不存在");
        }
        Student student = new Student();
        BeanUtils.copyProperties(studentUpdateRequest, student);
        // 参数校验
        this.validStudent(student, false);

        // 判断学号是否修改 => 密码是否修改
        if (!Objects.equals(oldStudent.getSid(), student.getSid())) {
            String password = DigestUtils.md5DigestAsHex(student.getSid().getBytes(StandardCharsets.UTF_8));
            student.setPassword(password);
        }

        boolean update = this.updateById(student);

        return update;
    }

    /**
     * 根据 id 获取
     *
     * @param id id
     * @return 学生信息
     */
    @Override
    public StudentVo getStudentById(Long id) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        Student student = this.getById(id);
        // 判空
        if (student == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "学生信息不存在");
        }
        StudentVo studentInfo = new StudentVo();
        BeanUtils.copyProperties(student, studentInfo);
        // 班级赋值
        studentInfo.setCid(studentClassMapper.getClassById(student.getCid()));

        return studentInfo;
    }

    /**
     * 获取学生列表
     */
    @Override
    public List<StudentVo> listStudent() {
        List<Student> studentList = this.list();
        if (studentList == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "没有数据记录");
        }

        List<StudentClass> classList = studentClassMapper.selectList(null);
        Map<Integer, String> classMap = classList.stream().collect(Collectors.toMap(StudentClass::getId, StudentClass::getCid));

        List<StudentVo> studentVoList = studentList.stream().map((student) -> {
            StudentVo studentVo = new StudentVo();
            BeanUtils.copyProperties(student, studentVo);
            studentVo.setCid(classMap.get(student.getCid()));
            return studentVo;
        }).collect(Collectors.toList());

        return studentVoList;
    }

    /**
     * 校验
     *
     * @param student 学生信息
     * @param add     是否为创建校验
     */
    @Override
    public void validStudent(Student student, boolean add) {
        if (student == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String sid = student.getSid();
        String name = student.getName();
        Integer sex = student.getSex();
        Integer age = student.getAge();
        Integer major = student.getMajor();
        Integer cid = student.getCid();
        Integer grade = student.getGrade();
        List<StudentClass> classList = studentClassMapper.selectList(null);
        Map<Integer, String> classMap = classList.stream().collect(Collectors.toMap(StudentClass::getId, StudentClass::getCid));
        // 判断是否为新增操作
        if (add) {
            if (StringUtils.isAnyBlank(name, sid) || ObjectUtils.isNull(sex, age, cid, major, grade)) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "数据含空");
            }
        }
        if (SpecialUtil.isSpecialChar(sid)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "学号不符合要求");
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
        if (cid != null && !classMap.containsKey(cid)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "班级不符合要求");
        }
    }
}




