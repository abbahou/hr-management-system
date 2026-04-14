package com.hrplatform.iam;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class HrplatformApplication {

    public static void main(String[] args) {
        SpringApplication.run(HrplatformApplication.class, args);
    }

}
