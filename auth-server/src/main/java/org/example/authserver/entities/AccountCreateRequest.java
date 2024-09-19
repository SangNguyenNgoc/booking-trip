package org.example.authserver.entities;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Value;

import java.io.Serializable;

/**
 * DTO for {@link User}
 */
@Value
public class AccountCreateRequest implements Serializable {
    @NotBlank(message = "profile Id must not be blank")
    String profileId;
    @Email(message = "Email  invalid")
    String email;
    @Pattern(message = "Password invalid", regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[\\W_]).{8,}$ ")
    @NotBlank(message = "password must not be blank")
    String password;
}