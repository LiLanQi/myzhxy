package com.atguigu.myzhxy.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/llq")
public class TestController {

    @RequestMapping("/hello")
    public String hello(){
        return "student";
    }
}
