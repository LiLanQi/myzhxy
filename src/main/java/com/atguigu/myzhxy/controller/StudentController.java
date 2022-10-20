package com.atguigu.myzhxy.controller;


import com.atguigu.myzhxy.pojo.Student;
import com.atguigu.myzhxy.service.StudentService;
import com.atguigu.myzhxy.util.MD5;
import com.atguigu.myzhxy.util.Result;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.api.R;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.models.auth.In;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "学生控制器")
@RestController
@RequestMapping("/sms/studentController")
public class StudentController {

    @Autowired
    StudentService studentService;

    @ApiOperation("分页带条件查询学生信息")
    @GetMapping("getStudentByOpr/{pageNo}/{pageSize}")
    public Result getStudentByOpr(
            @ApiParam("页码数") @PathVariable("pageNo") int pageNo,
            @ApiParam("页大小") @PathVariable("pageSize") int pageSize,
            @ApiParam("查询的条件") Student student
            ){
        Page<Student> Page = new Page<>(pageNo, pageSize);
        IPage<Student> page = studentService.getStudentByOpr(Page, student);
        return Result.ok(page);
    }

    @ApiOperation("删除单个或者多个学生信息")
    @DeleteMapping("/delStudentById")
    public Result delStudentById(
            @ApiParam("要删除的学生编号的JSON数组") @RequestBody List<Integer> ids
    ){
        studentService.removeByIds(ids);
        return Result.ok();
    }

    @PostMapping("/addOrUpdateStudent")
    public Result addOrUpdateStudent(
            @ApiParam("要保存或修改的学生JSON") @RequestBody Student student
    ){
        Integer id = student.getId();
        if(null == id) {
            student.setPassword(MD5.encrypt(student.getPassword()));
        }
        studentService.saveOrUpdate(student);
        return Result.ok();
    }

}
