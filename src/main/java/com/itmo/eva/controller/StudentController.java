package com.itmo.eva.controller;

import com.itmo.eva.common.*;
import com.itmo.eva.exception.BusinessException;
import com.itmo.eva.model.dto.student.StudentAddRequest;
import com.itmo.eva.model.dto.student.StudentUpdateRequest;
import com.itmo.eva.model.vo.StudentVo;
import com.itmo.eva.service.StudentService;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.List;

/**
 * 学生接口
 */
@RestController
@Slf4j
@RequestMapping("/student")
public class StudentController {

    @Resource
    private StudentService studentService;

    /**
     * 添加学生
     *
     * @param studentAddRequest 添加请求体
     * @return 添加成功
     */
    @PostMapping("/add")
    public BaseResponse<Boolean> addStudent(@RequestBody StudentAddRequest studentAddRequest) {
        if (studentAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "添加参数为空");
        }
        Boolean save = studentService.addStudent(studentAddRequest);

        if (!save) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "保存数据失败！");
        }

        return ResultUtils.success(true);
    }

    /**
     * 删除学生信息
     *
     * @param deleteRequest 删除请求体
     * @return 删除成功
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteStudent(@RequestBody DeleteRequest deleteRequest) {
        Long id = deleteRequest.getId();
        if (id == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "id为空");
        }
        Boolean delete = studentService.deleteStudent(id);

        if (!delete) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "删除信息失败！");
        }

        return ResultUtils.success(true);
    }

    /**
     * 更新学生信息
     *
     * @param studentUpdateRequest 更新请求体
     * @return 更新成功
     */
    @PostMapping("/update")
    public BaseResponse<Boolean> updateStudent(@RequestBody StudentUpdateRequest studentUpdateRequest) {
        if (studentUpdateRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }
        Boolean update = studentService.updateStudent(studentUpdateRequest);

        if (!update) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "更新数据失败！");
        }

        return ResultUtils.success(true);
    }

    /**
     * 根据 id 获取
     *
     * @param idRequest id请求体
     * @return 学生信息
     */
    @PostMapping("/get")
    public BaseResponse<StudentVo> getStudentById(@RequestBody IdRequest idRequest) {
        if (idRequest == null || idRequest.getId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }
        Long id = idRequest.getId();
        StudentVo studentInfo = studentService.getStudentById(id);

        if (studentInfo == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        return ResultUtils.success(studentInfo);
    }

    /**
     * 获取列表
     *
     * @return 所有学生信息
     */
    @GetMapping("/list")
    public BaseResponse<List<StudentVo>> listStudent() {
        List<StudentVo> studentVoList = studentService.listStudent();
        if (studentVoList == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }

        return ResultUtils.success(studentVoList);
    }

    /**
     * Excel文件批量上传教师信息
     * @param file excel
     * @return 保存成功
     */
    @PostMapping("/excel/import")
    public BaseResponse<Boolean> saveExcel(@RequestParam("file") MultipartFile file) {

        Boolean isImport = studentService.excelImport(file);

        if (!isImport) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "保存文件失败！");
        }

        return ResultUtils.success(true);
    }

    /**
     * 下载示例Excel
     * @param response 请求
     * @return 示例
     */
    @GetMapping("/excel/export")
    public BaseResponse<Boolean> exportExcel(HttpServletResponse response) {

        //2.建立Excel对象，封装数据
        response.setCharacterEncoding("UTF-8");
        //2.1创建Excel对象
        XSSFWorkbook wb = new XSSFWorkbook();
        //2.3创建sheet对象
        XSSFSheet sheet = wb.createSheet("学生信息表");
        //2.3创建表头
        XSSFRow xssfRow = sheet.createRow(0);
        xssfRow.createCell(0).setCellValue("姓名");
        xssfRow.createCell(1).setCellValue("学号");
        xssfRow.createCell(2).setCellValue("性别");
        xssfRow.createCell(3).setCellValue("年龄");
        xssfRow.createCell(4).setCellValue("专业");
        xssfRow.createCell(5).setCellValue("班级号");
        xssfRow.createCell(6).setCellValue("学期");

        // 建立输出流，输出浏览器文件
        OutputStream os = null;

        try {
            String folderPath = "C:\\excel";
            //创建上传文件目录
            File folder = new File(folderPath);
            //如果文件夹不存在创建对应的文件夹
            if (!folder.exists()) {
                folder.mkdirs();
            }
            //设置文件名
            String fileName = "学生信息表" + ".xlsx";
            String savePath = folderPath + File.separator + fileName;
            OutputStream fileOut = new FileOutputStream(savePath);
            wb.write(fileOut);
            fileOut.close();
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (os != null)
                    os.close();
            } catch(Exception e) {
                e.printStackTrace();
            }
        }

        return ResultUtils.success(true);
    }


}
