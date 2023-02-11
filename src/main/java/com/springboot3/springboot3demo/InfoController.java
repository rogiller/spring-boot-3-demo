package com.springboot3.springboot3demo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class InfoController {

    record Info (String jvmVersion) {}


    @GetMapping("/info")
    public Info info(){

        return new Info(System.getProperty("java.version"));
    }

}
