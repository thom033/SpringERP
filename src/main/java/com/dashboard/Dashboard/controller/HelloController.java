package com.dashboard.Dashboard.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HelloController {

    @GetMapping("/hello")
    public String helloWorld() {
        return "hello"; // Retourner la vue "hello" pour afficher "Hello World"
    }
}
