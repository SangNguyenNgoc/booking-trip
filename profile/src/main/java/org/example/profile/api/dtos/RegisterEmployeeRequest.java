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
public class RegisterEmployeeRequest implements Serializable {
    @Email(message = "Email invalid")
    private String email;
    @NotBlank(message = "fullname must not be blank")
    private String fullname;
}
