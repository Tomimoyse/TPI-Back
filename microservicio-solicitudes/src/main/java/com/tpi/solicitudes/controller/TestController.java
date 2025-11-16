package com.tpi.solicitudes.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/solicitudes")
public class TestController {
    
    @GetMapping("/test")
    public String test() {
        return "Microservicio de Solicitudes funcionando correctamente";
    }
}

