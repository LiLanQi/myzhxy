package com.atguigu.myzhxy.controller;


import com.atguigu.myzhxy.pojo.Admin;
import com.atguigu.myzhxy.pojo.LoginForm;
import com.atguigu.myzhxy.pojo.Student;
import com.atguigu.myzhxy.pojo.Teacher;
import com.atguigu.myzhxy.service.AdminService;
import com.atguigu.myzhxy.service.StudentService;
import com.atguigu.myzhxy.service.TeacherService;
import com.atguigu.myzhxy.util.*;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.api.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.naming.factory.ResourceLinkFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@Api(tags = "系统控制器")
@RestController
@RequestMapping("/sms/system")
public class SystemController {

    @Autowired
    AdminService adminService;
    @Autowired
    StudentService studentService;
    @Autowired
    TeacherService teacherService;

    @ApiOperation("更新用户密码的处理器")
    @PostMapping("/updatePwd/{oldPwd}/{newPwd}")
    public Result updatePwd(
            @ApiParam("token口令") @RequestHeader("token") String token,
            @ApiParam("旧密码") @PathVariable("oldPwd") String oldPwd,
            @ApiParam("新密码") @PathVariable("newPwd") String newPwd
    ){
        boolean expiration = JwtHelper.isExpiration(token);
        if (expiration) {
            // token过期
            return Result.fail().message("token失效,请重新登录后修改密码");
        }
        // 获取用户ID和用类型
        Long userId = JwtHelper.getUserId(token);
        Integer userType = JwtHelper.getUserType(token);

        oldPwd= MD5.encrypt(oldPwd);
        newPwd= MD5.encrypt(newPwd);

        switch (userType) {
            case 1:
                QueryWrapper<Admin> queryWrapper1=new QueryWrapper<>();
                queryWrapper1.eq("id",userId.intValue());
                queryWrapper1.eq("password",oldPwd);
                Admin admin =adminService.getOne(queryWrapper1);
                if (admin != null){
                    // 修改
                    admin.setPassword(newPwd);
                    adminService.saveOrUpdate(admin);
                }else{
                    return Result.fail().message("原密码有误!");
                }
                break;

            case 2:
                QueryWrapper<Student> queryWrapper2=new QueryWrapper<>();
                queryWrapper2.eq("id",userId.intValue());
                queryWrapper2.eq("password",oldPwd);
                Student student =studentService.getOne(queryWrapper2);
                if (student != null){
                    // 修改
                    student.setPassword(newPwd);
                    studentService.saveOrUpdate(student);
                }else{
                    return Result.fail().message("原密码有误!");
                }
                break;
            case 3:
                QueryWrapper<Teacher> queryWrapper3=new QueryWrapper<>();
                queryWrapper3.eq("id",userId.intValue());
                queryWrapper3.eq("password",oldPwd);
                Teacher teacher =teacherService.getOne(queryWrapper3);
                if (teacher != null){
                    // 修改
                    teacher.setPassword(newPwd);
                    teacherService.saveOrUpdate(teacher);
                }else{
                    return Result.fail().message("原密码有误!");
                }
                break;

        }
        return Result.ok();
    }

    @ApiOperation("文件上传统一入口")
    @PostMapping("/headerImgUpload")
    public Result headerImgUpload(
            @RequestPart("multipartFile") MultipartFile multipartFile
    ){
        String uuid = UUID.randomUUID().toString().replace("-", "").toLowerCase();
        String name = multipartFile.getOriginalFilename();
        name = uuid + name.substring(name.lastIndexOf("."));

        String portraitPath = "D:/myzhxy/target/classes/public/upload/" + name;
        try {
            multipartFile.transferTo(new File(portraitPath));
        } catch (IOException e) {
            e.printStackTrace();
        }
        String path = "upload/" + name;
        return Result.ok(path);
    }

    @GetMapping("/getInfo")
    public Result getInfoByToken(@RequestHeader("token") String token){
        boolean expiration = JwtHelper.isExpiration(token);
        if(expiration) {
            Result.build(null, ResultCodeEnum.TOKEN_ERROR);
        }
        //从token中解析出 用户id 和 用户的类型
        Long userId = JwtHelper.getUserId(token);
        Integer userType = JwtHelper.getUserType(token);
        Map<String,Object> map =new LinkedHashMap<>();
        switch(userType){
            case 1:
                Admin admin = adminService.getAdminById(userId);
                map.put("userType", 1);
                map.put("user", admin);
                break;
            case 2:
                Student student = studentService.getStudentById(userId);
                map.put("userType", 2);
                map.put("user", student);
                break;
            case 3:
                Teacher teacher = teacherService.getTeacherById(userId);
                map.put("userType", 3);
                map.put("user", teacher);
                break;
        }
        return Result.ok(map);
    }

    @PostMapping("/login")
    public Result login(@RequestBody LoginForm loginForm, HttpServletRequest request){
        //验证码校验
        String sessionVerifiCode = (String) request.getSession().getAttribute("verifiCode");
        String loginVerifiCode = loginForm.getVerifiCode();

        if("".equals(sessionVerifiCode) || null == sessionVerifiCode) {
            return Result.fail().message("验证码失效,请刷新后重试");
        }
        if(!sessionVerifiCode.equalsIgnoreCase(loginVerifiCode)) {
            return Result.fail().message("验证码有误,请小心输入后重试");
        }
        //移除现有验证码
        request.getSession().removeAttribute("verifiCode");
        //分用户进行校验
        Map<String, Object> map = new LinkedHashMap();
        switch(loginForm.getUserType()){
            case 1:
                try {
                    Admin admin = adminService.login(loginForm);
                    if(null != admin) {
                        map.put("token", JwtHelper.createToken(admin.getId().longValue(), 1));
                    } else {
                        throw new RuntimeException("用户名或者密码有误");
                    }
                    return Result.ok(map);
                } catch (RuntimeException e) {
                    e.printStackTrace();
                    return Result.fail().message(e.getMessage());
                }

            case 2:
                try {
                    Student student = studentService.login(loginForm);
                    if(null != student) {
                        map.put("token", JwtHelper.createToken(student.getId().longValue(), 2));
                    } else {
                        throw new RuntimeException("用户名或者密码有误");
                    }
                    return Result.ok(map);
                } catch (RuntimeException e) {
                    e.printStackTrace();
                    return Result.fail().message(e.getMessage());
                }
            case 3:
                try {
                    Teacher teacher = teacherService.login(loginForm);
                    if(null != teacher) {
                        map.put("token", JwtHelper.createToken(teacher.getId().longValue(), 3));
                    } else {
                        throw new RuntimeException("用户名或者密码有误");
                    }
                    return Result.ok(map);
                } catch (RuntimeException e) {
                    e.printStackTrace();
                    return Result.fail().message(e.getMessage());
                }

        }
        return Result.fail().message("查无此用户");
    }



    @RequestMapping("/getVerifiCodeImage")
    public void getVerifiCodeImage(HttpServletRequest request, HttpServletResponse response){
        //获取图片
        BufferedImage verifiCodeImage = CreateVerifiCodeImage.getVerifiCodeImage();
        //获取图片上的验证码
        String verifiCode = new String(CreateVerifiCodeImage.getVerifiCode());

        //将验证码文本放入session，为下一次验证做准备
        HttpSession session = request.getSession();
        session.setAttribute("verifiCode", verifiCode);

        //将验证码图片响应给浏览器
        try {
            ImageIO.write(verifiCodeImage, "JPEG", response.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
