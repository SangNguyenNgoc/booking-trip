package org.example.profile.api.dtos;

import lombok.*;

import java.io.Serializable;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AccountCreated implements Serializable {
    private String profileId;
    private String username;
    private String password;
    private Integer RoleId;
    private String fullName;
}
