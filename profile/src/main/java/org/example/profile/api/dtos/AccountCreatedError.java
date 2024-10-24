package org.example.profile.api.dtos;

import lombok.*;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AccountCreatedError implements Serializable {
    private String message;
    private String profileId;
    private String email;
}
