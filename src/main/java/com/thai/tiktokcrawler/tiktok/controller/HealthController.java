package com.thai.tiktokcrawler.tiktok.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class HealthController {
    @GetMapping("/health")
    public String checkHealth() {
        return "OK";
    }
}
