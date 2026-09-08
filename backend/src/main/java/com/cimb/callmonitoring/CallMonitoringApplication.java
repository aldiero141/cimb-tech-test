package com.cimb.callmonitoring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CallMonitoringApplication {

    public static void main(String[] args) {
        SpringApplication.run(CallMonitoringApplication.class, args);
    }
}
