package at.bif.swen.rest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import at.bif.swen.rest.config.StorageProperties;


@SpringBootApplication
@EnableConfigurationProperties(StorageProperties.class)
public class RestApplication {

    public static void main(String[] args) {
        SpringApplication.run(RestApplication.class, args);
    }

}
