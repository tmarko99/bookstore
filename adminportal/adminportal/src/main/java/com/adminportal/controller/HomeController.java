package com.adminportal.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HomeController {
    @RequestMapping("/")
    public String index(){
        return "redirect:book/bookList";
    }

    @RequestMapping("/home")
    public String home(){
        return "book/bookList";
    }

    @RequestMapping("/login")
    public String login(){
        return "login";
    }

}
