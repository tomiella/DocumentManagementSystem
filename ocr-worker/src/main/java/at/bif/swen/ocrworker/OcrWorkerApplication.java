package at.bif.swen.ocrworker;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableRabbit
public class OcrWorkerApplication {
    public static void main(String[] args) {
        SpringApplication.run(OcrWorkerApplication.class, args);
    }
}
