package com.example.appleshop.controller;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class StaticPageController {

    @GetMapping("/login")
    public String login() {
        return "forward:/login.html"; // forward tới static file
    }

    @GetMapping("/TrangChu")
    public String home() {
        return "forward:/TrangChu.html"; // forward tới static file
    }
}

