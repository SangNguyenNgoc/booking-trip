package org.example.profile.api.services;

import lombok.RequiredArgsConstructor;
import org.example.profile.api.dtos.ProfileResponse;
import org.example.profile.api.dtos.RegisterRequest;
import org.example.profile.api.dtos.UpdateProfileRequest;
import org.example.profile.api.dtos.UserDto;
import org.example.profile.api.entities.Profile;
import org.example.profile.api.interfaces.ProfileMapper;
import org.example.profile.api.interfaces.ProfileRepository;
import org.example.profile.untils.dtos.ListResponse;
import org.example.profile.untils.exception.AppException;
import org.example.profile.untils.exception.DataNotFoundException;
import org.example.profile.untils.exception.InputInvalidException;
import org.example.profile.untils.services.ObjectsValidator;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public interface ProfileService {
    ProfileResponse getProfile();
    ListResponse<ProfileResponse> getAllProfile();
    ProfileResponse updateInfo(UpdateProfileRequest updateProfileRequest);
    String register(RegisterRequest registerRequest);

}
@Service
@RequiredArgsConstructor
class ProfileServiceImpl implements ProfileService{

    private final ProfileRepository profileRepository;
    private final ProfileMapper mapper;
    private final ObjectsValidator<UpdateProfileRequest> updateProfileRequestObjectsValidator;
    private final ObjectsValidator<RegisterRequest> registerRequestObjectsValidator;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    public String register(RegisterRequest registerRequest) {
        registerRequestObjectsValidator.validate(registerRequest);

        if (profileRepository.existsByEmail(registerRequest.getEmail())){
            throw new AppException("Email taken!", HttpStatus.CONFLICT, List.of("Email already exists"));
        }
        if (!isPasswordConfirmed(registerRequest)) {
            throw new InputInvalidException(List.of("Invalid confirming password"));
        }
        Profile newProfile = profileRepository.save(Profile.builder()
                .email(registerRequest.getEmail())
                .fullname(registerRequest.getFullname())
                .build()
        );
        UserDto newUser = UserDto.builder()
                .profileId(newProfile.getId())
                .password(registerRequest.getPassword())
                .username(registerRequest.getUsername())
                .build();
        kafkaTemplate.send("createAccount", newUser);
        return "success";
    }

    @Override
    public ProfileResponse getProfile() {
        return mapper.toResponseDto(getProfileByAuth());
    }

    @Override
    public ListResponse<ProfileResponse> getAllProfile() {
        var profiles = profileRepository.findAll();
        return ListResponse.<ProfileResponse>builder()
                .size(profiles.size())
                .data(profiles.stream().map(mapper::toResponseDto).collect(Collectors.toList()))
                .build();
    }

    @Override
    @Transactional
    public ProfileResponse updateInfo(UpdateProfileRequest updateProfileRequest) {
        updateProfileRequestObjectsValidator.validate(updateProfileRequest);
        Profile profile = getProfileByAuth();
        Profile profileUpdate = mapper.partialUpdate(updateProfileRequest, profile);
        return mapper.toResponseDto(profileUpdate);
    }

    private Profile getProfileByAuth(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Jwt jwt = (Jwt) authentication.getPrincipal();
        String userId = jwt.getClaim("sub");
        return profileRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException(List.of("User not found")));
    }

    private boolean isPasswordConfirmed(RegisterRequest registerRequest) {
        return registerRequest.getPassword().equals(registerRequest.getConfirmPassword());
    }
}
