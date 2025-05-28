package com.evofun.userservice.rest.controller.publicc;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
public class RootController {
    @GetMapping("/")
    public Map<String, Object> home() {
        return Map.of(
                "status", "EVO_FUN Backend is running",
                "time", LocalDateTime.now()
        );
    }
}
