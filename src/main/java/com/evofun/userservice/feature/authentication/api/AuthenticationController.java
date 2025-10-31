package com.evofun.userservice.feature.authentication.api;

import com.evofun.userservice.feature.authentication.app.AuthenticationUseCase;
import com.evofun.userservice.feature.authentication.api.response.AuthResponse;
import com.evofun.userservice.feature.authentication.api.request.AuthRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/user/authentication")
public class AuthenticationController {
    private final AuthenticationUseCase authenticationUseCase;

    public AuthenticationController(AuthenticationUseCase authenticationUseCase) {
        this.authenticationUseCase = authenticationUseCase;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
        AuthResponse jwt = authenticationUseCase.login(request);

        return ResponseEntity.ok(jwt);
    }
}