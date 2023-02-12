package com.springboot3.springboot3demo;

import org.apache.commons.lang3.time.DurationFormatUtils;
import org.springframework.boot.SpringBootVersion;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.management.ManagementFactory;

@RestController
public class InfoController {

    static final String UPTIME_DURATION_FORMAT = "d' day, 'H' hour, 'm' min, 's' sec'";

    record Info (String jvmId, String jvmVersion, String jvmVendor, String springBootVersion, String uptime) {}

    @GetMapping("/info")
    public Info info(){

        return new Info( ManagementFactory.getRuntimeMXBean().getName(),
                System.getProperty("java.version"),
                System.getProperty("java.vm.vendor"),
                SpringBootVersion.getVersion(),
                getUptimeString(ManagementFactory.getRuntimeMXBean().getUptime()));
    }

    static String getUptimeString(long uptime) {
        return DurationFormatUtils.formatDuration(uptime, UPTIME_DURATION_FORMAT);
    }

}
