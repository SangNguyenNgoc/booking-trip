package org.example.profile.api.dtos;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDto {
    private String profileId;
    private String username;
    private String password;
}
