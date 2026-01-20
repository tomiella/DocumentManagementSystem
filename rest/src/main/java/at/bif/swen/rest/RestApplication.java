package at.bif.swen.rest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;
//import at.bif.swen.rest.config.StorageProperties;
import at.bif.swen.rest.config.MinioProperties;

@SpringBootApplication
@EnableConfigurationProperties(MinioProperties.class)
@EnableScheduling
public class RestApplication {

    public static void main(String[] args) {
        SpringApplication.run(RestApplication.class, args);
    }

}
