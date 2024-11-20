package org.example.profile.api.services;

import com.fasterxml.jackson.core.type.TypeReference;
import lombok.RequiredArgsConstructor;
import org.example.profile.api.dtos.*;
import org.example.profile.api.entities.Profile;
import org.example.profile.api.interfaces.ProfileMapper;
import org.example.profile.api.interfaces.ProfileRepository;
import org.example.profile.utils.dtos.ListResponse;
import org.example.profile.utils.exception.AppException;
import org.example.profile.utils.exception.DataNotFoundException;
import org.example.profile.utils.exception.InputInvalidException;
import org.example.profile.utils.services.AppUtils;
import org.example.profile.utils.services.ObjectsValidator;
import org.example.profile.utils.services.RedisService;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

public interface ProfileService {
    ProfileResponse getProfile();
    ListResponse<ProfileResponse> getAllProfile();
    ProfileResponse updateInfo(UpdateProfileRequest updateProfileRequest);
    String register(RegisterRequest registerRequest);
    void handleAccountCreatedError(AccountCreatedError error);
    public String registerEmployee(RegisterEmployeeRequest registerRequest);

}
@Service
@RequiredArgsConstructor
class ProfileServiceImpl implements ProfileService{

    private final ProfileRepository profileRepository;
    private final ProfileMapper mapper;
    private final ObjectsValidator<UpdateProfileRequest> updateProfileRequestObjectsValidator;
    private final ObjectsValidator<RegisterRequest> registerRequestObjectsValidator;
    private final ObjectsValidator<RegisterEmployeeRequest> registerEmployeeRequestObjectsValidator;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final RedisService<ProfileCreated> profileCreatedRedisService;

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
                .type("Customer")
                .phoneNumber(registerRequest.getPhoneNumber())
                .build()
        );
        AccountCreated newUser = AccountCreated.builder()
                .profileId(newProfile.getId())
                .password(registerRequest.getPassword())
                .username(registerRequest.getEmail())
                .RoleId(2)
                .fullName(registerRequest.getFullname())
                .build();
        kafkaTemplate.send("createAccount", newUser);
        return "success";
    }

    @Override
    public String registerEmployee(RegisterEmployeeRequest registerRequest) {
        registerEmployeeRequestObjectsValidator.validate(registerRequest);

        if (profileRepository.existsByEmail(registerRequest.getEmail())){
            throw new AppException("Email taken!", HttpStatus.CONFLICT, List.of("Email already exists"));
        }
        Profile newProfile = profileRepository.save(Profile.builder()
                .email(registerRequest.getEmail())
                .fullname(registerRequest.getFullname())
                .type("Employee")
                .build()
        );
        AccountCreated newUser = AccountCreated.builder()
                .profileId(newProfile.getId())
                .password(AppUtils.generatePassword())
                .username(registerRequest.getEmail())
                .RoleId(3)
                .fullName(registerRequest.getFullname())
                .build();
        kafkaTemplate.send("createAccount", newUser);
        return "success";
    }

    @Override
    @Transactional
    @KafkaListener(
            topics = "AccountCreatedFailed",
            id = "AccountCreatedErrorGroup"
    )
    public void handleAccountCreatedError(AccountCreatedError error) {
        var profile = profileRepository.findById(error.getProfileId());
        profile.ifPresent(profileRepository::delete);
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

    @KafkaListener(
            topics = "AccountCreatedGG" ,
            id = "profile-consumer-group"
    )
    public void createProfile(ProfileCreated profileCreated){
       profileRepository.save(Profile.builder()
                .email(profileCreated.getEmail())
                .fullname(profileCreated.getFullName())
                .type("Customer")
                .build()
       );
    }

    private Profile getProfileByAuth(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Jwt jwt = (Jwt) authentication.getPrincipal();
        String userId = jwt.getClaim("sub");
        var profile = profileRepository.findById(userId);
        if (profile.isEmpty()) {
            var redisProfile = profileCreatedRedisService.getValue(userId, new TypeReference<ProfileCreated>() {});
            if(redisProfile == null) throw new DataNotFoundException(List.of("User not found"));
            profileCreatedRedisService.deleteKeysWithPrefix(userId);
            profile.orElseGet(() -> mapper.toEntity(redisProfile));
        }
        return profile.orElseThrow(() -> new DataNotFoundException(List.of("User not found")));
    }

    private boolean isPasswordConfirmed(RegisterRequest registerRequest) {
        return registerRequest.getPassword().equals(registerRequest.getConfirmPassword());
    }
}
