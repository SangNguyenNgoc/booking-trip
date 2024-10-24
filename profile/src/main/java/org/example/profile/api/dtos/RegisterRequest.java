package org.example.profile.api.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.io.Serializable;

@Setter
@Getter
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class RegisterRequest implements Serializable {
    @Email(message = "Email invalid")
    private String email;
    @NotBlank(message = "password must not be blank")
    @Pattern(regexp = "^[a-zA-Z0-9]{8,20}$", message = "password must be between 8 and 20 characters long")
    private String password;
    @NotBlank(message = "Please confirm the password")
    private String confirmPassword;
    @NotBlank(message = "fullname must not be blank")
    private String fullname;
    @NotBlank(message = "Phone number must not be blank")
    @Pattern(regexp = "^0[35789]\\d{8}$", message = "Phone number invalid")
    private String phoneNumber;
}
