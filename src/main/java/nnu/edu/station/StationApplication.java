package nnu.edu.station;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@MapperScan("nnu.edu.station.dao")
public class StationApplication {

    public static void main(String[] args) {
        SpringApplication.run(StationApplication.class, args);
    }

}
