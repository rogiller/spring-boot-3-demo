package com.springboot3.springboot3demo;

import org.apache.commons.lang3.time.DurationFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootVersion;
import org.springframework.boot.info.BuildProperties;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.lang.management.ManagementFactory;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.*;

@RestController
public class InfoController {

    private static Logger LOG = LoggerFactory.getLogger(InfoController.class);

    BuildProperties buildProperties;

    private static String containerId = UUID.randomUUID().toString();

    static final String UPTIME_DURATION_FORMAT = "d' day, 'H' hour, 'm' min, 's' sec'";

    private static Vector memoryLeak = new Vector();

    @Autowired
    InfoController(BuildProperties buildProperties){
        this.buildProperties = buildProperties;
        LOG.info("VM started with containerId: " + containerId);
    }

    record Info (String containerId, String jvmId, String jvmVersion, String jvmVendor,
                 String springBootVersion, String uptime, BuildProperties buildProperties) {}

    @GetMapping("/info")
    public Info info(){

        return new Info(containerId,
                ManagementFactory.getRuntimeMXBean().getName(),
                System.getProperty("java.version"),
                System.getProperty("java.vm.vendor"),
                SpringBootVersion.getVersion(),
                getUptimeString(ManagementFactory.getRuntimeMXBean().getUptime()),
                buildProperties);
    }

    @GetMapping("/users")
    public List<Map> mysql(){

        List<Map> arrayList = new ArrayList<>();

        try {
            String dbUrl = System.getenv().get("BOOT3_DB_URL");
            String dbUser = System.getenv().get("BOOT3_DB_USER");
            String dbPw = System.getenv().get("BOOT3_DB_PW");

            Class.forName("com.mysql.jdbc.Driver");
            Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPw) ;
            Statement stmt = conn.createStatement() ;
            String query = "select userprofileid, name, nickname, emailaddress from rh_userprofile order by name";
            ResultSet rs = stmt.executeQuery(query);
            while(rs.next()){
                Map<String, String> record = new LinkedHashMap<>();
                record.put("userId", rs.getString("userprofileid"));
                record.put("name", rs.getString("name"));
                record.put("userName", rs.getString("nickname"));
                record.put("email", rs.getString("emailaddress"));
                arrayList.add(record);
            }
            return arrayList;

        }catch(Exception e){
            Map<String, String> record = new HashMap<>();
            record.put("Error", e.getMessage());
            arrayList.add(record);
            return arrayList;
        }
    }

    @GetMapping("/eatMemory")
    public void eatMemory(){

        while (true) {
            byte [] b  = new byte[1048576];
            memoryLeak.add(b);
            Runtime rt = Runtime.getRuntime();
            LOG.info("Free memory: " + rt.freeMemory());
        }
    }

    @GetMapping("/exit")
    public void exit(){
        System.exit(0);
    }

    @GetMapping({"/fib", "/fib/", "/fib/{fib}"})
    public ResponseEntity<Map> fibonacci(
            @PathVariable(required = false) Long fib) {

        if(fib == null){
            fib = 1L;
        }

        LinkedHashMap<String, Object> map = new LinkedHashMap();

        long start = System.currentTimeMillis();

        map.put("containerId", containerId);
        map.put("fibonacciInput", fib);

        long result = slowFibonacciCompute(fib);
        LOG.info("Slow fibonacci result: {}", result);

        map.put("fibonacciResult", result);
        map.put("millisecondsTook", System.currentTimeMillis() - start);

        return ResponseEntity.ok(map);
    }

    private Long slowFibonacciCompute(long n) {
        if (n <= 1) {
            return n;
        } else {
            return slowFibonacciCompute(n - 1) + slowFibonacciCompute(n - 2);
        }
    }

    static String getUptimeString(long uptime) {
        return DurationFormatUtils.formatDuration(uptime, UPTIME_DURATION_FORMAT);
    }

}
