package org.example.profile.api.controllers;

import lombok.RequiredArgsConstructor;
import org.example.profile.api.dtos.ProfileResponse;
import org.example.profile.api.dtos.RegisterRequest;
import org.example.profile.api.dtos.UpdateProfileRequest;
import org.example.profile.api.services.ProfileService;
import org.example.profile.utils.dtos.ListResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/profiles")
@RequiredArgsConstructor
public class ProfileController {
    private final ProfileService profileService;

    @PostMapping
    public ResponseEntity<String> register(@RequestBody RegisterRequest registerRequest) {
        return ResponseEntity.ok(profileService.register(registerRequest));
    }

    @GetMapping("/info")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ProfileResponse> getProfile() {
        return ResponseEntity.ok(profileService.getProfile());
    }

    @GetMapping
    @PreAuthorize("hasAuthority('SCOPE_ROLE_ADMIN')")
    public ResponseEntity<ListResponse<ProfileResponse>> getAllProfile() {
        return ResponseEntity.ok(profileService.getAllProfile());
    }

    @PutMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ProfileResponse> updateProfile(@RequestBody UpdateProfileRequest updateProfileRequest) {
        return ResponseEntity.ok(profileService.updateInfo(updateProfileRequest));
    }

}
