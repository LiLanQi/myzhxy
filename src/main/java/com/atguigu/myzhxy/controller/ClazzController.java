package com.atguigu.myzhxy.controller;


import com.atguigu.myzhxy.pojo.Clazz;
import com.atguigu.myzhxy.service.ClazzService;
import com.atguigu.myzhxy.util.Result;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.api.R;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "班级管理器")
@RestController
@RequestMapping("/sms/clazzController")
public class ClazzController {

    @Autowired
    ClazzService clazzService;

    @ApiOperation("分页带条件查询班级信息")
    @GetMapping("/getClazzsByOpr/{pageNo}/{pageSize}")
    public Result getClazzsByOpr(
            @ApiParam("分页查询的页码数") @PathVariable("pageNo") int pageNo,
            @ApiParam("分页查询页大小") @PathVariable("pageSize") int pageSize,
            @ApiParam("分页查询的查询条件") Clazz clazz){
        Page<Clazz> page = new Page<>(pageNo, pageSize);
        IPage<Clazz> iPage = clazzService.getClazzsByOpr(page, clazz);
        return Result.ok(iPage);
    }



    @ApiOperation("增加或修改班级信息")
    @PostMapping("/saveOrUpdateClazz")
    public Result saveOrUpdateClazz(@ApiParam("JSON格式的班级信息") @RequestBody Clazz clazz){
        clazzService.saveOrUpdate(clazz);
        return Result.ok();
    }

    @ApiOperation("删除班级信息")
    @DeleteMapping("/deleteClazz")
    public Result deleteClazz(@RequestBody List<Integer> ids){
        clazzService.removeByIds(ids);
        return Result.ok();
    }

    @ApiOperation("查询所有班级信息")
    @GetMapping("/getClazzs")
    public Result getClazzs(){
        List<Clazz> clazzes=clazzService.getClazzs();
        return Result.ok(clazzes);
    }
}
