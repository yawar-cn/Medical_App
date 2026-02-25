package com.medapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;
import com.medapp.config.AppProperties;
import com.medapp.config.JwtProperties;

@EnableAsync
@EnableCaching
@EnableConfigurationProperties({AppProperties.class, JwtProperties.class})
@SpringBootApplication
public class MedicalApplication {

    public static void main(String[] args) {
        SpringApplication.run(MedicalApplication.class, args);
    }
}
