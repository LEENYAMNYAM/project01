package com.example.pro.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class sampleController {

    @GetMapping("/")
    public String sample() {
        return "/layout/layout";
    }

}
