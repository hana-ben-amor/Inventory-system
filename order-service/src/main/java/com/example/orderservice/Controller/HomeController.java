package com.example.orderservice.Controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {


    @GetMapping("/actuator/info")
    public String home_info()
    {
        return "<h1>Orders management service is Running !</h1>";
    }

    @GetMapping("/")
    public String home()
    {
        return "Orders management service is Running !";
    }

}
