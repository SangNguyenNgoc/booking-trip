package org.example.profile.api.interfaces;

import org.example.profile.api.dtos.ProfileCreated;
import org.example.profile.api.dtos.ProfileResponse;
import org.example.profile.api.entities.Profile;
import org.example.profile.api.dtos.UpdateProfileRequest;
import org.mapstruct.*;

import java.util.Optional;

@Mapper(componentModel = "spring")
public interface ProfileMapper {
    Profile toEntity(ProfileResponse profileResponse);

    ProfileResponse toResponseDto(Profile profile);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Profile partialUpdate(ProfileResponse profileResponse, @MappingTarget Profile profile);

    Profile toEntity(UpdateProfileRequest updateProfileRequest);

    UpdateProfileRequest toUpdateDto(Profile profile);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Profile partialUpdate(UpdateProfileRequest updateProfileRequest, @MappingTarget Profile profile);

    Optional<Profile> toEntity(ProfileCreated profileCreated);
}