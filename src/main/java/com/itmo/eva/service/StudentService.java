package com.itmo.eva.service;

import com.itmo.eva.model.dto.student.StudentAddRequest;
import com.itmo.eva.model.dto.student.StudentUpdateRequest;
import com.itmo.eva.model.entity.Student;
import com.baomidou.mybatisplus.extension.service.IService;
import com.itmo.eva.model.vo.classes.ClassVo;
import com.itmo.eva.model.vo.StudentVo;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @author chenjiahan
 * @description 针对表【e_student(学生表)】的数据库操作Service
 * @createDate 2023-01-22 10:14:33
 */
public interface StudentService extends IService<Student> {
    /**
     * 添加学生
     *
     * @param studentAddRequest 学生请求体
     * @return 添加成功
     */
    Boolean addStudent(StudentAddRequest studentAddRequest);

    /**
     * 删除学生
     *
     * @param id 删除id
     * @return 删除成功
     */
    Boolean deleteStudent(Long id);

    /**
     * 更新学生
     *
     * @param studentUpdateRequest 更新请求体
     * @return 更新成功
     */
    Boolean updateStudent(StudentUpdateRequest studentUpdateRequest);

    /**
     * 根据 id 获取
     *
     * @param id id
     * @return 学生信息
     */
    StudentVo getStudentById(Long id);

    /**
     * 获取学生列表
     */
    List<StudentVo> listStudent();

    /**
     * Excel批量插入
     * @param file excel
     * @return 插入成功
     */
    Boolean excelImport(MultipartFile file);


    /**
     * 校验
     *
     * @param student 学生信息
     * @param add     是否为创建校验
     */
    void validStudent(Student student, boolean add);



    ClassVo getStudentClass();
}
