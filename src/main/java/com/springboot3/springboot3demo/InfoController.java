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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
    @GetMapping("/mysql")
    public List<String> mysql(){

        List<String> arrayList = new ArrayList<>();

        try {
            String dbUrl = System.getenv().get("BOOT3_DB_URL");
            String dbUser = System.getenv().get("BOOT3_DB_USER");
            String dbPw = System.getenv().get("BOOT3_DB_PW");

            Class.forName("com.mysql.jdbc.Driver");
            Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPw) ;
            Statement stmt = conn.createStatement() ;
            String query = "select location_code from st_biz_location order by location_code";
            ResultSet rs = stmt.executeQuery(query) ;
            while(rs.next()){
                arrayList.add(rs.getString(1));
            }
            return arrayList;

        }catch(Exception e){
            arrayList.add(e.getMessage());
            return arrayList;
        }
    }

    static String getUptimeString(long uptime) {
        return DurationFormatUtils.formatDuration(uptime, UPTIME_DURATION_FORMAT);
    }

}
