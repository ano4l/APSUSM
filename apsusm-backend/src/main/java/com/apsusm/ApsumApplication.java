package com.apsusm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class ApsumApplication {
    public static void main(String[] args) {
        SpringApplication.run(ApsumApplication.class, args);
    }
}
