package com.zhw.controller;

import org.springframework.stereotype.*;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/excel")
public class ExcelController {

    @RequestMapping("upload")
    public void upload(HttpServletRequest request) {

    }
}
