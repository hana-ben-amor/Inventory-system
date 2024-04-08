package com.example.userservice.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    @GetMapping("/actuator/info")
    public String home_info()
    {
        return "<h1>Users management service is Running !</h1>";
    }

    @GetMapping("/")
    public String home()
    {
        return "Users management service is Running !";
    }

}