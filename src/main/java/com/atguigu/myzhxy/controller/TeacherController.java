package com.atguigu.myzhxy.controller;
import com.atguigu.myzhxy.pojo.Teacher;
import com.atguigu.myzhxy.service.TeacherService;
import com.atguigu.myzhxy.util.MD5;
import com.atguigu.myzhxy.util.Result;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api()
@RestController
@RequestMapping("/sms/teacherController")
public class TeacherController {

    @Autowired
    TeacherService teacherService;

    @GetMapping("/getTeachers/{pageNo}/{pageSize}")
    @ApiOperation("")
    public Result getTeachers(
            @PathVariable("pageNo") int pageNo,
            @PathVariable("pageSize") int pageSize,
            Teacher teacher
    ){
        Page<Teacher> page = new Page<>(pageNo, pageSize);
        IPage<Teacher> teacherPage = teacherService.getTeacherByOpr(page, teacher);
        return Result.ok(teacherPage);
    }

    @DeleteMapping("/deleteTeacher")
    public Result deleteTeacher(
            @RequestBody List<Integer> ids
    ){
        teacherService.removeByIds(ids);
        return Result.ok();
    }

    @PostMapping("/saveOrUpdateTeacher")
    public Result saveOrUpdateTeacher(
            @RequestBody Teacher teacher
    ){
        Integer id = teacher.getId();
        if(null == id || 0 == id){
            teacher.setPassword(MD5.encrypt(teacher.getPassword()));
        }
        teacherService.saveOrUpdate(teacher);
        return Result.ok();
    }
}
