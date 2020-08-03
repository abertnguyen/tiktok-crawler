package com.thai.tiktokcrawler.tiktok;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class TiktokApplication {

    public static void main(String[] args) {
        SpringApplication.run(TiktokApplication.class, args);
    }

}
