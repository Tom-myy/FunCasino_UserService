package com.evofun.userservice.feature.profile.api;

import com.evofun.userservice.feature.profile.api.request.UpdateProfileRequest;
import com.evofun.userservice.feature.profile.api.response.ProfileResponse;
import com.evofun.userservice.feature.profile.app.ProfileUseCase;
import com.evofun.userservice.shared.security.jwt.JwtUserPrincipal;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@SecurityRequirement(name = "bearerAuth")
@RequestMapping("/api/v1/user/profile")
public class ProfileController {
    private final ProfileUseCase profileUseCase;

    public ProfileController(ProfileUseCase profileUseCase) {
        this.profileUseCase = profileUseCase;
    }

    @GetMapping("/me")
    public ResponseEntity<ProfileResponse> getMyProfile(
            @AuthenticationPrincipal JwtUserPrincipal principal) {

        ProfileResponse profile = profileUseCase.getProfile(principal.getUserId());
        return ResponseEntity.ok(profile);
    }

    @PatchMapping("/update")
    public ResponseEntity<ProfileResponse> updateMyProfile(
            @AuthenticationPrincipal JwtUserPrincipal principal,
            @Valid @RequestBody UpdateProfileRequest request) {

        ProfileResponse updated = profileUseCase.updateProfile(principal.getUserId(), request);
        return ResponseEntity.ok(updated);
    }
}