package com.springboot3.springboot3demo;

import org.apache.commons.lang3.time.DurationFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootVersion;
import org.springframework.boot.info.BuildProperties;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.management.ManagementFactory;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;

@RestController
public class InfoController {

    @Autowired
    BuildProperties buildProperties;

    static final String UPTIME_DURATION_FORMAT = "d' day, 'H' hour, 'm' min, 's' sec'";

    record Info (String jvmId, String jvmVersion, String jvmVendor, String springBootVersion, String uptime, BuildProperties buildProperties) {}

    @GetMapping("/info")
    public Info info(){

        return new Info( ManagementFactory.getRuntimeMXBean().getName(),
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
        Vector v = new Vector();
        while (true) {
            byte [] b  = new byte[1048576]
            v.add(b)
            Runtime rt = Runtime.getRuntime();
            System.out.println("Free memory: " + rt.freeMemory());
        }
    }

    static String getUptimeString(long uptime) {
        return DurationFormatUtils.formatDuration(uptime, UPTIME_DURATION_FORMAT);
    }

}
