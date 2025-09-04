package com.evofun.userservice.rest.controller.publicc;

import com.evofun.userservice.rest.AuthService;
import com.evofun.userservice.rest.dto.response.JwtResponse;
import com.evofun.userservice.rest.dto.response.LoginRequestDto;
import com.evofun.userservice.rest.dto.response.RegisterRequestDto;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@Valid @RequestBody LoginRequestDto request) {
        JwtResponse jwt = authService.login(request);

        return ResponseEntity.ok(jwt);
    }

    @PostMapping("/register")
    public ResponseEntity<Void> register(@Valid @RequestBody RegisterRequestDto request) {
        authService.register(request);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
