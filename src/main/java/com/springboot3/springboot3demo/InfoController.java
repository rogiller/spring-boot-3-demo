package com.springboot3.springboot3demo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;

@RestController
public class InfoController {

    record Info (String jvmId, String jvmVersion, String jvmVendor) {}


    @GetMapping("/info")
    public Info info(){

        RuntimeMXBean mxBean = ManagementFactory.getRuntimeMXBean();

        return new Info(mxBean.getName(),
                System.getProperty("java.version"),
                System.getProperty("java.vm.vendor"));
    }

}
