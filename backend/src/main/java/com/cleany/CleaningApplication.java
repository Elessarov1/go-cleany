package com.cleany;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class CleaningApplication {

    public static void main(String[] args) {
        SpringApplication.run(CleaningApplication.class, args);
    }
}

