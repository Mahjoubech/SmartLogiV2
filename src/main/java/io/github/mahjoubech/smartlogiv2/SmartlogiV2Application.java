package io.github.mahjoubech.smartlogiv2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SmartlogiV2Application {

    public static void main(String[] args) {
        SpringApplication.run(SmartlogiV2Application.class, args);
    }

}
