package com.itmo.eva.service;

import com.itmo.eva.model.dto.teacher.TeacherAddRequest;
import com.itmo.eva.model.dto.teacher.TeacherUpdateRequest;
import com.itmo.eva.model.entity.Teacher;
import com.baomidou.mybatisplus.extension.service.IService;
import com.itmo.eva.model.vo.TeacherVo;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
* @author chenjiahan
* @description 针对表【e_teacher(教师表)】的数据库操作Service
* @createDate 2023-01-21 13:17:49
*/
public interface TeacherService extends IService<Teacher> {

    /**
     * 添加教师
     * @param teacherAddRequest 教师请求体
     * @return 添加成功
     */
    Boolean addTeacher(TeacherAddRequest teacherAddRequest);

    /**
     * 删除教师
     * @param id 删除id
     * @return 删除成功
     */
    Boolean deleteTeacher(Long id);

    /**
     * 更新教师
     * @param teacherUpdateRequest 更新请求体
     * @return 更新成功
     */
    Boolean updateTeacher(TeacherUpdateRequest teacherUpdateRequest);

    /**
     * 根据 id 获取
     * @param id id
     * @return 教师信息
     */
    TeacherVo getTeacherById(Long id);

    /**
     * 获取教师列表
     */
    List<TeacherVo> listTeacher();

    /**
     * Excel批量插入
     * @param file excel
     * @return 插入成功
     */
    Boolean excelImport(MultipartFile file);


    /**
     * 校验
     *
     * @param teacher 教师信息
     * @param add 是否为创建校验
     */
    void validTeacher(Teacher teacher, boolean add);

    /**
     * 获取所有中方教师
     *
     * @return 中方教师
     */
    List<TeacherVo> getChineseTeacher();

    /**
     * 获取所有俄方教师
     *
     * @return 俄方教师
     */
    List<TeacherVo> getRussianTeacher();
}
