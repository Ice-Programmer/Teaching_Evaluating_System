package com.itmo.eva.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itmo.eva.common.ErrorCode;
import com.itmo.eva.exception.BusinessException;
import com.itmo.eva.mapper.*;
import com.itmo.eva.model.dto.evaluate.EvaluateAddRequest;
import com.itmo.eva.model.dto.evaluate.EvaluateUpdateRequest;
import com.itmo.eva.model.entity.*;
import com.itmo.eva.model.entity.System;
import com.itmo.eva.model.vo.Evaluation.EvaluateVo;
import com.itmo.eva.model.vo.Evaluation.StudentCompletionVo;
import com.itmo.eva.model.vo.Evaluation.StudentEvaVo;
import com.itmo.eva.service.EvaluateService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
* @author chenjiahan
* @description 针对表【e_evaluate(评测表)】的数据库操作Service实现
* @createDate 2023-01-23 13:14:03
*/
@Service
public class EvaluateServiceImpl extends ServiceImpl<EvaluateMapper, Evaluate>
    implements EvaluateService {

    @Resource
    private EvaluateMapper evaluateMapper;

    @Resource
    private StudentMapper studentMapper;

    @Resource
    private CourseMapper courseMapper;

    @Resource
    private SystemMapper systemMapper;

    @Resource
    private MarkHistoryMapper markHistoryMapper;

    @Resource
    private TeacherMapper teacherMapper;

    @Resource
    private StudentClassMapper studentClassMapper;

    /**
     * 添加评测
     *
     * @param evaluateAddRequest 评测请求体
     * @return 添加成功
     */
    @Override
    public Boolean addEvaluate(EvaluateAddRequest evaluateAddRequest) {
        if (evaluateAddRequest == null || StringUtils.isBlank(evaluateAddRequest.getName())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空！");
        }
        // 判断评测是否存在 【按照名称查询】
        String name = evaluateAddRequest.getName();
        Evaluate oldEvaluate = evaluateMapper.getEvaluateByName(name);
        if (oldEvaluate != null) {
            throw new BusinessException(ErrorCode.DATA_REPEAT, "已存在该测评！");
        }
        // 判断是否有正在进行的评测
        Evaluate evaluateGoing = evaluateMapper.getEvaluateByStatus();
        if (evaluateGoing != null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "已有正在进行的测评！");
        }

        Evaluate evaluate = new Evaluate();
        BeanUtils.copyProperties(evaluateAddRequest, evaluate);


        // 校验数据
        this.validEvaluate(evaluate, true);
        boolean save = this.save(evaluate);

        // 发布评测给学生
        releaseEvaluation(evaluate.getId());

        return save;
    }

    /**
     * 删除评测
     *
     * @param id 删除id
     * @return 删除成功
     */
    @Override
    public Boolean deleteEvaluate(Long id) {
        // 判断是否存在
        Evaluate oldEvaluate = this.getById(id);
        if (oldEvaluate == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "评测不存在");
        }

        boolean remove = this.removeById(id);

        return remove;
    }

    /**
     * 更新课程
     *
     * @param evaluateUpdateRequest 更新请求体
     * @return 更新成功
     */
    @Override
    public Boolean updateEvaluate(EvaluateUpdateRequest evaluateUpdateRequest) {
        if (evaluateUpdateRequest == null || evaluateUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 判断是否存在
        Integer id =  evaluateUpdateRequest.getId();
        Evaluate oldEvaluate = this.getById(id);
        if (oldEvaluate == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "评测不存在");
        }
        Evaluate evaluate = new Evaluate();
        BeanUtils.copyProperties(evaluateUpdateRequest, evaluate);

        // 参数校验
        this.validEvaluate(evaluate, false);
        boolean update = this.updateById(evaluate);

        return update;
    }

    /**
     * 根据 id 获取
     *
     * @param id id
     * @return 评测信息
     */
    @Override
    public EvaluateVo getEvaluateById(Long id) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "id错误");
        }
        Evaluate evaluate = this.getById(id);
        // 判空
        if (evaluate == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "评测信息不存在");
        }
        EvaluateVo evaluateInfo = new EvaluateVo();
        BeanUtils.copyProperties(evaluate, evaluateInfo);

        return evaluateInfo;
    }

    /**
     * 获取评测列表
     */
    @Override
    public List<EvaluateVo> listEvaluate() {
        List<Evaluate> evaluateList = this.list();
        if (evaluateList == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "没有数据记录");
        }

        List<EvaluateVo> evaluateVoList = evaluateList.stream().map((evaluate) -> {
            EvaluateVo evaluateVo = new EvaluateVo();
            BeanUtils.copyProperties(evaluate, evaluateVo);
            return evaluateVo;
        }).collect(Collectors.toList());
        return evaluateVoList;
    }

    /**
     * 获取完成学生
     *
     * @param eid 评测id
     * @return 完成学生列表
     */
    @Override
    public StudentCompletionVo listStudentCompletion(Integer eid) {
        StudentCompletionVo studentCompletionVo = new StudentCompletionVo();
        // 查找学生aid是否还有状态（state = 0）的字段在mark表中
        // 查询所有学生信息Id
        List<Integer> studentIdList = studentMapper.getStudentId();

        // 获取该评测的所有未完成学生的id
        List<Integer> studentId = markHistoryMapper.getByEidAndState(eid);

        // 判断是否存在该评测信息
        if (studentId == null || studentId.size() == 0) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }

        // 对studentId去重 -> 未完成学生id
        List<Integer> undoneStudentId = studentId.stream().distinct().collect(Collectors.toList());
        // 取差集 ->  完成学生名单
        List<Integer> doneStudentId = studentIdList.stream().filter(item -> !undoneStudentId.contains(item)).collect(Collectors.toList());

        // 取出所有班级
        Map<Integer, String> classList = studentClassMapper.selectList(null).stream().collect(Collectors.toMap(StudentClass::getId, StudentClass::getCid));

        List<StudentEvaVo> undoneStudentList = new ArrayList<>();
        for (Integer id : undoneStudentId) {
            StudentEvaVo undoneStudent = new StudentEvaVo();
            Student student = studentMapper.selectById(id);
            String studentName = student.getName();
            String classNumber = classList.get(student.getCid());
            String studentNumber = student.getSid();
            undoneStudent.setStudentId(studentNumber);
            undoneStudent.setName(studentName);
            undoneStudent.setStudentClass(classNumber);
            undoneStudentList.add(undoneStudent);
        }
        studentCompletionVo.setStudentUndone(undoneStudentList);

        // 记录完成同学
        List<StudentEvaVo> doneStudentList = new ArrayList<>();
        for (Integer id : doneStudentId) {
            StudentEvaVo doneStudent = new StudentEvaVo();
            Student student = studentMapper.selectById(id);
            String studentName = student.getName();
            String classNumber = classList.get(student.getCid());
            String studentNumber = student.getSid();
            doneStudent.setStudentId(studentNumber);
            doneStudent.setName(studentName);
            doneStudent.setStudentClass(classNumber);
            doneStudentList.add(doneStudent);
        }
        studentCompletionVo.setStudentDone(doneStudentList);

        return studentCompletionVo;
    }

    /**
     * 校验
     *
     * @param evaluate 评测信息
     * @param add     是否为创建校验
     */
    @Override
    public void validEvaluate(Evaluate evaluate, boolean add) {
        if (evaluate == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String name = evaluate.getName();
        String create_time = evaluate.getCreate_time();
        String start_time = evaluate.getStart_time();
        String e_time = evaluate.getE_time();
        if (add) {
            if (StringUtils.isAnyBlank(name, create_time, start_time, e_time)) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "数据含空");
            }
        }
    }

    /**
     * 分发学生测评
     * @param evaluateId 评测id
     */
    public void releaseEvaluation(Integer evaluateId) {
        // todo 起床记得测试
        // 取出所有学生信息
        List<Student> studentList = studentMapper.selectList(null);

        for (Student student : studentList) {
            // 取出grade和major，来查询学生的响应课程
            Integer grade = student.getGrade();
            Integer major = student.getMajor();
            Long studentId = student.getId();

            // 获取该学生的所有课程信息
            List<Course> courseList = courseMapper.getCourseByMajorAndGrade(major, grade);
            for (Course course : courseList) {
                // 取出教师id
                Long teacherId = course.getTid();
                Integer courseId = course.getId();

                // 取出教师的国籍
                Teacher teacher = teacherMapper.selectById(teacherId);
                Integer identity = teacher.getIdentity();

                // 查询教师的国籍，所对应的所有一级指标
                List<System> systemList = systemMapper.getCountByKind(identity);

                for (System system : systemList) {
                    Integer systemId = system.getId();
                    MarkHistory markHistory = new MarkHistory();
                    markHistory.setTid(teacherId.intValue());
                    markHistory.setCid(courseId);
                    markHistory.setEid(evaluateId);
                    markHistory.setScore(0);
                    markHistory.setSid(systemId);
                    markHistory.setAid(studentId.intValue());
                    markHistory.setState(0);
                    // 插入数据库
                    markHistoryMapper.insert(markHistory);
                }



            }
        }


    }

    /**
     * 在这里写一下分发逻辑 【帮助笨蛋开发者梳理逻辑】
     *
     * 学生 =》 课程         根据学生的年级（grade）和专业（major）来查找课程信息
     * 课程 =》 老师         根据课程的tid来查找教师信息
     * 老师 =》 一级指标      根据老师的国籍（identity）来确定需要的一级指标
     *
     */
}




