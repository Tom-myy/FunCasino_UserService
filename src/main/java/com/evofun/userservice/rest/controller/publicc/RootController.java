package com.evofun.userservice.rest.controller.publicc;

import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;
@Hidden
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
