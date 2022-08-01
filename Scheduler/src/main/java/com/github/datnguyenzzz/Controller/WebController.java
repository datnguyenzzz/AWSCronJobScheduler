package com.github.datnguyenzzz.Controller;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
@EnableAutoConfiguration
public class WebController {

    @GetMapping
    public String index() {
        return "Okay!!";
    }
}