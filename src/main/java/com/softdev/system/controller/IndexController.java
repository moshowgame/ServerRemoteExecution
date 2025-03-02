package com.softdev.system.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class IndexController {
    /**
     * 首页
     */
    @GetMapping("/")
    public Object indexPage(){
        return "hello world - https://zhengkai.blog.csdn.net/";
    }

}
