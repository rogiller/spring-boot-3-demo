package com.springboot3.springboot3demo;

public class InfoController {

    record Info (String jvmVersion) {}

    public Info info(){

        return new Info(System.getProperty("java.version"));
    }

}
