package com.atguigu.myzhxy.controller;


import com.atguigu.myzhxy.pojo.Grade;
import com.atguigu.myzhxy.service.GradeService;
import com.atguigu.myzhxy.util.Result;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "年级控制器")
@RestController
@RequestMapping("/sms/gradeController")
public class GradeController {
    @Autowired
    GradeService gradeService;

    @ApiOperation("新增或修改grade,有id属性是修改,没有则是增加")
    @PostMapping("/saveOrUpdateGrade")
    public Result saveOrUpdateGrade(@ApiParam("JSON的Grade对象") @RequestBody Grade grade){
        // 接收参数
        // 调用服务层方法完成增减或者修改
        gradeService.saveOrUpdate(grade);
        return Result.ok();
    }

    @ApiOperation("删除Grade信息")
    @DeleteMapping("/deleteGrade")
    public Result deleteGrade(@ApiParam("要删除的所有的grade的id的JSON集合") @RequestBody List<Integer> ids){
        gradeService.removeByIds(ids);
        return Result.ok();
    }

    @ApiOperation("根据年级名称模糊查询,带分页")
    @GetMapping("/getGrades/{pageNo}/{pageSize}")
    public Result getGrades(
            @ApiParam("分页查询的页码数") @PathVariable("pageNo") int pageNo,
            @ApiParam("分页查询的页大小") @PathVariable("pageSize") int pageSize,
            @ApiParam("分页查询模糊匹配的名称") String gradeName
    ){
        //分页，带条件查询
        Page<Grade> page = new Page<>(pageNo, pageSize);
        //通过服务层
        IPage<Grade> pageRs=gradeService.getGradeByOpr(page,gradeName);
        System.out.println("pageRs====================" + pageRs);
        //封装Result并返回
        return Result.ok(pageRs);
    }
    @GetMapping("/getGrades")
    public Result getGrades(){
        List<Grade> list = gradeService.getGrades();
        return Result.ok(list);
    }
}
