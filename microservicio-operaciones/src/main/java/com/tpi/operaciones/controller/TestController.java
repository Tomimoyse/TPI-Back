package com.tpi.operaciones.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/operaciones/test")
public class TestController {
    @GetMapping
    public String test() {
        return "Microservicio Operaciones OK";
    }
}
