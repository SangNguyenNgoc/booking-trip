package org.example.profile.api.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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

    @Operation(
            summary = "Get logged-in account's information",
            description = "The endpoint allows a logged-in account to retrieve information. ",
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @GetMapping("/info")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ProfileResponse> getProfile() {
        return ResponseEntity.ok(profileService.getProfile());
    }

    @Operation(
            summary = "Get all user's information",
            description = "This endpoint allows administrators get all user's information ",
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @GetMapping
    @PreAuthorize("hasAuthority('SCOPE_ROLE_ADMIN')")
    public ResponseEntity<ListResponse<ProfileResponse>> getAllProfile() {
        return ResponseEntity.ok(profileService.getAllProfile());
    }

    @Operation(
            summary = "Update logged-in account's information",
            description = "This endpoint allows logged-in account update information ",
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @PutMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ProfileResponse> updateProfile(@RequestBody UpdateProfileRequest updateProfileRequest) {
        return ResponseEntity.ok(profileService.updateInfo(updateProfileRequest));
    }

}
